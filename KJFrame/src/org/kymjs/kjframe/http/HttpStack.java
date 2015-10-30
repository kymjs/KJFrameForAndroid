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

import java.io.IOException;
import java.util.Map;

/**
 * Http请求端，已知实现类:
 *
 * @author kymjs (http://www.kymjs.com/) .
 * @see HttpConnectStack
 * 过时移除 HttpClientStack
 */
public interface HttpStack {
    /**
     * 让Http请求端去发起一个Request
     *
     * @param request           一次实际请求集合
     * @param additionalHeaders Http请求头
     * @return 一个Http响应
     */
    KJHttpResponse performRequest(Request<?> request,
                                  Map<String, String> additionalHeaders) throws IOException;

}
