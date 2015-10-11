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

import java.util.Collections;
import java.util.Map;

/**
 * 一个缓存接口协议，其中包含了缓存的bean原型
 */
public interface Cache {

    Entry get(String key);

    void put(String key, Entry entry);

    void remove(String key);

    void clean();

    /**
     * 执行在线程中
     */
    void initialize();

    /**
     * 让一个缓存过期
     *
     * @param key        Cache key
     * @param fullExpire True to fully expire the entry, false to soft expire
     */
    void invalidate(String key, boolean fullExpire);

    /**
     * cache真正缓存的数据bean，这个是会被保存的缓存对象
     */
    class Entry {
        public byte[] data;
        public String etag; // 为cache标记一个tag

        public long serverDate; // 本次请求成功时的服务器时间
        public long ttl; // 有效期,System.currentTimeMillis()

        public Map<String, String> responseHeaders = Collections.emptyMap();

        /**
         * 是否已过期
         */
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }
    }
}
