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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 用于解析HttpHeader的工具类
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class HttpHeaderParser {

    public static Cache.Entry parseCacheHeaders(HttpConfig httpconfig,
                                                NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0; // 服务器返回本次响应时的时间
        long maxAge = 0; // 本次缓存的有效时间
        boolean hasCacheControl = false; // 服务器是否有声明缓存控制
        String serverEtag = null;
        String tempStr;

        tempStr = headers.get("Date");
        if (tempStr != null) {
            serverDate = parseDateAsEpoch(tempStr);
        }

        // 如果服务器有声明缓存控制器，则使用服务器的控制逻辑
        tempStr = headers.get("Cache-Control");
        if (tempStr != null) {
            hasCacheControl = true;
            String[] tokens = tempStr.split(",");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                // 如果服务器说不缓存这次数据，那么就不缓存了。。。。
                if (token.equals("no-cache") || token.equals("no-store")) {
                    return null;
                } else if (token.startsWith("max-age=")) {
                    try {
                        // 如果服务器声明了缓存时间长度，则使用服务器缓存时间长度
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                    }
                } else if (token.equals("must-revalidate")
                        || token.equals("proxy-revalidate")) {
                    // 如果服务器声明必须重新验证，或必须使用代理验证，则相当于本次数据是一次性的
                    maxAge = 0;
                }
            }
        }

        long serverExpires = 0; // 如果有到期时限，则也使用服务器的到期时限
        tempStr = headers.get("Expires");
        if (tempStr != null) {
            serverExpires = parseDateAsEpoch(tempStr);
        }

        long softExpire = 0; // 定义多久以后需要刷新
        serverEtag = headers.get("ETag");
        if (hasCacheControl) {
            softExpire = now + maxAge * 1000;
        } else if (serverDate > 0 && serverExpires >= serverDate) {
            softExpire = now + (serverExpires - serverDate);
        }

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;

        if (HttpConfig.useServerControl) {
            entry.ttl = softExpire;
        } else {
            entry.ttl = now + httpconfig.cacheTime * 60000; // 分钟转毫秒
        }
        entry.etag = serverEtag;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }

    /**
     * 使用RFC1123格式解析服务器返回的时间
     *
     * @return 如果解析异常，返回null
     */
    public static long parseDateAsEpoch(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        try {
            return sdf.parse(dateStr).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 返回这个内容头的编码，如果没有则使用HTTP默认（ISO-8859-1）指定的字符集
     */
    public static String parseCharset(Map<String, String> headers) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return "ISO-8859-1";
    }
}
