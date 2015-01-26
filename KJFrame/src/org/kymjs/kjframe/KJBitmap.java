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
import java.util.HashSet;
import java.util.Set;

import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.bitmap.helper.BitmapCreate;
import org.kymjs.kjframe.bitmap.helper.BitmapHelper;
import org.kymjs.kjframe.bitmap.helper.BitmapMemoryCache;
import org.kymjs.kjframe.http.core.KJAsyncTask;
import org.kymjs.kjframe.http.core.KJAsyncTask.OnFinishedListener;
import org.kymjs.kjframe.http.core.SimpleSafeAsyncTask;
import org.kymjs.kjframe.utils.CipherUtils;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * The BitmapLibrary's core classes<br>
 * <b>创建时间</b> 2014-7-11<br>
 * <b>最后修改</b> 2015-1-26<br>
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 2.3
 */
public class KJBitmap {

    private static KJBitmap instance;

    /** 记录所有正在下载或等待下载的任务 */
    private final Set<BitmapWorkerTask> taskCollection;
    private final BitmapConfig config;
    /** LRU缓存器 */
    private final BitmapMemoryCache mMemoryCache;
    private BitmapCallBack callback;

    public static KJBitmap create() {
        return create(new BitmapConfig());
    }

    /**
     * 使用配置器创建KJBitmap
     * 
     * @param bitmapConfig
     * @return
     */
    public synchronized static KJBitmap create(BitmapConfig bitmapConfig) {
        if (instance == null) {
            instance = new KJBitmap(bitmapConfig);
        }
        return instance;
    }

    private KJBitmap(BitmapConfig bitmapConfig) {
        this.config = bitmapConfig;
        taskCollection = new HashSet<BitmapWorkerTask>();
        mMemoryCache = new BitmapMemoryCache(bitmapConfig.memoryCacheSize);
    }

    /**
     * 加载过程中图片不会闪烁加载方法
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    public void displayNotTwink(View imageView, String imageUrl) {
        displayNotTwink(imageView, imageUrl, 0, 0);
    }

    /**
     * 加载过程中图片不会闪烁加载方法
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     * @param width
     *            图片的宽
     * @param height
     *            图片的高
     */
    public void displayNotTwink(View imageView, String imageUrl, int width,
            int height) {
        display(imageView, imageUrl, width, height);
    }

    /**
     * 使用默认配置加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    public void display(View imageView, String imageUrl) {
        display(imageView, imageUrl, 0, 0);
    }

    /**
     * 显示网络图片
     * 
     * @param imageView
     *            要显示图片的控件
     * @param imageUrl
     *            图片地址
     * @param width
     *            宽
     * @param height
     *            高
     */
    public void display(View imageView, String imageUrl, int width, int height) {
        display(imageView, imageUrl, width, height, null);
    }

    /**
     * 显示网络图片
     * 
     * @param imageView
     *            要显示图片的控件
     * @param imageUrl
     *            图片地址
     * @param loadBitmap
     *            载入过程中显示的图片
     */
    public void display(View imageView, String imageUrl, Bitmap loadBitmap) {
        display(imageView, imageUrl, loadBitmap, 0, 0);
    }

    /**
     * 显示网络图片
     * 
     * @param imageView
     *            要显示图片的控件
     * @param imageUrl
     *            图片地址
     * @param loadingId
     *            载入过程中显示的图片
     */
    public void display(View imageView, String imageUrl, int loadingId) {
        display(imageView, imageUrl, 0, 0, imageView.getResources()
                .getDrawable(loadingId));
    }

    /**
     * 显示网络图片
     * 
     * @param imageView
     *            要显示图片的控件
     * @param imageUrl
     *            图片地址
     * @param loadBitmap
     *            图片载入过程中的显示
     * @param width
     *            图片宽度
     * @param height
     *            图片高度
     */
    public void display(View imageView, String imageUrl, Bitmap loadBitmap,
            int width, int height) {
        Drawable loadDrawable = null;
        if (loadBitmap != null) {
            loadDrawable = new BitmapDrawable(imageView.getResources(),
                    loadBitmap);
        }
        display(imageView, imageUrl, width, height, loadDrawable);
    }

