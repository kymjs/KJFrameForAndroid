/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
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
package org.kymjs.kjframe.bitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import org.kymjs.kjframe.bitmap.ImageDisplayer.ImageCache;
import org.kymjs.kjframe.utils.SystemTool;

/**
 * 使用lru算法的Bitmap内存缓存池
 * <b>创建时间</b> 2014-7-11
 *
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 */
public final class BitmapMemoryCache implements ImageCache {

    private LruCache<String, Bitmap> cache;
    private int maxSize = 0;

    public BitmapMemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        init(maxMemory / 8);
    }

    /**
     * @param maxSize 使用内存缓存的内存大小，单位：kb
     */
    public BitmapMemoryCache(int maxSize) {
        init(maxSize);
    }

    /**
     * @param maxSize 使用内存缓存的内存大小，单位：kb
     */
    @SuppressLint("NewApi")
    private void init(int maxSize) {
        this.maxSize = maxSize;
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                super.sizeOf(key, value);
                if (SystemTool.getSDKVersion() >= 12) {
                    return value.getByteCount();
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public void clean() {
        init(maxSize);
    }

    /**
     * @param url 图片的地址
     * @return
     */
    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);
    }

    /**
     * @param url    图片的地址
     * @param bitmap 要缓存的bitmap
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if (this.getBitmap(url) == null) {
            cache.put(url, bitmap);
        }
    }
}
