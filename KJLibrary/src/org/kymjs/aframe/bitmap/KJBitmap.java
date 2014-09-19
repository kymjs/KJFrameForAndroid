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
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.bitmap.utils.BitmapHelper;
import org.kymjs.aframe.bitmap.utils.BitmapMemoryCache;
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
 * <b>创建时间</b> 2014-7-11<br>
 * <b>最后修改</b> 2014-9-19<br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 2.1
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
        if (instance == null) {
            config = new KJBitmapConfig();
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
     * 使用默认bitmap配置器的设置，从指定链接获取一张图片<br>
     * 
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param imageUrl
     *            图片对应的Url
     * @param callback
     *            当图片加载成功后调用接口方法
     */
    public void loadBmpWithConfig(String imageUrl,
            LoadBitmapCallback callback) {
        loadBmpWithConfig(imageUrl, config, callback);
    }

    /**
     * 使用参数bitmap配置器的设置，从指定链接获取一张图片<br>
     * 
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param imageUrl
     *            图片对应的Url
     * @param config
     *            bitmap大小的配置器
     * @param callback
     *            当图片加载成功后调用接口方法
     */
    public void loadBmpWithConfig(String imageUrl,
            KJBitmapConfig config, LoadBitmapCallback callback) {
        loadBmpWithWH(imageUrl, config.width, config.height, callback);
    }

    /**
     * 从指定链接获取一张图片<br>
     * 
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param imageUrl
     *            图片对应的Url
     * @param reqW
     *            图片期望宽度，0为图片默认大小
     * @param reqH
     *            图片期望高度，0为图片默认大小
     * @param callback
     *            当图片加载成功后调用接口方法
     */
    public void loadBmpWithWH(String imageUrl, int reqW, int reqH,
            LoadBitmapCallback callback) {
        Bitmap bmp = getBitmapFromMC(imageUrl);
        if (bmp == null) {
            new LoadBmpWorkerTask(imageUrl, reqW, reqH, callback)
                    .execute();
        } else {
            callback.doSomething(bmp);
        }
    }

    /**
     * 使用默认bitmap配置器的设置，从指定链接获取一张图片，必须放在线程中执行<br>
     * 
     * <b>注意：</b>这里有访问网络的请求，必须放在线程中执行<br>
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param imageUrl
     *            图片对应的Url
     */
    public Bitmap loadBmpMustInThread(String imageUrl) {
        return loadBmpMustInThread(imageUrl, config);
    }

    /**
     * 使用参数bitmap配置器的设置，从指定链接获取一张图片，必须放在线程中执行<br>
     * 
     * <b>注意：</b>这里有访问网络的请求，必须放在线程中执行<br>
     * <b>注意：</b>如果宽高参数为0，显示图片默认大小，此时有可能会造成OOM<br>
     * 
     * @param imageUrl
     *            图片对应的Url
     * @param config
     *            bitmap大小的配置器
     */
    public Bitmap loadBmpMustInThread(String imageUrl,
            KJBitmapConfig config) {
        return loadBmpMustInThread(imageUrl, config.width,
                config.height);
    }

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
    public Bitmap loadBmpMustInThread(String imageUrl, int reqW,
            int reqH) {
        Bitmap bmp = getBitmapFromCache(imageUrl);
        if (bmp == null) {
            bmp = getBitmapFromNet(imageUrl, reqW, reqH);
        } else {
            bmp = BitmapHelper.scaleWithWH(bmp, reqW, reqH);
        }
        if (bmp != null) {
            putBitmapToMC(imageUrl, bmp);
        }
        return bmp;
    }

    private class LoadBmpWorkerTask extends
            KJTaskExecutor<Void, Void, Bitmap> {
        private LoadBitmapCallback callback;
        private String url;
        private int width, height;

        public LoadBmpWorkerTask(String url, int width, int height,
                LoadBitmapCallback callback) {
            this.callback = callback;
            this.url = url;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // 再次检查内存是否由于多线程关系出现了Bitmap
            return loadBmpMustInThread(url, width, height);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            callback.doSomething(result);
        }
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
        display(imageView, imageUrl, config.openProgress);
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
        if (openProgress) {
            loadImageWithProgress(imageView, imageUrl,
                    config.loadingBitmap, config.width, config.height);
        } else {
            doDisplay(imageView, imageUrl, config.loadingBitmap,
                    config.width, config.height);
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
     *            图片显示的宽度，0为默认大小，但可能出现OOM
     * @param imgH
     *            图片显示的高度，0为默认大小，但可能出现OOM
     */
    public void display(View imageView, String imageUrl, int imgW,
            int imgH) {
        display(imageView, imageUrl, config.loadingBitmap, imgW, imgH);
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
        display(imageView, imageUrl, loadingBitmap, config.width,
                config.height);
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的Url
     * @param loadingBitmap
     *            加载中显示的bitmap，没有可以传null
     * @param width
     *            图片显示的宽度，0为默认大小，但可能出现OOM
     * @param height
     *            图片显示的高度，0为默认大小，但可能出现OOM
     */
    public void display(View imageView, String imageUrl,
            Bitmap loadingBitmap, int width, int height) {
        if (config.openProgress) {
            loadImageWithProgress(imageView, imageUrl, loadingBitmap,
                    width, height);
        } else {
            doDisplay(imageView, imageUrl, loadingBitmap, width,
                    height);
        }
    }

    /**
     * 显示加载中的环形等待条
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的Url
     * @param loadingBitmap
     *            加载中显示的bitmap，没有可以传null
     * @param width
     *            图片显示的宽度，0为默认大小，但可能出现OOM
     * @param height
     *            图片显示的高度，0为默认大小，但可能出现OOM
     */
    private void loadImageWithProgress(View imageView,
            String imageUrl, Bitmap loadingBitmap, int width,
            int height) {
        ProgressBar bar = new ProgressBar(imageView.getContext());
        try {
            ViewGroup parent = ((ViewGroup) imageView.getParent());
            // 如果这个控件还没有显示过菊花轮
            if (parent.findViewWithTag(imageUrl) == null) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (imageView.equals(parent.getChildAt(i))) {
                        parent.addView(bar, i);
                        break;
                    }
                }
                bar.setTag(imageUrl);
                imageView.setVisibility(View.GONE);
            }
        } catch (ClassCastException e) {
        }
        doDisplay(imageView, imageUrl, loadingBitmap, width, height);
    }

    /**
     * 加载并显示图片（核心方法）
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的Url
     * @param loadingBitmap
     *            加载中显示的bitmap，没有可以传null
     * @param width
     *            图片显示的宽度，0为默认大小，但可能出现OOM
     * @param height
     *            图片显示的高度，0为默认大小，但可能出现OOM
     */
    private void doDisplay(View imageView, String imageUrl,
            Bitmap loadingBitmap, int width, int height) {
        doLoadCallBack(imageView);
        Bitmap cacheBmp = getBitmapFromCache(imageUrl);
        if (cacheBmp != null) {
            cacheBmp = BitmapHelper.scaleWithWH(cacheBmp, width,
                    height);
            // 内存缓存中已有图片
            viewSetImage(imageView, cacheBmp); // 设置控件显示图片
            doSuccessCallBack(imageView); // 图片加载成功时的回调
            showLogIfOpen("download success, from memory cache\n"
                    + imageUrl);
        } else {
            disPlayFromNet(imageView, imageUrl, loadingBitmap, width,
                    height);
        }
    }

    /**
     * 启动网络加载图片任务
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的Url
     * @param loadingBitmap
     *            加载中显示的bitmap，没有可以传null
     * @param width
     *            图片显示的宽度，0为默认大小，但可能出现OOM
     * @param height
     *            图片显示的高度，0为默认大小，但可能出现OOM
     */
    private void disPlayFromNet(View imageView, String imageUrl,
            Bitmap loadingBitmap, int width, int height) {
        // 开启task的时候先检查传进来的这个view是否已经有一个task是为它执行
        for (BitmapWorkerTask task : taskCollection) {
            if (task.getView().equals(imageView)) {
                // 是同一个url的话就不用开新的task，不一样就取消掉之前开新的
                if (task.getUrl().equals(imageUrl)) {
                    return;
                } else {
                    task.cancelTask();
                    taskCollection.remove(task);
                    break;
                }
            }
        }
        // 在内存缓存中没有图片，去加载图片
        viewSetImage(imageView, loadingBitmap);
        BitmapWorkerTask task = new BitmapWorkerTask(imageView,
                imageUrl, width, height);
        taskCollection.add(task);
        task.execute();
    }

    /********************* 异步获取Bitmap并设置image的任务类 *********************/
    private class BitmapWorkerTask extends
            KJTaskExecutor<Void, Void, Bitmap> {
        private View view;
        private String url;
        private int width, height;

        public BitmapWorkerTask(View view, String url, int width,
                int height) {
            this.view = view;
            this.url = url;
            this.width = width;
            this.height = height;
            this.view.setTag(url);
        }

        public View getView() {
            return view;
        }

        public String getUrl() {
            return url;
        }

        // 取消当前正在进行的任务
        public boolean cancelTask() {
            showLogIfOpen("task->" + this.url + "has been canceled");
            return this.cancel(true);
        }

        @Override
        protected Bitmap doInBackground(Void... _void) {
            Bitmap bmp = getBitmapFromNet(url, width, height);
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            if (bmp != null && config.openMemoryCache) {
                putBitmapToMC(url, bmp); // 图片载入完成后缓存到LrcCache中
                showLogIfOpen("put to memory cache\n" + url);
            }
            if (url.equals(view.getTag())) {
                bmp = BitmapHelper.scaleWithWH(bmp, width, height);
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
    public void putBitmapToMC(String k, Bitmap v) {
        mMemoryCache.put(CipherUtils.md5(k), v);
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
        byte[] res = downloader.loadImage(url); // 调用加载器加载url中的图片
        if (res != null) {
            bmp = BitmapCreate.bitmapFromByteArray(res, 0,
                    res.length, reqW, reqH);
        }
        return bmp;
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
        Bitmap bitmap = getBitmapFromMC(key);
        if (bitmap == null) {
            byte[] res = downloader.loadImage(key);
            if (res != null) {
                bitmap = BitmapCreate.bitmapFromByteArray(res, 0,
                        res.length, config.width, config.height);
            }
            if (bitmap != null && config.openMemoryCache) {
                // 图片载入完成后缓存到LrcCache中
                putBitmapToMC(key, bitmap);
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

    /********************************* 私有方法 *********************************/
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
}
