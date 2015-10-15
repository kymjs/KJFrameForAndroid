/*
 * Copyright (c) 2014, 张涛.
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

import android.os.Looper;

import org.kymjs.kjframe.KJHttp;

import java.util.LinkedList;
import java.util.List;

/**
 * 负责维护当前正在下载状态<br>
 * 对每个下载请求提供一个控制器，来控制下载的各种状态改变
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class DownloadTaskQueue {
    private final int mParallelTaskCount; // 最大同时下载量
    private final List<DownloadController> mTaskQueue; // 以链表的形式储存下载控制器
    private KJHttp mRequestQueue; // 关联一个请求队列，目的是在恢复下载的时候可以再次将下载请求加入到请求队列中

    public DownloadTaskQueue(int parallelTaskCount) {
        if (parallelTaskCount >= HttpConfig.NETWORK_POOL_SIZE) {
            parallelTaskCount = HttpConfig.NETWORK_POOL_SIZE - 1;
        }
        mParallelTaskCount = parallelTaskCount;
        mTaskQueue = new LinkedList<>();
    }

    public List<DownloadController> getTaskQueue() {
        return mTaskQueue;
    }

    /**
     * 清空全部下载任务
     */
    public void clearAll() {
        synchronized (mTaskQueue) {
            while (mTaskQueue.size() > 0) {
                mTaskQueue.get(0).removeTask();
            }
        }
    }

    /**
     * 添加一个下载请求,如果这个请求已经存在，则尝试唤醒这个请求
     *
     * @param request 要添加的下载请求
     */
    public void add(FileRequest request) {
        throwIfNotOnMainThread();
        DownloadController requestTask = requestExist(request);
        if (requestTask != null) {
            requestTask.removeTask();
        }
        synchronized (mTaskQueue) {
            mTaskQueue.add(new DownloadController(this, request));
        }
        wake();
    }

    /**
     * 移除一个下载任务
     *
     * @param url 要移除的url
     */
    public void remove(String url) {
        for (DownloadController controller : mTaskQueue) {
            if (controller.equalsUrl(url)) {
                synchronized (mTaskQueue) {
                    mTaskQueue.remove(controller);
                    wake();
                    return;
                }
            }
        }
    }

    /**
     * @param storeFilePath 下载后文件在本地的地址
     * @param url           下载的url
     * @return DownloadController下载控制器
     */
    public DownloadController get(String storeFilePath, String url) {
        synchronized (mTaskQueue) {
            for (DownloadController controller : mTaskQueue) {
                if (controller.equalsRequest(storeFilePath, url))
                    return controller;
            }
        }
        return null;
    }

    public void setRequestQueue(KJHttp requestQueue) {
        this.mRequestQueue = requestQueue;
    }

    /* package */KJHttp getRequestQueue() {
        return mRequestQueue;
    }

    /* package */void wake() {
        synchronized (mTaskQueue) {
            int parallelTaskCount = 0; // 同时下载的数量

            for (DownloadController controller : mTaskQueue) {
                if (controller.isDownloading()) {
                    parallelTaskCount++;
                }
            }

            // 判断同时下载数量是否超过最大值
            for (DownloadController controller : mTaskQueue) {
                if (parallelTaskCount < mParallelTaskCount) {
                    if (controller.doLoadOnWait()) {
                        parallelTaskCount++;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 必须在主线程执行
     */
    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "FileDownloader must be invoked from the main thread.");
        }
    }

    /**
     * 如果这个请求本身就存在，则直接返回这个请求
     *
     * @param request 要被判断的request
     * @return 如果这个请求本身就存在，则直接返回这个请求
     */
    private DownloadController requestExist(FileRequest request) {
        for (DownloadController task : mTaskQueue) {
            FileRequest req = task.getRequest();
            if (request.getUrl().equals(req.getUrl())
                    && request.getStoreFile().getAbsolutePath()
                    .equals(req.getStoreFile().getAbsolutePath())) {
                return task;
            }
        }
        return null;
    }
}
