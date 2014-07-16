/*
 * Copyright (c) 2014, kymjs 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.bitmap.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.utils.FileUtils;

import android.graphics.Bitmap;

/**
 * 使用lru算法的Bitmap磁盘缓存
 * 
 * @from https://github.com/kuangsunny/KJFrameForAndroid
 * @author kuangsunny
 * @version 1.0
 * @created 2014-7-13
 * @change kymjs(kymjs123@gmail.com)
 * @lastChange 2014-7-16
 */
public class DiskCache {
    private DiskLruCache mDiskCache;

    private static int IO_BUFFER_SIZE = 16 * 1024; // 默认IO缓冲区大小

    public DiskCache() {
        initLruCache();
    }

    /**
     * 初始化容器类：mDiskCache
     */
    private void initLruCache() {
        File diskCacheDir = FileUtils.getSaveFolder(KJBitmap.config.cachePath); // 缓存地址
        try {
            mDiskCache = DiskLruCache.open(diskCacheDir,
                    KJBitmap.config.diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String k, Bitmap v) {
        if (mDiskCache.isClosed()) { // 如果还没有初始化容器就先初始化容器
            initLruCache();
        }
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(k);
            if (editor != null) {
                mDiskCache.flush();
                editor.commit();
            }
        } catch (IOException e) {
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    public Bitmap get(String key) {
        if (mDiskCache.isClosed()) {
            initLruCache();
        }
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            snapshot = mDiskCache.get(key);
            if (snapshot != null) {
                is = snapshot.getInputStream(0);
                if (is != null) {
                    bis = new BufferedInputStream(is, IO_BUFFER_SIZE);
                    bitmap = BitmapCreate.bitmapFromStream(bis, null,
                            KJBitmap.config.width, KJBitmap.config.height);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeIO(is, bis, snapshot);
        }
        return bitmap;
    }

    public boolean containsKey(String key) {
        if (mDiskCache.isClosed()) {
            initLruCache();
        }
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            contained = (snapshot != null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return contained;
    }

    public void clearCache() {
        if (mDiskCache.isClosed()) {
            initLruCache();
        }
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存路径
     */
    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }
}
