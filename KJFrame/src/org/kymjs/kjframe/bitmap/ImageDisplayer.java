/**
 * Copyright (C) 2013 The Android Open Source Project
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

import java.util.HashMap;
import java.util.LinkedList;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.KJHttpException;
import org.kymjs.kjframe.http.Request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

/**
 * 图片显示器
 */
public class ImageDisplayer {
    private final KJHttp mKJHttp; // 使用KJHttp的线程池执行队列去加载图片

    private final ImageCache mMemoryCache; // 内存缓存器
    // 为了防止网速很快的时候速度过快而造成先显示加载中图片，然后瞬间显示网络图片的闪烁问题
    private final int mResponseDelayMs = 100;

    private Runnable mRunnable;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // 正在请求的事件
    private final HashMap<String, ImageRequestEven> mRequestsMap = new HashMap<String, ImageRequestEven>();
    // 已经请求完成，待处理的事件
    private final HashMap<String, ImageRequestEven> mResponsesMap = new HashMap<String, ImageRequestEven>();

    /**
     * 创建一个图片显示器
     * 
     * @param bitmapConfig
     */
    public ImageDisplayer(BitmapConfig bitmapConfig) {
        HttpConfig config = new HttpConfig();
        config.mCache = bitmapConfig.mCache;
        config.cacheTime = Integer.MAX_VALUE;
        mKJHttp = new KJHttp(config);
        mMemoryCache = bitmapConfig.mMemoryCache;
    }

