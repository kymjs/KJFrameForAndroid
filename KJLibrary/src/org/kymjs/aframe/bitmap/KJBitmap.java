/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.bitmap;

import java.util.HashSet;
import java.util.Set;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.bitmap.utils.BitmapMemoryCache;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.core.KJTaskExecutor;
import org.kymjs.aframe.utils.CipherUtils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * The BitmapLibrary's core classes<br>
 * <b>创建时间</b> 2014-7-11
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class KJBitmap {
    /**
     * 必须设置为单例，否则内存缓存无效
     */
    private static KJBitmap instance;
    /** 记录所有正在下载或等待下载的任务 */
    private Set<BitmapWorkerTask> taskCollection;
    /** LRU缓存器 */
    private BitmapMemoryCache mMemoryCache;
    /** 图片加载器,若认为KJLibrary的加载器不好，也可自定义图片加载器 */
    private I_ImageLoder downloader;
    /** BitmapLabrary配置器 */
    public static KJBitmapConfig config;

    public synchronized static KJBitmap create() {
        config = new KJBitmapConfig();
        if (instance == null) {
            instance = new KJBitmap();
        }
        return instance;
    }

    private KJBitmap() {
        // downloader = new Downloader(config); // 配置图片加载器
        downloader = new DownloadWithLruCache(config); // 配置图片加载器
        mMemoryCache = new BitmapMemoryCache(config.memoryCacheSize);
        taskCollection = new HashSet<BitmapWorkerTask>();
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    public void display(View imageView, String imageUrl) {
        if (config.openProgress) {
            loadImageWithProgress(imageView, imageUrl);
        } else {
            doDisplay(imageView, imageUrl);
        }
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     * @param openProgress
     *            是否开启环形等待条
     */
    public void display(View imageView, String imageUrl,
            boolean openProgress) {
        boolean temp = config.openProgress;
        config.openProgress = openProgress;
        if (config.openProgress) {
            loadImageWithProgress(imageView, imageUrl);
        } else {
            doDisplay(imageView, imageUrl);
        }
        config.openProgress = temp;
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     * @param imgW
     *            图片显示宽度。若大于图片本身大小，则只显示图片大小
     * @param imgH
     *            图片显示高度。若大于图片本身大小，则只显示图片大小
     */
    public void display(View imageView, String imageUrl, int imgW,
            int imgH) {
        int tempW = config.width;
        int tempH = config.height;
        config.width = imgW;
        config.height = imgH;
        display(imageView, imageUrl);
        config.width = tempW;
        config.height = tempH;
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     * @param loadingBitmap
     *            图片载入过程中显示的图片
     */
    public void display(View imageView, String imageUrl,
            Bitmap loadingBitmap) {
        Bitmap tempLoadBitmap = config.loadingBitmap;
        config.loadingBitmap = loadingBitmap;
        display(imageView, imageUrl);
        config.loadingBitmap = tempLoadBitmap;
        tempLoadBitmap = null;
    }

    /**
     * 显示加载中的环形等待条
     */
    private void loadImageWithProgress(View imageView, String imageUrl) {
        ProgressBar bar = new ProgressBar(imageView.getContext());
        try {
            ViewGroup parent = ((ViewGroup) imageView.getParent());
            if (parent.findViewWithTag(imageUrl) == null) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (imageView.equals(parent.getChildAt(i))) {
                        parent.addView(bar, i);
                        break;
                    }
                }
                bar.setTag(imageUrl);
                imageView.setVisibility(View.GONE);
            } else {
                return;
            }
        } catch (ClassCastException e) {
        }
        doDisplay(imageView, imageUrl);
    }

    /**
     * 加载并显示图片（核心方法）
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    private void doDisplay(View imageView, String imageUrl) {
        doLoadCallBack(imageView);
        Bitmap bitmap = mMemoryCache.get(CipherUtils.md5(imageUrl)); // 从内存中读取
        if (bitmap != null) {
            // 内存缓存中已有图片
            viewSetImage(imageView, bitmap); // 设置控件显示图片
            doSuccessCallBack(imageView); // 图片加载成功时的回调
            showLogIfOpen("download success, from memory cache\n"
                    + imageUrl);
            showProgressIfOpen(imageView, imageUrl);
        } else {
            // 在内存缓存中没有图片，去加载图片
            viewSetImage(imageView, config.loadingBitmap);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView,
                    imageUrl);
            taskCollection.add(task);
            task.execute();
        }

    }

    /**
     * 如果设置了回调,则会调用加载中的回调
     * 
     * @param imageView
     */
    private void doLoadCallBack(View imageView) {
        if (config.callBack != null) {
            config.callBack.imgLoading(imageView);
        }
    }

    /**
     * 如果设置了回调,则会调用加载成功的回调
     * 
     * @param imageView
     */
    private void doSuccessCallBack(View imageView) {
        if (config.callBack != null) {
            config.callBack.imgLoadSuccess(imageView);
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

    /**
     * 如果设置了显示环形等待条
     * 
     * @param imageView
     * @param imageUrl
     */
    private void showProgressIfOpen(View imageView, String imageUrl) {
        if (config.openProgress) {
            try {
                ViewGroup parent = ((ViewGroup) imageView.getParent());
                parent.removeView(parent.findViewWithTag(imageUrl));
            } catch (ClassCastException e) {
            } finally {
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 对不同的控件调用不同的显示方式
     * 
     * @param view
     * @param bitmap
     */
    private void viewSetImage(View view, Bitmap bitmap) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bitmap);
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
    }

    /********************* 异步获取Bitmap并设置image的任务类 *********************/
    private class BitmapWorkerTask extends
            KJTaskExecutor<Void, Void, Bitmap> {
        private View view;
        private String url;

        public BitmapWorkerTask(View view, String url) {
            this.view = view;
            this.url = url;
            this.view.setTag(url);
        }

        @Override
        protected Bitmap doInBackground(Void... _void) {
            Bitmap bmp = null;
            byte[] res = downloader.loadImage(url); // 调用加载器加载url中的图片
            if (res != null) {
                bmp = BitmapCreate.bitmapFromByteArray(res, 0,
                        res.length, config.width, config.height);
            }
            if (bmp != null && config.openMemoryCache) {
                putBmpToMC(url, bmp); // 图片载入完成后缓存到LrcCache中
                showLogIfOpen("put to memory cache\n" + url);
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            if (url.equals(view.getTag())) {
                viewSetImage(view, bmp);
                doSuccessCallBack(view);
                showProgressIfOpen(view, url);
                taskCollection.remove(this);
            }
        }
    }

    /********************************* 属性方法 *********************************/

    /**
     * 添加bitmap到内存缓存
     * 
     * @param k
     *            缓存的key
     * @param v
     *            要添加的bitmap
     */
    public void putBmpToMC(String k, Bitmap v) {
        mMemoryCache.put(CipherUtils.md5(k), v);
    }

    /**
     * 从内存缓存读取Bitmap
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBmpFromMC(String key) {
        return mMemoryCache.get(CipherUtils.md5(key));
    }

    /**
     * 从磁盘缓存读取Bitmap（注，这里有IO操作，应该放在线程中调用）
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBitmapFromDisk(String key) {
        return downloader.getBitmapFromDisk(key);
    }

    /**
     * 从指定key获取一个Bitmap，而不关心是从哪个缓存获取的（注：这里可能会有IO或网络操作，应该放在线程中调用）
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = getBmpFromMC(key);
        if (bitmap == null) {
            byte[] res = downloader.loadImage(key);
            if (res != null) {
                bitmap = BitmapCreate.bitmapFromByteArray(res, 0,
                        res.length, config.width, config.height);
            }
            if (bitmap != null && config.openMemoryCache) {
                // 图片载入完成后缓存到LrcCache中
                putBmpToMC(key, bitmap);
                showLogIfOpen("put to memory cache\n" + key);
            }
        }
        return bitmap;
    }

    /**
     * 取消正在下载的任务
     */
    public void destory() {
        for (BitmapWorkerTask task : taskCollection) {
            task.cancel(true);
        }
        taskCollection.clear();
    }

    /********************************* 配置器设置 *********************************/

    /**
     * 设置bitmap载入时显示的图片
     * 
     * @param b
     */
    public void configLoadingBitmap(Bitmap b) {
        config.loadingBitmap = b;
    }

    /**
     * 设置内存缓存大小
     * 
     * @param size
     */
    public void configMemoryCache(int size) {
        config.memoryCacheSize = size;
    }

    /**
     * 设置图片默认显示的宽高，如果参数大于图片本身的宽高则只显示图片本身宽高
     */
    public void configDefaultShape(int w, int h) {
        config.width = w;
        config.height = h;
    }

    /**
     * 设置图片下载器
     */
    public void configDownloader(I_ImageLoder downloader) {
        this.downloader = downloader;
    }

    /**
     * 是否开启内存缓存
     */
    public void configOpenMemoryCache(boolean openCache) {
        config.openMemoryCache = openCache;
    }

    /**
     * 是否开启本地图片缓存功能
     */
    public void configOpenDiskCache(boolean openCache) {
        config.openDiskCache = openCache;
    }

    /**
     * 设置图片缓存路径
     * 
     * @param cachePath
     */
    public void configCachePath(String cachePath) {
        config.cachePath = cachePath;
    }

    /**
     * 设置配置器
     */
    public void setConfig(KJBitmapConfig cfg) {
        config = cfg;
    }
}