    /**
     * 显示网络图片
     * 
     * @param imageView
     *            要显示图片的控件
     * @param imageUrl
     *            图片地址
     * @param loadingId
     *            图片载入过程中的显示
     * @param width
     *            图片宽度
     * @param height
     *            图片高度
     */
    public void display(View imageView, String imageUrl, int loadingId,
            int width, int height) {
        display(imageView, imageUrl, width, height, imageView.getResources()
                .getDrawable(loadingId));
    }

    /**
     * 显示网络图片(core)
     * 
     * @param imageView
     *            要显示图片的控件
     * @param imageUrl
     *            图片地址
     * @param loadBitmap
     *            图片载入过程中的显示
     * @param width
     *            图片宽度
     * @param height
     *            图片高度
     */
    private void display(View imageView, String imageUrl, int width,
            int height, Drawable loadBitmap) {
        if (imageView == null) {
            return;
        }
        if (StringUtils.isEmpty(imageUrl)) {
            return;
        }
        if (width == 0 || height == 0) {
            int w = DensityUtils.getScreenW(imageView.getContext()) / 2;
            imageView.measure(w, w);
            if (imageView.getMeasuredWidth() != 0) {
                w = imageView.getMeasuredWidth();
            }
            width = height = w;
        }
        boolean notTwink = false;
        if (loadBitmap == null) {
            notTwink = true;
        }

        config.setDefaultHeight(height);
        config.setDefaultWidth(width);

        cancle(imageView);

        BitmapWorkerTask task = new BitmapWorkerTask(imageView, imageUrl,
                loadBitmap, width, height, notTwink);
        taskCollection.add(task);
        task.execute();
    }

    /********************* 异步获取Bitmap并设置image的任务类 *********************/
    private class BitmapWorkerTask extends SimpleSafeAsyncTask<Bitmap> {

        final View imageView;
        final String imageUrl;
        final Drawable loadBitmap;
        final int w;
        final int h;
        boolean notTwink;

        // public BitmapWorkerTask(View imageView, String imageUrl,
        // Bitmap loadBitmap, int w, int h, boolean notTwink) {
        // this(imageView, imageUrl, new BitmapDrawable(
        // imageView.getResources(), loadBitmap), w, h, notTwink);
        // }

        public BitmapWorkerTask(View imageView, String imageUrl,
                Drawable loadBitmap, int w, int h, boolean notTwink) {
            this.imageView = imageView;
            this.imageUrl = imageUrl;
            this.loadBitmap = loadBitmap;
            this.w = w;
            this.h = h;
            this.notTwink = notTwink;
        }

        // 取消当前正在进行的任务
        public boolean cancelTask() {
            showLogIfOpen("task->" + this.imageUrl + "has been canceled");
            return this.cancel(true);
        }

        @Override
        protected void onPreExecuteSafely() throws Exception {
            super.onPreExecuteSafely();
            if (!notTwink) {
                setViewImage(imageView, loadBitmap);
            }
            config.downloader.setImageCallBack(callback);
            if (callback != null) {
                callback.onPreLoad(imageView);
            }
            imageView.setTag(imageUrl);
        }

        @Override
        protected Bitmap doInBackground() {
            return loadBmpMustInThread(imageUrl, w, h);
        }

        @Override
        protected void onPostExecuteSafely(Bitmap result, Exception e)
                throws Exception {
            super.onPostExecuteSafely(result, e);
            if (e == null) {
                if (imageUrl.equals(imageView.getTag()) && result != null) {
                    setViewImage(imageView, result);
                    putBitmapToMC(imageUrl, result);
                    if (callback != null) {
                        callback.onSuccess(imageView);
                    }
                }
            } else {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
            if (callback != null) {
                callback.onFinish(imageView);
            }
        }
    }

    /********************* public preference method *********************/

    /**
     * 从指定链接获取一张图片，必须放在线程中执行<br>
     * 
     * <b>注意：</b>这里有访问网络的请求，必须放在线程中执行<br>
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param imageUrl
     *            图片对应的Url
     * @param reqW
     *            图片期望宽度，0为图片默认大小
     * @param reqH
     *            图片期望高度，0为图片默认大小
     */
    public Bitmap loadBmpMustInThread(String imageUrl, int reqW, int reqH) {
        Bitmap bmp = getBitmapFromMC(imageUrl);
        if (bmp == null) {
            bmp = getBitmapFromNet(imageUrl, reqW, reqH);
        } else {
            bmp = BitmapHelper.scaleWithWH(bmp, reqW, reqH);
            showLogIfOpen("load image frome memory cache");
        }
        if (bmp != null) {
            putBitmapToMC(imageUrl, bmp);
        }
        return bmp;
    }

