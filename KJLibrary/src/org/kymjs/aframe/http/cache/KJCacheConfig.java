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
 * Http请求中缓存池的配置器<br>
 * 
 * <b>创建时间</b> 2014-9-3
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class KJCacheConfig {
    private static final long EFFECTIVE_TIME = 50 * 60 * 1000; // 默认十分钟
    /** Cache有效期,实际单位：毫秒；对外单位：秒 */
    private long effectiveTime;
    private boolean isDebug;

    public KJCacheConfig() {
        effectiveTime = EFFECTIVE_TIME;
        isDebug = true;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    /** Cache有效期，单位:秒 */
    public long getEffectiveTime() {
        return effectiveTime;
    }

    /** Cache有效期，单位:秒 */
    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }
}
