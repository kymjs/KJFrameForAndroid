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
import org.kymjs.aframe.bitmap.core.MemoryCache;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.utils.StringUtils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

/**
 * The BitmapLibrary's core classes
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class KJBitmap {
    /**
     * 必须设置为单例，否则内存缓存无效
     */
    private static KJBitmap instance;
    /** 记录所有正在下载或等待下载的任务 */
    private Set<BitmapWorkerTask> taskCollection;
    /** LRU缓存器 */
    private MemoryCache mMemoryCache;
    /** 图片加载器,若认为KJLibrary的加载器不好，也可自定义图片加载器 */
    private I_ImageLoder downloader;
    /** BitmapLabrary配置器 */
    public KJBitmapConfig config;

    public synchronized static KJBitmap create() {
        if (instance == null)
            instance = new KJBitmap();
        return instance;
    }

    private KJBitmap() {
        if (config == null) {
            config = new KJBitmapConfig();
        }
        // downloader = new Downloader(config); // 配置图片加载器
        downloader = new DownloadWithLruCache(config); // 配置图片加载器
        mMemoryCache = new MemoryCache(config.memoryCacheSize);
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
            loadImage(imageView, imageUrl);
        }
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
    public void display(View imageView, String imageUrl, int imgW, int imgH) {
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
    public void display(View imageView, String imageUrl, Bitmap loadingBitmap) {
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
        loadImage(imageView, imageUrl);
    }

    /**
     * 加载图片（核心方法）
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    private void loadImage(View imageView, String imageUrl) {
        if (config.callBack != null)
            config.callBack.imgLoading(imageView);
        Bitmap bitmap = mMemoryCache.get(StringUtils.md5(imageUrl));
        if (bitmap != null) {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(bitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            if (config.callBack != null)
                config.callBack.imgLoadSuccess(imageView);
            if (config.isDEBUG)
                KJLoger.debugLog(getClass().getName(),
                        "download success, from memory cache\n" + imageUrl);
        } else {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(config.loadingBitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(
                        config.loadingBitmap));
            }
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            taskCollection.add(task);
            task.execute(imageUrl);
        }
    }

    /********************* 异步获取Bitmap并设置image的任务类 *********************/
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private View imageView;

        public BitmapWorkerTask(View imageview) {
            this.imageView = imageview;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            byte[] res = downloader.loadImage(params[0]);
            if (res != null) {
                bitmap = BitmapCreate.bitmapFromByteArray(res, 0, res.length,
                        config.width, config.height);
            }
            if (bitmap != null && config.openMemoryCache) {
                // 图片载入完成后缓存到LrcCache中
                putBitmapToMemory(params[0], bitmap);
                if (config.isDEBUG)
                    KJLoger.debugLog(getClass().getName(),
                            "put to memory cache\n" + params[0]);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView instanceof ImageView) {
                if (bitmap != null) {
                    ((ImageView) imageView).setImageBitmap(bitmap);
                }
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            if (config.callBack != null)
                config.callBack.imgLoadSuccess(imageView);
            taskCollection.remove(this);
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
    public void putBitmapToMemory(String k, Bitmap v) {
        mMemoryCache.put(StringUtils.md5(k), v);
    }

    /**
     * 从内存缓存读取Bitmap
     * 
     * @param key
     *            图片地址Url
     * @return 如果没有key对应的value返回null
     */
    public Bitmap getBitmapFromMemory(String key) {
        return mMemoryCache.get(StringUtils.md5(key));
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
        Bitmap bitmap = getBitmapFromMemory(key);
        if (bitmap == null) {
            byte[] res = downloader.loadImage(key);
            if (res != null) {
                bitmap = BitmapCreate.bitmapFromByteArray(res, 0, res.length,
                        config.width, config.height);
            }
            if (bitmap != null && config.openMemoryCache) {
                // 图片载入完成后缓存到LrcCache中
                putBitmapToMemory(key, bitmap);
                if (config.isDEBUG)
                    KJLoger.debugLog(getClass().getName(),
                            "put to memory cache\n" + key);
            }
        }
        return bitmap;
    }

    /**
     * 取消正在下载的任务
     */
    public void destory() {
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
    public void setConfig(KJBitmapConfig config) {
        this.config = config;
    }
}
