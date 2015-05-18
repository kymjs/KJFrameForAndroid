/*
 * Copyright (c) 2014, Android Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.kjframe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.kymjs.kjframe.http.Cache;
import org.kymjs.kjframe.http.CacheDispatcher;
import org.kymjs.kjframe.http.Delivery;
import org.kymjs.kjframe.http.DownloadController;
import org.kymjs.kjframe.http.DownloadTaskQueue;
import org.kymjs.kjframe.http.FileRequest;
import org.kymjs.kjframe.http.FormRequest;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.http.JsonRequest;
import org.kymjs.kjframe.http.NetworkDispatcher;
import org.kymjs.kjframe.http.Request;
import org.kymjs.kjframe.http.Request.HttpMethod;
import org.kymjs.kjframe.utils.KJLoger;

/**
 * 本类工作流程： 每当发起一次Request，会对这个Request标记一个唯一值。<br>
 * 并加入当前请求的Set中(保证唯一;方便控制)。<br>
 * 同时判断是否启用缓存，若启用则加入缓存队列，否则加入执行队列。<br>
 * 
 * Note:<br>
 * 整个KJHttp工作流程：采用责任链设计模式，由三部分组成，类似设计可以类比Handle...Looper...MessageQueue<br>
 * 
 * 1、KJHttp负责不停向NetworkQueue(或CacheQueue实际还是NetworkQueue， 具体逻辑请查看
 * {@link CacheDispatcher})添加Request<br>
 * 2、另一边由TaskThread不停从NetworkQueue中取Request并交给Network执行器(逻辑请查看
 * {@link NetworkDispatcher} )，<br>
 * 3、Network执行器将执行成功的NetworkResponse返回给TaskThead，并通过Request的定制方法
 * {@link Request#parseNetworkResponse()}封装成Response，最终交给分发器 {@link Delivery}
 * 分发到主线程并调用HttpCallback相应的方法
 * 
 * @author kymjs (https://www.kymjs.com/)
 */
public class KJHttp {

    // 请求缓冲区
    private final Map<String, Queue<Request<?>>> mWaitingRequests = new HashMap<String, Queue<Request<?>>>();
    // 请求的序列化生成器
    private final AtomicInteger mSequenceGenerator = new AtomicInteger();
    // 当前正在执行请求的线程集合
    private final Set<Request<?>> mCurrentRequests = new HashSet<Request<?>>();
    // 执行缓存任务的队列.
    private final PriorityBlockingQueue<Request<?>> mCacheQueue = new PriorityBlockingQueue<Request<?>>();
    // 需要执行网络请求的工作队列
    private final PriorityBlockingQueue<Request<?>> mNetworkQueue = new PriorityBlockingQueue<Request<?>>();
    // 请求任务执行池
    private final NetworkDispatcher[] mTaskThreads;
    // 缓存队列调度器
    private CacheDispatcher mCacheDispatcher;
    // 配置器
    private HttpConfig mConfig;

    public KJHttp() {
        this(new HttpConfig());
    }

    public KJHttp(HttpConfig config) {
        this.mConfig = config;
        mConfig.mController.setRequestQueue(this);
        mTaskThreads = new NetworkDispatcher[HttpConfig.NETWORK_POOL_SIZE];
        start();
    }

    /**
     * 发起get请求
     * 
     * @param url
     *            地址
     * @param callback
     *            请求中的回调方法
     */
    public Request<byte[]> get(String url, HttpCallBack callback) {
        return get(url, new HttpParams(), callback);
    }

    /**
     * 发起get请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     */
    public Request<byte[]> get(String url, HttpParams params,
            HttpCallBack callback) {
        return get(url, params, true, callback);
    }

    /**
     * 发起get请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     * @param useCache
     *            是否缓存本条请求
     */
    public Request<byte[]> get(String url, HttpParams params, boolean useCache,
            HttpCallBack callback) {
        if (params != null) {
            url += params.getUrlParams();
        }
        Request<byte[]> request = new FormRequest(HttpMethod.GET, url, params,
                callback);
        request.setShouldCache(useCache);
        doRequest(request);
        return request;
    }

    /**
     * 发起post请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     */
    public Request<byte[]> post(String url, HttpParams params,
            HttpCallBack callback) {
        return post(url, params, true, callback);
    }

