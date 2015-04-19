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
import org.kymjs.kjframe.bitmap.helper.DiskCache;
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
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
    private final DiskCache diskCache;
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
        taskCollection = new HashSet<BitmapWorkerTask>();
        this.config = bitmapConfig;
        mMemoryCache = new BitmapMemoryCache(config.memoryCacheSize);
        diskCache = new DiskCache(BitmapConfig.CACHEPATH,
                config.memoryCacheSize * 8, config.isDEBUG);
        callback = null;
    }

    /**
     * 加载过程中图片不会闪烁加载方法(不可用在ListView中)
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
     * 加载过程中图片不会闪烁加载方法(不可用在ListView中)
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
            callFailure("imageview is null");
            return;
        }
        if (StringUtils.isEmpty(imageUrl)) {
            callFailure("image url is empty");
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

        config.cxt = (Activity) imageView.getContext();
        config.setDefaultHeight(height);
        config.setDefaultWidth(width);

        cancle(imageView);

        BitmapWorkerTask task = new BitmapWorkerTask(imageView, imageUrl,
                loadBitmap, width, height, notTwink);
        taskCollection.add(task);
        BitmapWorkerTask.setDefaultExecutor(BitmapWorkerTask.mSerialExecutor);
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
        private Bitmap keyBitmap;

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
            config.downloader.setImageCallBack(callback);
            if (callback != null) {
                callback.onPreLoad(imageView);
            }
            imageView.setTag(imageUrl);

            keyBitmap = getBitmapFromCache(imageUrl);
            if (keyBitmap == null) {
                if (!notTwink && imageUrl.startsWith("http")) {
                    setViewImage(imageView, loadBitmap);
                }
            }
        }

        @Override
        protected Bitmap doInBackground() {
            if (keyBitmap == null) {
                keyBitmap = getBitmapFromNet(imageUrl, w, h);
                if (keyBitmap != null) {
                    putBitmapToMC(imageUrl, keyBitmap);
                }
            }
            return keyBitmap;
        }

        @Override
        protected void onPostExecuteSafely(Bitmap result, Exception e)
                throws Exception {
            super.onPostExecuteSafely(result, e);
            if (result != null) {
                if (imageUrl.equals(imageView.getTag())) {
                    setViewImage(imageView, result);
                    if (callback != null) {
                        callback.onSuccess(imageView, result);
                    }
                }
            } else {
                if (callback != null) {
                    e = (e == null) ? new RuntimeException("bitmap not found")
                            : e;
                    callback.onFailure(e);
                }
            }
            if (callback != null) {
                callback.onFinish(imageView);
            }
            keyBitmap = null;
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
        Bitmap bmp = getBitmapFromCache(imageUrl);
        if (bmp == null) {
            bmp = getBitmapFromNet(imageUrl, reqW, reqH);
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
        // 调用加载器加载url中的图片
        byte[] res = config.downloader.loadImage(url);
        if (res != null) {
            bmp = BitmapCreate.bitmapFromByteArray(res, 0, res.length, reqW,
                    reqH);
            if (bmp != null && url.startsWith("http")) {
                putBitmapToDC(url, bmp);
            }
        }
        return bmp;
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     *            图片地址
     * @param path
     *            在本地的绝对路径
     */
    public void saveImage(String url, String path) {
        saveImage(url, path, 0, 0, null);
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     *            图片地址
     * @param path
     *            在本地的绝对路径
     * @param cb
     *            保存过程回调
     */
    public void saveImage(String url, String path, BitmapCallBack cb) {
        saveImage(url, path, 0, 0, cb);
    }

    /**
     * 保存一张图片到本地
     * 
     * @param url
     *            图片地址
     * @param path
     *            在本地的绝对路径
     * @param reqW
     *            图片的宽度（0表示默认）
     * @param reqH
     *            图片的高度（0表示默认）
     * @param cb
     *            保存过程回调
     */
    public void saveImage(final String url, final String path, final int reqW,
            final int reqH, final BitmapCallBack cb) {
        if (cb != null)
            cb.onPreLoad(null);

        Bitmap bmp = getBitmapFromCache(url);
        if (bmp == null) {
            KJAsyncTask.setOnFinishedListener(new OnFinishedListener() {
                @Override
                public void onPostExecute() {
                    if (cb != null) {
                        cb.onSuccess(null, null);
                        cb.onFinish(null);
                    }
                }
            });
            KJAsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = getBitmapFromNet(url, reqW, reqH);
                    if (bmp == null && cb != null && config.cxt != null) {
                        config.cxt.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cb.onFailure(new RuntimeException(
                                        "download error"));
                            }
                        });
                    } else {
                        FileUtils.bitmapToFile(bmp, path);
                    }
                }
            });
        } else {
            bmp = BitmapHelper.scaleWithWH(bmp, reqW, reqH);
            showLogIfOpen("load image from cache");
            boolean success = FileUtils.bitmapToFile(bmp, path);
            if (cb != null) {
                if (success) {
                    cb.onSuccess(null, null);
                } else {
                    cb.onFailure(new RuntimeException("save error"));
                }
                cb.onFinish(null);
            }
        }
    }

    /**
     * 从缓存查找Bitmap
     * 
     * @param key
     */
    public Bitmap getBitmapFromCache(String key) {
        Bitmap bmp = getBitmapFromMC(key);
        if (bmp == null) {
            bmp = getBitmapFromDC(key);
        }
        return bmp;
    }

    /**
     * 从内存缓存读取Bitmap
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBitmapFromMC(String key) {
        Bitmap bmp = mMemoryCache.get(CipherUtils.md5(key));
        if (bmp != null) {
            showLogIfOpen("get bitmap from memory cache");
        }
        return bmp;
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
     * 从磁盘缓存读取Bitmap
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBitmapFromDC(String key) {
        Bitmap bmp = diskCache.get(CipherUtils.md5(key));
        if (bmp != null) {
            showLogIfOpen("get bitmap from disk cache");
        }
        return bmp;
    }

    /**
     * 加入磁盘缓存
     * 
     * @param imagePath
     *            图片路径
     * @param v
     *            要保存的Bitmap
     */
    public void putBitmapToDC(String imagePath, Bitmap v) {
        diskCache.put(CipherUtils.md5(imagePath), v);
    }

    /**
     * 移除一个指定的图片缓存
     * 
     * @param key
     */
    public void removeCache(String key) {
        key = CipherUtils.md5(key);
        mMemoryCache.remove(key);
        File dir = FileUtils.getSaveFolder(BitmapConfig.CACHEPATH);
        File file = new File(dir, BitmapConfig.CACHE_FILENAME_PREFIX + key);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 清空图片缓存
     */
    public void removeCacheAll() {
        mMemoryCache.removeAll();
        diskCache.clearCache();
        // File dir = FileUtils.getSaveFolder(BitmapConfig.CACHEPATH);
        // for (File f : dir.listFiles()) {
        // f.delete();
        // }
    }

    /**
     * 取消一个下载请求
     * 
     * @param view
     */
    public synchronized void cancle(View view) {
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

    private void callFailure(String errorInfo) {
        if (callback != null) {
            callback.onFailure(new RuntimeException(errorInfo));
        }
        Log.e("debug", errorInfo);
    }

    private void callFailure(Exception e) {
        if (callback != null) {
            callback.onFailure(e);
        }
    }

}
