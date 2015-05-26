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
package org.kymjs.kjframe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.bitmap.DiskImageRequest;
import org.kymjs.kjframe.bitmap.ImageDisplayer;
import org.kymjs.kjframe.http.Cache;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

/**
 * The BitmapLibrary's core classes<br>
 * <b>创建时间</b> 2014-6-11<br>
 * <b>最后修改</b> 2015-4-23<br>
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 2.4
 */
public class KJBitmap {

    private final BitmapConfig mConfig;
    private final ImageDisplayer displayer;

    private final List<View> doLoadingViews;

    public KJBitmap() {
        this(new BitmapConfig());
    }

    public KJBitmap(BitmapConfig bitmapConfig) {
        this.mConfig = bitmapConfig;
        displayer = new ImageDisplayer(mConfig);
        doLoadingViews = new LinkedList<View>();
    }

    /**
     * 使用默认配置加载网络图片(屏幕的一半显示图片)
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    public void display(View imageView, String imageUrl) {
        displayWithDefWH(imageView, imageUrl, null, null, null);
    }

    /**
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param width
     *            要显示的图片的最大宽度
     * @param height
     *            要显示图片的最大高度
     */
    public void display(View imageView, String imageUrl, int width, int height) {
        display(imageView, imageUrl, width, height, null, null, null);
    }

    /**
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param callback
     *            加载过程的回调
     */
    public void display(View imageView, String imageUrl, BitmapCallBack callback) {
        displayWithDefWH(imageView, imageUrl, null, null, callback);
    }

    /**
     * 如果内存缓存有图片，则显示内存缓存的图片，否则显示默认图片
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param defaultImage
     *            如果没有内存缓存，则显示默认图片
     */
    public void displayCacheOrDefult(View imageView, String imageUrl,
            int defaultImage) {
        Bitmap cache = getMemoryCache(imageUrl);
        if (cache == null) {
            setViewImage(imageView, defaultImage);
        } else {
            setViewImage(imageView, cache);
        }
    }

    /**
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param width
     *            要显示的图片的最大宽度
     * @param height
     *            要显示图片的最大高度
     * @param loadBitmap
     *            加载中图片
     */
    public void display(View imageView, String imageUrl, int width, int height,
            int loadBitmap) {
        display(imageView, imageUrl, width, height, imageView.getResources()
                .getDrawable(loadBitmap), null, null);
    }

    /**
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param loadBitmap
     *            加载中的图片
     */
    public void displayWithLoadBitmap(View imageView, String imageUrl,
            int loadBitmap) {
        displayWithDefWH(imageView, imageUrl, imageView.getResources()
                .getDrawable(loadBitmap), null, null);
    }

    /**
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param errorBitmap
     *            加载出错时设置的默认图片
     */
    public void displayWithErrorBitmap(View imageView, String imageUrl,
            int errorBitmap) {
        displayWithDefWH(imageView, imageUrl, null, imageView.getResources()
                .getDrawable(errorBitmap), null);
    }

    /**
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param errorBitmap
     *            加载出错时设置的默认图片
     */
    public void displayLoadAndErrorBitmap(View imageView, String imageUrl,
            int loadBitmap, int errorBitmap) {
        Resources res = imageView.getResources();
        displayWithDefWH(imageView, imageUrl, res.getDrawable(loadBitmap),
                res.getDrawable(errorBitmap), null);
    }

    /**
     * 如果不指定宽高，则使用默认宽高计算方法
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param loadBitmap
     *            加载中图片
     * @param errorBitmap
     *            加载失败的图片
     * @param callback
     *            加载过程的回调
     */
    public void displayWithDefWH(View imageView, String imageUrl,
            Drawable loadBitmap, Drawable errorBitmap, BitmapCallBack callback) {
        imageView.measure(0, 0);
        int w = imageView.getMeasuredWidth();
        int h = imageView.getMeasuredHeight();
        if (w < 5) {
            w = DensityUtils.getScreenW(imageView.getContext()) / 2;
        }
        if (h < 5) {
            h = DensityUtils.getScreenH(imageView.getContext()) / 2;
        }
        display(imageView, imageUrl, w, h, loadBitmap, errorBitmap, callback);
    }