    /**
     * 从网络读取Bitmap<br>
     * 
     * <b>注意：</b>这里有网络访问，应该放在线程中调用<br>
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param url
     *            图片地址Url
     * @param reqW
     *            图片期望宽度，0为默认大小
     * @param reqH
     *            图片期望高度，0为默认大小
     * @return
     */
    public Bitmap getBitmapFromNet(String url, int reqW, int reqH) {
        Bitmap bmp = null;
        byte[] res = null;
        try {
            config.downloader.setImageWH(reqW, reqH);
            res = config.downloader.loadImage(url);
        } catch (Exception e) {
            e.printStackTrace();
        } // 调用加载器加载url中的图片
        if (res != null) {
            bmp = BitmapCreate.bitmapFromByteArray(res, 0, res.length, reqW,
                    reqH);
        }
        return bmp;
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     *            图片地址
     * @param fileName
     *            在本地的文件名
     */
    public void saveImage(String url, String fileName) {
        saveImage(url, fileName, 0, 0, null);
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     *            图片地址
     * @param fileName
     *            在本地的文件名
     * @param cb
     *            保存过程回调
     */
    public void saveImage(String url, String fileName, BitmapCallBack cb) {
        saveImage(url, fileName, 0, 0, cb);
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     *            图片地址
     * @param fileName
     *            在本地的文件名
     * @param reqW
     *            图片的宽度（0表示默认）
     * @param reqH
     *            图片的高度（0表示默认）
     * @param cb
     *            保存过程回调
     */
    public void saveImage(final String url, final String fileName,
            final int reqW, final int reqH, final BitmapCallBack cb) {
        if (cb != null)
            cb.onPreLoad(null);

        Bitmap bmp = getBitmapFromMC(url);
        if (bmp == null) {
            KJAsyncTask.setOnFinishedListener(new OnFinishedListener() {
                @Override
                public void onPostExecute() {
                    if (cb != null) {
                        cb.onSuccess(null);
                        cb.onFinish(null);
                    }
                }
            });
            KJAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = getBitmapFromNet(url, reqW, reqH);
                    if (bmp == null && cb != null) {
                        cb.onFailure(new RuntimeException("download error"));
                    } else {
                        FileUtils.bitmapToFile(bmp, FileUtils.getSDCardPath()
                                + File.separator + fileName);
                    }
                }
            });
        } else {
            bmp = BitmapHelper.scaleWithWH(bmp, reqW, reqH);
            showLogIfOpen("load image frome memory cache");
            boolean success = FileUtils.bitmapToFile(bmp,
                    FileUtils.getSDCardPath() + File.separator + fileName);
            if (cb != null) {
                if (success) {
                    cb.onSuccess(null);
                } else {
                    cb.onFailure(new RuntimeException("save error"));
                }
                cb.onFinish(null);
            }
        }
    }

    /**
     * 从内存缓存读取Bitmap
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBitmapFromMC(String key) {
        return mMemoryCache.get(CipherUtils.md5(key));
    }

    /**
     * 添加bitmap到内存缓存
     * 
     * @param k
     *            缓存的key
     * @param v
     *            要添加的bitmap
     */
    public void putBitmapToMC(String k, Bitmap v) {
        mMemoryCache.put(CipherUtils.md5(k), v);
    }

    /**
     * 取消一个下载请求
     * 
     * @param view
     */
    public void cancle(View view) {
        for (BitmapWorkerTask task : taskCollection) {
            if (task.imageView.equals(view)) {
                task.cancelTask();
                taskCollection.remove(task);
                break;
            }
        }
    }

    /**
     * 取消全部下载
     */
    public void cancleAll() {
        for (BitmapWorkerTask task : taskCollection) {
            task.cancelTask();
            taskCollection.remove(task);
        }
    }

    /********************* preference method ******************/

    /**
     * 设置BitmapLibrary的回调监听器
     * 
     * @param callback
     */
    public void setCallback(BitmapCallBack callback) {
        this.callback = callback;
    }

    /********************* private method *********************/
    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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
        if (config.isDEBUG) {
            KJLoger.debugLog(getClass().getName(), msg);
        }
    }
}
