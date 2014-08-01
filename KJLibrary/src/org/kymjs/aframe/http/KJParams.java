/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.aframe.http;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Http请求 参数集
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-6-5
 */
public class KJParams {
    protected ConcurrentHashMap<String, String> urlParams;

    protected void init() {
        urlParams = new ConcurrentHashMap<String, String>();
    }

    public KJParams() {
        init();
    }

    public KJParams(Map<String, String> source) {
        init();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public KJParams(String key, String value) {
        init();
        put(key, value);
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /**
     * 移除key与对应的value
     */
    public void remove(String key) {
        urlParams.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        Iterator<Entry<String, String>> it = urlParams.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            str.append(key).append("=").append(value).append("&");
        }
        return str.toString();
    }
}