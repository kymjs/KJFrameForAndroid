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
package org.kymjs.kjframe.http;

import java.util.concurrent.BlockingQueue;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.utils.KJLoger;

import android.os.Process;

/**
 * 缓存调度器<br>
 * 
 * Note:<br>
 * 工作描述： 缓存逻辑同样也采用责任链模式，参考注释{@link KJHttp }，
 * 由缓存任务队列CacheQueue，缓存调度器CacheDispatcher，缓存器Cache组成<br>
 * 
 * 调度器不停的从CacheQueue中取request，并把这个request尝试从缓存器中获取缓存响应。<br>
 * 如果缓存器有有效且及时的缓存则直接返回缓存;<br>
 * 如果缓存器有有效但待刷新的有效缓存，则交给分发器去分发一次中介相应，并再去添加到工作队列中执行网络请求获取最新的数据;<br>
 * 如果缓存器中没有有效缓存，则把请求添加到mNetworkQueue工作队列中去执行网络请求;<br>
 * 
 * Note:<br>
 * 关于中介相应查看{@link Response#intermediate}
 */
public class CacheDispatcher extends Thread {

    private final BlockingQueue<Request<?>> mCacheQueue; // 缓存队列
    private final BlockingQueue<Request<?>> mNetworkQueue; // 用于执行网络请求的工作队列
    private final Cache mCache; // 缓存器
    private final Delivery mDelivery; // 分发器
    private final HttpConfig mConfig; // 配置器

    private volatile boolean mQuit = false;

    /**
     * 创建分发器(必须手动调用star()方法启动分发任务)
     * 
     * @param cacheQueue
     *            缓存队列
     * @param networkQueue
     *            正在执行的队列
     * @param cache
     *            缓存器对象
     * @param delivery
     *            分发器
     */
    public CacheDispatcher(BlockingQueue<Request<?>> cacheQueue,
            BlockingQueue<Request<?>> networkQueue, Cache cache,
            Delivery delivery, HttpConfig config) {
        mCacheQueue = cacheQueue;
        mNetworkQueue = networkQueue;
        mCache = cache;
        mDelivery = delivery;
        mConfig = config;
    }

    /**
     * 强制退出
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    /**
     * 工作在阻塞态
     */
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mCache.initialize();

        while (true) {
            try {
                final Request<?> request = mCacheQueue.take();
                if (request.isCanceled()) {
                    request.finish("cache-discard-canceled");
                    continue;
                }

                Cache.Entry entry = mCache.get(request.getCacheKey());
                if (entry == null) { // 如果没有缓存，去网络请求
                    mNetworkQueue.put(request);
                    continue;
                }

                // 如果缓存过期，去网络请求,图片缓存永久有效
                if (entry.isExpired()) {
                    // && !(request instanceof ImageRequest)
                    request.setCacheEntry(entry);
                    mNetworkQueue.put(request);
                    continue;
                }

                // 从缓存返回数据
                Response<?> response = request
                        .parseNetworkResponse(new NetworkResponse(entry.data,
                                entry.responseHeaders));
                KJLoger.debugLog("CacheDispatcher：", "http resopnd from cache");
                if (mConfig.useDelayCache) {
                    sleep(mConfig.delayTime);
                }
                mDelivery.postResponse(request, response);
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                } else {
                    continue;
                }
            }
        }
    }
}