    /**
     * 发起post请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     * @param useCache
     *            是否缓存本条请求
     */
    public Request<byte[]> post(String url, HttpParams params,
            boolean useCache, HttpCallBack callback) {
        Request<byte[]> request = new FormRequest(HttpMethod.POST, url, params,
                callback);
        request.setShouldCache(useCache);
        doRequest(request);
        return request;
    }

    /**
     * 使用JSON传参的post请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     */
    public Request<byte[]> jsonPost(String url, HttpParams params,
            HttpCallBack callback) {
        return jsonPost(url, params, true, callback);
    }

    /**
     * 使用JSON传参的post请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     * @param useCache
     *            是否缓存本条请求
     */
    public Request<byte[]> jsonPost(String url, HttpParams params,
            boolean useCache, HttpCallBack callback) {
        Request<byte[]> request = new JsonRequest(HttpMethod.POST, url, params,
                callback);
        request.setShouldCache(useCache);
        doRequest(request);
        return request;
    }

    /**
     * 使用JSON传参的get请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     */
    public Request<byte[]> jsonGet(String url, HttpParams params,
            HttpCallBack callback) {
        Request<byte[]> request = new JsonRequest(HttpMethod.GET, url, params,
                callback);
        doRequest(request);
        return request;
    }

    /**
     * 使用JSON传参的get请求
     * 
     * @param url
     *            地址
     * @param params
     *            参数集
     * @param callback
     *            请求中的回调方法
     * @param useCache
     *            是否缓存本条请求
     */
    public Request<byte[]> jsonGet(String url, HttpParams params,
            boolean useCache, HttpCallBack callback) {
        Request<byte[]> request = new JsonRequest(HttpMethod.GET, url, params,
                callback);
        request.setShouldCache(useCache);
        doRequest(request);
        return request;
    }

    /**
     * 下载
     * 
     * @param storeFilePath
     *            文件保存路径。注，必须是一个file路径不能是folder
     * @param url
     *            下载地址
     * @param callback
     *            请求中的回调方法
     */
    public DownloadTaskQueue download(String storeFilePath, String url,
            HttpCallBack callback) {
        FileRequest request = new FileRequest(storeFilePath, url, callback);
        mConfig.mController.add(request);
        doRequest(request);
        return mConfig.mController;
    }

    /**
     * 尝试唤醒一个处于暂停态的下载任务(不推荐)
     * 
     * @param storeFilePath
     *            文件保存路径。注，必须是一个file路径不能是folder
     * @param url
     *            下载地址
     * @deprecated 会造成莫名其妙的问题，建议直接再次调用download方法
     */
    @Deprecated
    public void resumeTask(String storeFilePath, String url) {
        DownloadController controller = mConfig.mController.get(storeFilePath,
                url);
        controller.resume();
    }

    /**
     * 返回下载总控制器
     * 
     * @return
     */
    public DownloadController getDownloadController(String storeFilePath,
            String url) {
        return mConfig.mController.get(storeFilePath, url);
    }

    public void cancleAll() {
        mConfig.mController.clearAll();
    }

    /**
     * 执行一个自定义请求
     * 
     * @param request
     */
    public void doRequest(Request<?> request) {
        request.setConfig(mConfig);
        add(request);
    }