    /**
     * 判断指定图片是否已经被缓存
     * 
     * @param requestUrl
     *            图片地址
     * @param maxWidth
     *            图片最大宽度
     * @param maxHeight
     *            图片最大高度
     * @return
     */
    public boolean isCached(String requestUrl, int maxWidth, int maxHeight) {
        throwIfNotOnMainThread();
        String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight);
        return mMemoryCache.getBitmap(cacheKey) != null;
    }

    /**
     * 加载一张图片
     * 
     * @param requestUrl
     *            图片地址
     * @param maxWidth
     *            图片最大宽度(如果网络图片大于这个宽度则缩放至这个大小)
     * @param maxHeight
     *            图片最大高度
     * @param callback
     * @return
     */
    public ImageBale get(String requestUrl, int maxWidth, int maxHeight,
            BitmapCallBack callback) {
        throwIfNotOnMainThread();
        callback.onPreLoad();

        final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight);
        Bitmap cachedBitmap = mMemoryCache.getBitmap(cacheKey);
        try {
            if (cachedBitmap == null) {
                byte[] cacheData = mKJHttp.getCache(requestUrl);
                if (cacheData != null) {
                    cachedBitmap = BitmapFactory.decodeByteArray(cacheData, 0,
                            cacheData.length);
                }
            }
        } catch (Exception e) { // 本地缓存读取异常，则去网络加载
        }
        if (cachedBitmap != null) {
            ImageBale container = new ImageBale(cachedBitmap, requestUrl, null,
                    null);
            callback.onSuccess(cachedBitmap);
            callback.onFinish();
            return container;
        } else {
            // 开始加载网络图片的标志
            callback.onSuccess(null);
        }

        ImageBale imageBale = new ImageBale(null, requestUrl, cacheKey,
                callback);
        ImageRequestEven request = mRequestsMap.get(cacheKey);
        if (request != null) {
            request.addImageBale(imageBale);
            return imageBale;
        }

        Request<Bitmap> newRequest = makeImageRequest(requestUrl, maxWidth,
                maxHeight, cacheKey);

        mKJHttp.doRequest(newRequest);
        mRequestsMap.put(cacheKey, new ImageRequestEven(newRequest, imageBale));
        return imageBale;
    }

    /**
     * 创建一个网络请求
     */
    protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth,
            int maxHeight, final String cacheKey) {
        return new ImageRequest(requestUrl, maxWidth, maxHeight,
                new HttpCallBack() {
                    @Override
                    public void onSuccess(Bitmap t) {
                        super.onSuccess(t);
                        onGetImageSuccess(cacheKey, t);
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        onGetImageError(cacheKey, new KJHttpException(strMsg));
                    }
                });
    }

    /**
     * 从网络获取bitmap成功时调用
     * 
     * @param cacheKey
     *            缓存key
     * @param bitmap
     *            获取到的bitmap
     */
    protected void onGetImageSuccess(String cacheKey, Bitmap bitmap) {
        mMemoryCache.putBitmap(cacheKey, bitmap);
        // 从正在请求的列表中移除这个已完成的请求
        ImageRequestEven request = mRequestsMap.remove(cacheKey);

        if (request != null) {
            request.mBitmapRespond = bitmap;
            batchResponse(cacheKey, request);
        }
    }

    /**
     * 从网络获取bitmap失败时调用
     * 
     * @param cacheKey
     *            缓存key
     * @param error
     *            失败原因
     */
    protected void onGetImageError(String cacheKey, KJHttpException error) {
        // 从正在请求的列表中移除这个已完成的请求
        ImageRequestEven request = mRequestsMap.remove(cacheKey);
        if (request != null) {
            request.setError(error);
            batchResponse(cacheKey, request);
        }
    }

    /**************************************************************************/

    /**
     * 对一个图片的封装，包含了这张图片所需要携带的信息
     * 
     * @author kymjs
     * 
     */
    public class ImageBale {
        private Bitmap mBitmap;
        private final String mRequestUrl;
        private final String mCacheKey;
        private final BitmapCallBack mCallback;

        public ImageBale(Bitmap bitmap, String requestUrl, String cacheKey,
                BitmapCallBack callback) {
            mBitmap = bitmap;
            mRequestUrl = requestUrl;
            mCacheKey = cacheKey;
            mCallback = callback;
        }

        public void cancelRequest() {
            if (mCallback == null) {
                return;
            }

            ImageRequestEven request = mRequestsMap.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeBale(this);
                if (canceled) {
                    mRequestsMap.remove(mCacheKey);
                }
            } else {
                request = mResponsesMap.get(mCacheKey);
                if (request != null) {
                    request.removeBale(this);
                    if (request.mImageBales.size() == 0) {
                        mResponsesMap.remove(mCacheKey);
                    }
                }
            }
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }

        public String getRequestUrl() {
            return mRequestUrl;
        }
    }

    /**
     * 图片从网络请求并获取到相应的事件
     * 
     * @author kymjs
     * 
     */
    private class ImageRequestEven {
        private final Request<?> mRequest;
        private Bitmap mBitmapRespond;
        private KJHttpException mError;
        private final LinkedList<ImageBale> mImageBales = new LinkedList<ImageBale>();

        public ImageRequestEven(Request<?> request, ImageBale imageBale) {
            mRequest = request;
            mImageBales.add(imageBale);
        }

        public void setError(KJHttpException error) {
            mError = error;
        }

        public KJHttpException getError() {
            return mError;
        }

        public void addImageBale(ImageBale imageBale) {
            mImageBales.add(imageBale);
        }

        public boolean removeBale(ImageBale imageBale) {
            mImageBales.remove(imageBale);
            if (mImageBales.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }

    /**************************************************************************/

    /**
     * 分发这次ImageRequest事件的结果
     */
    private void batchResponse(String cacheKey, final ImageRequestEven request) {
        mResponsesMap.put(cacheKey, request);
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    for (ImageRequestEven even : mResponsesMap.values()) {
                        for (ImageBale imageBale : even.mImageBales) {
                            if (imageBale.mCallback == null) {
                                continue;
                            }
                            if (even.getError() == null) {
                                imageBale.mBitmap = even.mBitmapRespond;
                                imageBale.mCallback
                                        .onSuccess(imageBale.mBitmap);
                            } else {
                                imageBale.mCallback.onFailure(even.getError());
                            }
                            imageBale.mCallback.onFinish();
                        }
                    }
                    mResponsesMap.clear();
                    mRunnable = null;
                }

            };
            mHandler.postDelayed(mRunnable, mResponseDelayMs);
        }
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "ImageLoader must be invoked from the main thread.");
        }
    }

    /**
     * 取消一个加载请求
     * 
     * @param url
     */
    public void cancle(String url) {
        mKJHttp.cancel(url);
    }

    /**
     * 根据指定url创建一个key
     */
    private static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return new StringBuilder(url.length() + 12).append("#W")
                .append(maxWidth).append("#H").append(maxHeight).append(url)
                .toString();
    }

    /**
     * 内存缓存接口定义
     * 
     * @author kymjs
     */
    public interface ImageCache {
        public Bitmap getBitmap(String url);

        public void putBitmap(String url, Bitmap bitmap);
    }
}
