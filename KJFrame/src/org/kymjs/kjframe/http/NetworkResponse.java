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

import java.util.Collections;
import java.util.Map;

/**
 * 从NetWork执行器返回的Http响应，包含了本次响应是成功还是失败，请求头，响应内容，HTTP状态码
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class NetworkResponse {

    public NetworkResponse(int statusCode, byte[] data,
                           Map<String, String> headers, boolean notModified) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
    }

    public NetworkResponse(byte[] data) {
        this(HttpStatus.SC_OK, data, Collections.<String, String>emptyMap(),
                false);
    }

    public NetworkResponse(byte[] data, Map<String, String> headers) {
        this(HttpStatus.SC_OK, data, headers, false);
    }

    public final int statusCode;

    public final byte[] data;

    public final Map<String, String> headers;

    public final boolean notModified; // 如果服务器返回304(Not Modified)，则为true
}