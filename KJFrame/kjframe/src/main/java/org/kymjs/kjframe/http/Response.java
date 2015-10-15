/*
 * Copyright (C) 2011 The Android Open Source Project, 张涛
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

import java.util.Map;

/**
 * Http响应封装类，包含了本次响应的全部信息
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class Response<T> {
    /**
     * Http响应的类型
     */
    public final T result;

    /**
     * 本次响应的缓存对象，如果失败则为null
     */
    public final Cache.Entry cacheEntry;

    public final KJHttpException error;

    public final Map<String, String> headers;

    public boolean isSuccess() {
        return error == null;
    }

    private Response(T result, Map<String, String> headers,
                     Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
        this.headers = headers;
    }

    private Response(KJHttpException error) {
        this.result = null;
        this.cacheEntry = null;
        this.headers = null;
        this.error = error;
    }

    /**
     * 返回一个成功的HttpRespond
     *
     * @param result     Http响应的类型
     * @param cacheEntry 缓存对象
     */
    public static <T> Response<T> success(T result,
                                          Map<String, String> headers, Cache.Entry cacheEntry) {
        return new Response<T>(result, headers, cacheEntry);
    }

    /**
     * 返回一个失败的HttpRespond
     *
     * @param error 失败原因
     */
    public static <T> Response<T> error(KJHttpException error) {
        return new Response<T>(error);
    }
}
