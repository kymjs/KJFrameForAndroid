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

/**
 * 分发器，将异步线程中的结果响应到UI线程中
 * 
 * @author kymjs (http://www.kymjs.com/).
 * 
 */
public interface Delivery {
    /**
     * 分发响应结果
     * 
     * @param request
     * @param response
     */
    public void postResponse(Request<?> request, Response<?> response);

    /**
     * 分发Failure事件
     * 
     * @param request
     *            请求
     * @param error
     *            异常原因
     */
    public void postError(Request<?> request, KJHttpException error);

    /**
     * 当有中介响应的时候，会被调用，首先返回中介响应，并执行runnable(实际就是再去请求网络)<br>
     * Note:所谓中介响应：当本地有一个未过期缓存的时候会优先返回一个缓存，但如果这个缓存又是需要刷新的时候，会再次去请求网络，
     * 那么之前返回的那个有效但需要刷新的就是中介响应
     */
    public void postResponse(Request<?> request, Response<?> response,
            Runnable runnable);

    /**
     * 分发下载进度事件
     * 
     * @param request
     * @param fileSize
     * @param downloadedSize
     */
    public void postDownloadProgress(Request<?> request, long fileSize,
            long downloadedSize);

    public void postCancel(Request<?> request);

}
