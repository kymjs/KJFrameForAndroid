/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.bitmap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.bitmap.core.DiskCache;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.StringUtils;

import android.graphics.Bitmap;

/**
 * 使用lru算法缓存的图片下载器：可以从网络或本地加载一张Bitmap并返回
 * 
 * @explain 采用工厂方法模式设计的下载器，同时本类也是一个具体工厂类，生产具体的产品byte[]
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class DownloadWithLruCache implements I_ImageLoder {

    private DiskCache diskCache;
    private KJBitmapConfig config;

    public DownloadWithLruCache(KJBitmapConfig config) {
        super();
        this.config = config;
        diskCache = new DiskCache(config.cachePath, config.diskCacheSize,
                config.isDEBUG);
    }

    /**
     * 图片加载器协议的接口方法
     */
    @Override
    public byte[] loadImage(String imagePath) {
        if (StringUtils.isEmpty(imagePath)) {
            return null;
        }
        byte[] img = null;
        if (config.openDiskCache) { // 如果开启本地缓存，则调用lruCache查找
            img = diskCache.getByteArray(StringUtils.md5(imagePath));
            if (config.isDEBUG && img != null) {
                KJLoger.debugLog(getClass().getName(), "\n" + imagePath
                        + "\ndownload success, from be disk LRU cache");
            }
        }
        if (img == null) { // 重新读取资源
            if (!imagePath.trim().toLowerCase().startsWith("http")) { // 如果不是网络图片
                img = loadImgFromFile(imagePath);
            } else { // 网络图片：首先从本地缓存读取，如果本地没有，则重新从网络加载
                img = loadImgFromNet(imagePath);
            }
        }
        return img;
    }

    /**
     * 从网络载入一张图片
     * 
     * @param imagePath
     *            图片的地址
     */
    private byte[] loadImgFromNet(String imagePath) {
        byte[] data = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(imagePath);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(config.timeOut);
            con.setReadTimeout(config.timeOut * 2);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            data = FileUtils.input2byte(con.getInputStream());
            // 建立diskLru缓存
            if (config.openDiskCache) {
                diskCache.put(StringUtils.md5(imagePath), BitmapCreate
                        .bitmapFromByteArray(data, 0, data.length,
                                config.width, config.height));
            }

            if (config.isDEBUG) {
                KJLoger.debugLog(getClass().getName(), "\n" + imagePath
                        + "\ndownload success, from be net");
            }
        } catch (Exception e) {
            if (config.callBack != null) {
                config.callBack.imgLoadFailure(imagePath, e.getMessage());
            }
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return data;
    }

    /**
     * 从本地载入一张图片
     * 
     * @param imagePath
     *            图片的地址
     */
    private byte[] loadImgFromFile(String imagePath) {
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagePath);
            if (fis != null) {
                data = FileUtils.input2byte(fis);
                // 建立diskLru缓存
                if (config.openDiskCache) {
                    diskCache.put(StringUtils.md5(imagePath), BitmapCreate
                            .bitmapFromByteArray(data, 0, data.length,
                                    config.width, config.height));
                }
                if (config.isDEBUG) {
                    KJLoger.debugLog(getClass().getName(), "\n" + imagePath
                            + "\ndownload success, from be local disk file");
                }
            }
        } catch (FileNotFoundException e) {
            if (config.callBack != null) {
                config.callBack.imgLoadFailure(imagePath, e.getMessage());
            }
            e.printStackTrace();
        } finally {
            FileUtils.closeIO(fis);
        }
        return data;
    }

    /**
     * 从磁盘缓存读取一个Bitmap
     * 
     * @return The bitmap or null if not found
     */
    @Override
    public Bitmap getBitmapFromDisk(String key) {
        return diskCache.get(StringUtils.md5(key));
    }
}
