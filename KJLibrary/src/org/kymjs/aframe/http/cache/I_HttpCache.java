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
package org.kymjs.aframe.http.cache;

/**
 * httpLibrary中数据缓存池的规范接口协议<br>
 * 
 * <b>创建时间</b> 2014-9-3
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public interface I_HttpCache {

    /**
     * 添加一个缓存
     * 
     * @param url
     *            key
     * @param json
     *            value
     */
    void add(String url, String json);

    /**
     * 获取一个未过期的缓存
     * 
     * @param url
     * @return 若url没有对应的cache，返回null；若url所对应的cache已经过期，返回null
     */
    String get(String url);

    /**
     * 忽略缓存的有效期读取缓存
     * 
     * @param url
     * @return 若url没有对应的cache，返回null
     */
    String getDataFromCache(String url);
}
