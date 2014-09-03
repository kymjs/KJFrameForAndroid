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

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.core.MemoryLruCache;
import org.kymjs.aframe.utils.CipherUtils;

/**
 * 使用lru算法的JSON数据内存缓存池。仅内部使用，外界无法访问本类<br>
 * 
 * <b>说明</b> cache中存储是以url的MD5作为key，CacheBean作为V存储<br>
 * <b>创建时间</b> 2014-9-3
 * 
 * @version 1.0
 * @author kymjs(kymjs123@gmail.com)
 */
final class HttpMemoryCache implements I_HttpCache {
    private MemoryLruCache<String, CacheBean> cache;
    private KJCacheConfig config;

    /******************** 创建单例 **********************/
    private static HttpMemoryCache instance;

    private HttpMemoryCache(KJCacheConfig config, int maxSize) {
        cache = new MemoryLruCache<String, CacheBean>(maxSize) {
            @Override
            protected int sizeOf(String k, CacheBean v) {
                super.sizeOf(k, v);
                return v.json.getBytes().length;
            }
        };
    }

    /**
     * 使用默认配置器与缓存大小的内存缓存池（默认缓存大小为系统内存的十六分之一）
     */
    public static HttpMemoryCache create() {
        return create(new KJCacheConfig());
    }

    /**
     * 使用默认配置器的内存缓存池
     * 
     * @param maxSize
     *            使用内存缓存的内存大小，单位：kb
     * @return
     */
    public static HttpMemoryCache create(int maxSize) {
        return create(new KJCacheConfig(), maxSize);
    }

    /**
     * 使用默认缓存大小的内存缓存池（默认缓存大小为系统内存的十六分之一）
     * 
     * @param config
     *            HttpCache配置器
     * @return
     */
    public static HttpMemoryCache create(KJCacheConfig config) {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        return create(config, maxMemory / 16);
    }

    /**
     * 创建内存缓存池
     * 
     * @param config
     *            HttpCache配置器
     * @param maxSize
     *            使用内存缓存的内存大小，单位：kb
     */
    public synchronized static HttpMemoryCache create(
            KJCacheConfig config, int maxSize) {
        if (instance == null) {
            instance = new HttpMemoryCache(config, maxSize);
        }
        return instance;
    }

    /******************** public method **********************/

    /**
     * 添加一个内存缓存
     * 
     * @param url
     *            key
     * @param json
     *            value
     */
    @Override
    public void add(String url, String json) {
        CacheBean data = cache.get(CipherUtils.md5(url));
        long currentTime = System.currentTimeMillis();
        if (data != null) {
            // 如果数据还没过期，则不再添加
            if ((data.overdueTime - currentTime) > 0) {
                return;
            }
        } else {
            data = new CacheBean();
        }
        data.createTime = currentTime;
        data.effectiveTime = config.getEffectiveTime();
        data.overdueTime = data.createTime + data.effectiveTime;
        data.url = url;
        data.json = json;
        cache.put(CipherUtils.md5(url), data);
    }

    /**
     * 添加一个内存缓存
     * 
     * @param cacheBean
     *            缓存对象
     */
    public void add(CacheBean cacheBean) {
        CacheBean data = cache.get(CipherUtils.md5(cacheBean.url));
        long currentTime = System.currentTimeMillis();
        if (data != null && (data.overdueTime - currentTime) > 0) {
            // 如果数据还没过期，则不再添加
            return;
        } else {
            cache.put(CipherUtils.md5(cacheBean.url), cacheBean);
        }
    }

    /**
     * 获取一个未过期的缓存
     * 
     * @param url
     * @return 若url没有对应的cache，返回null；若url所对应的cache已经过期，返回null
     */
    @Override
    public String get(String url) {
        CacheBean data = cache.get(CipherUtils.md5(url));
        if (data == null) { // 没有对应的缓存
            return null;
        } else if ((data.overdueTime - System.currentTimeMillis()) > 0) {
            return data.json;
        } else { // 对应缓存已过期
            return null;
        }
    }

    /**
     * 忽略缓存的有效期读取缓存
     * 
     * @param url
     * @return 若url没有对应的cache，返回null
     */
    @Override
    public String getDataFromCache(String url) {
        CacheBean data = cache.get(CipherUtils.md5(url));
        if (data != null) {
            return data.json;
        } else {
            return null;
        }
    }

    /**
     * 获取当前缓存配置器
     */
    public KJCacheConfig getConfig() {
        return this.config;
    }

    /**
     * 设置当前缓存配置器
     */
    public void setConfig(KJCacheConfig config) {
        this.config = config;
    }

    private void showDebug(String msg) {
        if (config.isDebug()) {
            KJLoger.debugLog(getClass().getName(), "---" + msg);
        }
    }
}
