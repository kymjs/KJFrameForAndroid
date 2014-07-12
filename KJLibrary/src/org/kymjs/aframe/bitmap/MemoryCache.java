package org.kymjs.aframe.bitmap;

import org.kymjs.aframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * 使用lru算法的内存缓存池
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class MemoryCache {

    private LruCache<String, Bitmap> cache;

    @SuppressLint("NewApi")
    public MemoryCache() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        cache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                super.sizeOf(key, value);
                if (SystemTool.getSDKVersion() >= 12) {
                    return value.getByteCount() / 1024;
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    @SuppressLint("NewApi")
    public MemoryCache(int maxSize) {
        cache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                super.sizeOf(key, value);
                if (SystemTool.getSDKVersion() >= 12) {
                    return value.getByteCount() / 1024;
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
    }

    public void put(String key, Bitmap bitmap) {
        if (this.get(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public Bitmap get(String key) {
        return cache.get(key);
    }
}
