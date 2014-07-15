/*
 * Copyright (c) 2014-2015, kymjs 张涛 (kymjs123@gmail.com).
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kymjs.aframe.utils.FileUtils;
import org.kymjs.aframe.utils.StringUtils;

/**
 * 图片下载器：可以从网络或本地加载一张Bitmap并返回
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class Downloader implements I_ImageLoder {

    /**
     * 图片加载器协议的接口方法
     */
    @Override
    public byte[] loadImage(String imagePath) {
        if (StringUtils.isEmpty(imagePath)) {
            return null;
        }
        byte[] img = null;
        if (!imagePath.trim().toLowerCase().startsWith("http")) { // 如果不是网络图片
            img = loadImgFromFile(imagePath);
        } else { // 网络图片：首先从本地缓存读取，如果本地没有，则重新从网络加载
            File file = FileUtils.getSaveFile(KJBitmap.config.cachePath,
                    StringUtils.md5(imagePath));
            if (file == null) { // 本地没有缓存
                img = loadImgFromNet(imagePath);
            } else {
                try {
                    img = FileUtils.input2byte(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    if (KJBitmapConfig.isDEBUG)
                        e.printStackTrace();
                }
            }
            file = null;
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
            con.setConnectTimeout(KJBitmap.config.timeOut);
            con.setReadTimeout(KJBitmap.config.timeOut * 2);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            data = FileUtils.input2byte(con.getInputStream());
            // 建立本地缓存
            if (KJBitmap.config.openLocalCache) {
                FileUtils.saveFileCache(data,
                        FileUtils.getSavePath(KJBitmap.config.cachePath),
                        StringUtils.md5(imagePath));
            }
        } catch (Exception e) {
            if (KJBitmap.config.callBack != null) {
                KJBitmap.config.callBack.imgLoadFailure(imagePath,
                        e.getMessage());
            }
            if (KJBitmapConfig.isDEBUG)
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
                // 本地图片就不加入本地缓存了
                data = FileUtils.input2byte(fis);
            }
        } catch (FileNotFoundException e) {
            if (KJBitmap.config.callBack != null) {
                KJBitmap.config.callBack.imgLoadFailure(imagePath,
                        e.getMessage());
            }
            if (KJBitmapConfig.isDEBUG)
                e.printStackTrace();
        } finally {
            FileUtils.closeIO(fis);
        }
        return data;
    }
}