    /**
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param width
     *            要显示的图片的最大宽度
     * @param height
     *            要显示图片的最大高度
     * @param loadOrErrorBitmap
     *            加载中或加载失败都显示这张图片
     * @param callback
     *            加载过程的回调
     */
    public void display(View imageView, String imageUrl, int loadOrErrorBitmap,
            int width, int height, BitmapCallBack callback) {
        display(imageView, imageUrl, width, height, imageView.getResources()
                .getDrawable(loadOrErrorBitmap), imageView.getResources()
                .getDrawable(loadOrErrorBitmap), callback);
    }

    /**
     * 显示网络图片(core)
     * 
     * @param imageView
     *            要显示的View
     * @param imageUrl
     *            网络图片地址
     * @param width
     *            要显示的图片的最大宽度
     * @param height
     *            要显示图片的最大高度
     * @param loadBitmap
     *            加载中图片
     * @param errorBitmap
     *            加载失败的图片
     * @param callback
     *            加载过程的回调
     */
    public void display(View imageView, String imageUrl, int width, int height,
            Drawable loadBitmap, Drawable errorBitmap, BitmapCallBack callback) {
        if (imageView == null) {
            showLogIfOpen("imageview is null");
            return;
        }
        if (StringUtils.isEmpty(imageUrl)) {
            showLogIfOpen("image url is empty");
            return;
        }

        if (loadBitmap == null) {
            loadBitmap = new ColorDrawable(0xFFCFCFCF);
        }
        if (errorBitmap == null) {
            errorBitmap = new ColorDrawable(0xFFCFCFCF);
        }
        if (callback == null) {
            callback = new BitmapCallBack() {};
        }
        doDisplay(imageView, imageUrl, width, height, loadBitmap, errorBitmap,
                callback);
    }