    /**
     * 获取内存缓存数据
     * 
     * @param url
     *            哪条url的缓存
     * @return
     */
    public byte[] getCache(String url) {
        Cache cache = mConfig.mCache;
        cache.initialize();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            return entry.data;
        } else {
            return new byte[0];
        }
    }

    /**
     * 只有你确定cache是一个String时才可以使用这个方法，否则还是应该使用getCache(String);
     * 
     * @param url
     * @return
     */
    public String getStringCache(String url) {
        return new String(getCache(url));
    }

    /**
     * 移除一个缓存
     * 
     * @param url
     *            哪条url的缓存
     */
    public void removeCache(String url) {
        mConfig.mCache.remove(url);
    }

    /**
     * 清空缓存
     */
    public void cleanCache() {
        mConfig.mCache.clear();
    }

    /**
     * 已过期，请更换为getCache()
     */
    @Deprecated
    public String getCache(String uri, HttpParams params) {
        if (params != null) {
            uri += params.getUrlParams();
        }
        return new String(getCache(uri));
    }

    /**
     * 已过期，请更换为cleanCache()
     */
    @Deprecated
    public void removeAllDiskCache() {
        cleanCache();
    }

    /**
     * 已过期，请更换为removeCache()
     */
    @Deprecated
    public void removeDiskCache(String uri, HttpParams params) {
        mConfig.mCache.remove(uri);
    }

    /**
     * 已过期，请更换为getConfig()
     */
    @Deprecated
    public HttpConfig getHttpConfig() {
        return getConfig();
    }

    public HttpConfig getConfig() {
        return mConfig;
    }

    /**
     * 已过期，请更换为setConfig()
     */
    @Deprecated
    public void setHttpConfig(HttpConfig config) {
        setConfig(config);
    }

    public void setConfig(HttpConfig config) {
        this.mConfig = config;
    }

    /******************************** core method ****************************************/

    /**
     * 启动队列调度
     */
    private void start() {
        stop();// 首先关闭之前的运行，不管是否存在
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue,
                mConfig.mCache, mConfig.mDelivery, mConfig);
        mCacheDispatcher.start();
        // 构建线程池
        for (int i = 0; i < mTaskThreads.length; i++) {
            NetworkDispatcher tasker = new NetworkDispatcher(mNetworkQueue,
                    mConfig.mNetwork, mConfig.mCache, mConfig.mDelivery);
            mTaskThreads[i] = tasker;
            tasker.start();
        }
    }

    /**
     * 停止队列调度
     */
    private void stop() {
        if (mCacheDispatcher != null) {
            mCacheDispatcher.quit();
        }
        for (int i = 0; i < mTaskThreads.length; i++) {
            if (mTaskThreads[i] != null) {
                mTaskThreads[i].quit();
            }
        }
    }

    public void cancel(String url) {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                if (url.equals(request.getTag())) {
                    request.cancel();
                }
            }
        }
    }

    /**
     * 取消全部请求
     */
    public void cancelAll() {
        synchronized (mCurrentRequests) {
            for (Request<?> request : mCurrentRequests) {
                request.cancel();
            }
        }
    }

    /**
     * 向请求队列加入一个请求<br>
     * Note:此处工作模式是这样的：KJHttp可以看做是一个队列类，而本方法不断的向这个队列添加request；另一方面，
     * TaskThread不停的从这个队列中取request并执行。类似的设计可以参考Handle...Looper...MessageQueue的关系
     */
    public <T> Request<T> add(Request<T> request) {
        if (request.getCallback() != null) {
            request.getCallback().onPreStart();
        }

        // 标记该请求属于该队列，并将它添加到该组当前的请求。
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }
        // 设置进程优先序列
        request.setSequence(mSequenceGenerator.incrementAndGet());

        // 如果请求不可缓存，跳过缓存队列，直接进入网络。
        if (!request.shouldCache()) {
            mNetworkQueue.add(request);
            return request;
        }

        // 如果已经在mWaitingRequests中有本请求，则替换
        synchronized (mWaitingRequests) {
            String cacheKey = request.getCacheKey();
            if (mWaitingRequests.containsKey(cacheKey)) {
                // There is already a request in flight. Queue up.
                Queue<Request<?>> stagedRequests = mWaitingRequests
                        .get(cacheKey);
                if (stagedRequests == null) {
                    stagedRequests = new LinkedList<Request<?>>();
                }
                stagedRequests.add(request);
                mWaitingRequests.put(cacheKey, stagedRequests);
                if (HttpConfig.DEBUG) {
                    KJLoger.debug(
                            "Request for cacheKey=%s is in flight, putting on hold.",
                            cacheKey);
                }
            } else {
                mWaitingRequests.put(cacheKey, null);
                mCacheQueue.add(request);
            }
            return request;
        }
    }

    /**
     * 将一个请求标记为已完成
     */
    public void finish(Request<?> request) {
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }

        if (request.shouldCache()) {
            synchronized (mWaitingRequests) {
                String cacheKey = request.getCacheKey();
                Queue<Request<?>> waitingRequests = mWaitingRequests
                        .remove(cacheKey);
                if (waitingRequests != null) {
                    if (HttpConfig.DEBUG) {
                        KJLoger.debug(
                                "Releasing %d waiting requests for cacheKey=%s.",
                                waitingRequests.size(), cacheKey);
                    }
                    mCacheQueue.addAll(waitingRequests);
                }
            }
        }
    }

    public void destroy() {
        cancelAll();
        stop();
    }
}
