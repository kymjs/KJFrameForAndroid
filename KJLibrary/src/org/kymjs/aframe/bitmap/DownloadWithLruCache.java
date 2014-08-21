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
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.core.DiskCache;
import org.kymjs.aframe.utils.CipherUtils;
import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.StringUtils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

/**
 * 使用lru算法缓存的图片下载器：可以从网络或本地加载一张Bitmap并返回<br>
 * <b>说明</b> 采用模板方法模式设计的下载器，同时本类也是一个具体模板类，实现具体的模板方法<br>
 * <b>创建时间</b> 2014-7-11
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0 <br>
 */
public class DownloadWithLruCache implements I_ImageLoder {

    private DiskCache diskCache;
    private KJBitmapConfig config;

    public DownloadWithLruCache(KJBitmapConfig config) {
        super();
        this.config = config;
        diskCache = new DiskCache(config.cachePath,
                config.diskCacheSize, config.isDEBUG);
    }

    /**
     * 图片加载器协议的接口方法
     */
    @Override
    @SuppressLint("DefaultLocale")
    public byte[] loadImage(String imagePath) {
        if (StringUtils.isEmpty(imagePath)) {
            return null;
        }
        byte[] img = null;
        if (config.openDiskCache) { // 如果开启本地缓存，则调用lruCache查找
            img = diskCache.getByteArray(CipherUtils.md5(imagePath));
            if (img != null) {
                showLogIfOpen(imagePath
                        + "\ndownload success, from be disk LRU cache");
            }
        }
        if (img == null) { // diskCache中没有，重新读取资源
            if (imagePath.trim().toLowerCase().startsWith("http")) {
                // 网络图片：首先从本地缓如果存读取，本地没有，则重新从网络加载
                img = loadImgFromNet(imagePath);
            } else {
                // 如果是本地图片
                img = loadImgFromFile(imagePath);
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
            putBmpToDC(imagePath, data); // 建立diskLru缓存
            showLogIfOpen(imagePath
                    + "\ndownload success, from be net");
        } catch (Exception e) {
            doFailureCallBack(imagePath, e);
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
                putBmpToDC(imagePath, data); // 建立diskLru缓存
                showLogIfOpen(imagePath
                        + "\ndownload success, from be local disk file");
            }
        } catch (FileNotFoundException e) {
            doFailureCallBack(imagePath, e);
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
        return diskCache.get(CipherUtils.md5(key));
    }

    /**
     * 如果设置了回调，调用加载失败回调
     * 
     * @param imagePath
     *            加载失败的图片路径
     * @param e
     *            失败原因
     */
    private void doFailureCallBack(String imagePath, Exception e) {
        if (config.callBack != null) {
            config.callBack.imgLoadFailure(imagePath, e.getMessage());
        }
    }

    /**
     * 加入磁盘缓存
     * 
     * @param imagePath
     *            图片路径
     * @param bmpByteArray
     *            图片二进制数组数据
     */
    private void putBmpToDC(String imagePath, byte[] bmpByteArray) {
        if (config.openDiskCache) {
            diskCache.put(CipherUtils.md5(imagePath), BitmapCreate
                    .bitmapFromByteArray(bmpByteArray, 0,
                            bmpByteArray.length, config.width,
                            config.height));
        }
    }

    /**
     * 如果打开了log显示器，则显示log
     * 
     * @param imageUrl
     */
    private void showLogIfOpen(String log) {
        if (config.isDEBUG) {
            KJLoger.debugLog(getClass().getName(), log);
        }
    }
}