    /**
     * 真正去加载一个图片
     */
    private void doDisplay(final View imageView, final String imageUrl,
            int width, int height, final Drawable loadBitmap,
            final Drawable errorBitmap, final BitmapCallBack callback) {
        checkViewExist(imageView);

        imageView.setTag(imageUrl);

        BitmapCallBack mCallback = new BitmapCallBack() {
            @Override
            public void onPreLoad() {
                if (callback != null) {
                    callback.onPreLoad();
                }
            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                if (imageUrl.equals(imageView.getTag())) {
                    doSuccess(imageView, bitmap, loadBitmap);
                    if (callback != null) {
                        callback.onSuccess(bitmap);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                doFailure(imageView, errorBitmap);
                if (callback != null) {
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFinish() {
                try {
                    doLoadingViews.remove(imageView);
                } catch (Exception e) {
                }
                if (callback != null) {
                    callback.onFinish();
                }
            }

            @Override
            public void onDoHttp() {
                super.onDoHttp();
                if (callback != null) {
                    callback.onDoHttp();
                }
            }
        };

        if (imageUrl.startsWith("http")) {
            displayer.get(imageUrl, width, height, mCallback);
        } else {
            new DiskImageRequest().load(imageUrl, width, width, mCallback);
        }
    }

    private void doFailure(View view, Drawable errorImage) {
        if (errorImage != null) {
            setViewImage(view, errorImage);
        }
    }

    /**
     * 需要解释一下：如果在本地没有缓存的时候，会首先调用一次onSuccess(null)，此时返回的bitmap是null，
     * 在这个时候我们去设置加载中的图片，当网络请求成功的时候，会再次调用onSuccess(bitmap)，此时才返回网络下载成功的bitmap
     */
    private void doSuccess(View view, Bitmap bitmap, Drawable defaultImage) {
        if (bitmap != null) {
            setViewImage(view, bitmap);
        } else if (defaultImage != null) {
            setViewImage(view, defaultImage);
        }
    }

    /**
     * 移除一个缓存
     * 
     * @param url
     *            哪条url的缓存
     */
    public void removeCache(String url) {
        BitmapConfig.mCache.remove(url);
    }

    /**
     * 已过期，请改用cleanCache()
     */
    @Deprecated
    public void removeCacheAll() {
        cleanCache();
    }

    /**
     * 清空缓存
     */
    public void cleanCache() {
        BitmapConfig.mCache.clear();
    }

    /**
     * 获取缓存数据
     * 
     * @param url
     *            哪条url的缓存
     * @return
     */
    public byte[] getCache(String url) {
        Cache cache = BitmapConfig.mCache;
        cache.initialize();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            return entry.data;
        } else {
            return new byte[0];
        }
    }

    /**
     * 获取内存缓存
     * 
     * @param url
     * @return
     */
    public Bitmap getMemoryCache(String url) {
        return BitmapConfig.mMemoryCache.getBitmap(url);
    }

    /**
     * 取消一个加载请求
     * 
     * @param url
     */
    public void cancle(String url) {
        displayer.cancle(url);
    }

    /**
     * 保存一张图片到本地，并自动通知图库刷新
     * 
     * @param cxt
     * @param url
     * @param path
     */
    public void saveImage(Context cxt, String url, String path) {
        saveImage(cxt, url, path, true, null);
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     * @param path
     * @param cb
     */
    public void saveImage(final Context cxt, String url, final String path,
            final boolean isRefresh, HttpCallBack cb) {
        if (cb == null) {
            cb = new HttpCallBack() {
                @Override
                public void onSuccess(byte[] t) {
                    super.onSuccess(t);
                    if (isRefresh) {
                        refresh(cxt, path);
                    }
                }
            };
        }
        byte[] data = getCache(url);
        if (data.length == 0) {
            new KJHttp().download(path, url, cb);
        } else {
            File file = new File(path);
            cb.onPreStart();
            File folder = file.getParentFile();
            if (folder != null) {
                folder.mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    cb.onFailure(-1, e1.getMessage());
                    return;
                }
            }
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                os.write(data);
                cb.onSuccess(data);
                if (isRefresh) {
                    refresh(cxt, path);
                }
            } catch (IOException e) {
                cb.onFailure(-1, e.getMessage());
            } finally {
                FileUtils.closeIO(os);
                cb.onFinish();
            }
        }
    }

    /**
     * 刷新图库
     * 
     * @param cxt
     * @param path
     */
    public void refresh(Context cxt, String path) {
        String name = "";
        name = path.substring(path.lastIndexOf('/'));
        try {
            MediaStore.Images.Media.insertImage(cxt.getContentResolver(), path,
                    name, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        cxt.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                .parse("file://" + path)));
    }

    /********************* private method *********************/

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void setViewImage(View view, Bitmap background) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(background);
        } else {
            if (SystemTool.getSDKVersion() >= 16) {
                view.setBackground(new BitmapDrawable(view.getResources(),
                        background));
            } else {
                view.setBackgroundDrawable(new BitmapDrawable(view
                        .getResources(), background));
            }
        }
    }

    private void setViewImage(View view, int background) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(background);
        } else {
            view.setBackgroundResource(background);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void setViewImage(View view, Drawable background) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(background);
        } else {
            if (SystemTool.getSDKVersion() >= 16) {
                view.setBackground(background);
            } else {
                view.setBackgroundDrawable(background);
            }
        }
    }

    private void showLogIfOpen(String msg) {
        if (mConfig.isDEBUG) {
            KJLoger.debugLog(getClass().getSimpleName(), msg);
        }
    }

    /**
     * 检测一个View是否已经有任务了，如果是，则取消之前的任务
     * 
     * @param view
     */
    private void checkViewExist(View view) {
        for (View v : doLoadingViews) {
            if (v.equals(view)) {
                String url = (String) v.getTag();
                if (!StringUtils.isEmpty(url)) {
                    cancle(url);
                    break;
                }
            }
        }
        doLoadingViews.add(view);
    }
}
