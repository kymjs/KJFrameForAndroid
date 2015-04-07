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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.kymjs.kjframe.ui.KJActivityStack;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;

import android.app.Activity;

/**
 * Bitmap下载器
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class BitmapDownloader implements I_ImageLoader {
    private final BitmapConfig config;
    private BitmapCallBack callback;

    public BitmapDownloader(BitmapConfig config) {
        this.config = config;
    }

    @Override
    public void setImageCallBack(BitmapCallBack callback) {
        this.callback = callback;
    }

    /**
     * 从网络加载图片
     * 
     * @param uri
     * @return
     */
    private byte[] fromNet(String uri) {
        byte[] data = null;
        HttpURLConnection con = null;
        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            data = FileUtils.input2byte(con.getInputStream());
        } catch (Exception e) {
            failure(e);
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
     * @throws FileNotFoundException
     */
    private byte[] fromFile(String uri) {
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(uri);
            if (fis != null) {
                data = FileUtils.input2byte(fis);
            }
        } catch (Exception e) {
            failure(e);
        } finally {
            FileUtils.closeIO(fis);
        }
        return data;
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

    private void failure(final Exception e) {
        if (callback != null) {
            final Activity aty;
            if (config.cxt != null) {
                aty = config.cxt;
            } else {
                aty = KJActivityStack.create().topActivity();
            }
            if (aty != null) {
                aty.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }
        }
    }

    @Override
    public byte[] loadImage(String uri) {
        byte[] data = null;
        if (uri.trim().toLowerCase().startsWith("http")) {
            // 网络图片：首先从本地缓如果存读取，本地没有，则重新从网络加载
            data = fromNet(uri);
            showLogIfOpen("download image from net");
        } else {
            // 如果是本地图片
            data = fromFile(uri);
            showLogIfOpen("download image from local file");
        }
        return data;
    }
}
