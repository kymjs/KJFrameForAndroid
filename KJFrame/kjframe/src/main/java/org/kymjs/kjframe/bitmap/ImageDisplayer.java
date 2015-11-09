/*
 * Copyright (c) 2015, 张涛.
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

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.KJHttpException;
import org.kymjs.kjframe.http.Request;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 图片显示器
 *
 * @author kymjs (https://www.kymjs.com/)
 */
public class ImageDisplayer {
    private KJHttp kjHttp; // 使用KJHttp的线程池执行队列去加载图片

    private final ImageCache mMemoryCache; // 内存缓存器
    private final long mResponseDelayMs;

    private Runnable mRunnable;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // 正在请求的事件
    private final HashMap<String, ImageRequestEven> mRequestsMap = new HashMap<>();
    // 已经请求完成，待处理的事件
    private final HashMap<String, ImageRequestEven> mResponsesMap = new HashMap<>();

    /**
     * 创建一个图片显示器
     *
     * @param bitmapConfig KJBitmap配置器
     */
    public ImageDisplayer(KJHttp http, BitmapConfig bitmapConfig) {
        kjHttp = http;
        mMemoryCache = BitmapConfig.mMemoryCache;
        mResponseDelayMs = bitmapConfig.delayTime;
    }

    /**
     * 判断指定图片是否已经被缓存
     *
     * @param requestUrl 图片地址
     * @return 图片是否已经被缓存
     */
    public boolean isCached(String requestUrl) {
        throwIfNotOnMainThread();
        return mMemoryCache.getBitmap(requestUrl) != null;
    }

    /**
     * 加载一张图片
     *
     * @param requestUrl 图片地址
     * @param maxWidth   图片最大宽度(如果网络图片大于这个宽度则缩放至这个大小)
     * @param maxHeight  图片最大高度
     * @param callback   回调
     * @return 加载的图片封装
     */
    public ImageBale get(String requestUrl, int maxWidth, int maxHeight,
                         BitmapCallBack callback) {
        throwIfNotOnMainThread();
        callback.onPreLoad();

        Bitmap cachedBitmap = mMemoryCache.getBitmap(requestUrl);
        if (cachedBitmap != null) {
            ImageBale container = new ImageBale(cachedBitmap, requestUrl, null);
            callback.onSuccess(cachedBitmap);
            callback.onFinish();
            return container;
        } else {
            // 开始加载网络图片的标志
            callback.onDoHttp();
        }

        ImageBale imageBale = new ImageBale(null, requestUrl, callback);
        ImageRequestEven request = mRequestsMap.get(requestUrl);
        if (request != null) {
            request.addImageBale(imageBale);
            return imageBale;
        }

        Request<Bitmap> newRequest = makeImageRequest(requestUrl, maxWidth,
                maxHeight);
        newRequest.setConfig(kjHttp.getConfig());
        kjHttp.doRequest(newRequest);
        mRequestsMap.put(requestUrl,
                new ImageRequestEven(newRequest, imageBale));
        return imageBale;
    }

    /**
     * 创建一个网络请求
     */
    protected Request<Bitmap> makeImageRequest(final String requestUrl,
                                               int maxWidth, int maxHeight) {
        return new ImageRequest(requestUrl, maxWidth, maxHeight,
                new HttpCallBack() {
                    @Override
                    public void onSuccess(Bitmap t) {
                        super.onSuccess(t);
                        onGetImageSuccess(requestUrl, t);
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        onGetImageError(requestUrl, new KJHttpException(strMsg));
                    }
                });
    }

    /**
     * 从网络获取bitmap成功时调用
     *
     * @param url    缓存key
     * @param bitmap 获取到的bitmap
     */
    protected void onGetImageSuccess(String url, Bitmap bitmap) {
        mMemoryCache.putBitmap(url, bitmap);
        // 从正在请求的列表中移除这个已完成的请求
        ImageRequestEven request = mRequestsMap.remove(url);

        if (request != null) {
            request.mBitmapRespond = bitmap;
            batchResponse(url, request);
        }
    }

    /**
     * 从网络获取bitmap失败时调用
     *
     * @param url   缓存key
     * @param error 失败原因
     */
    protected void onGetImageError(String url, KJHttpException error) {
        // 从正在请求的列表中移除这个已完成的请求
        ImageRequestEven request = mRequestsMap.remove(url);
        if (request != null) {
            request.setError(error);
            batchResponse(url, request);
        }
    }

    /**************************************************************************/

    /**
     * 对一个图片的封装，包含了这张图片所需要携带的信息
     *
     * @author kymjs
     */
    public class ImageBale {
        private Bitmap mBitmap;
        private final String mRequestUrl;
        private final BitmapCallBack mCallback;

        public ImageBale(Bitmap bitmap, String requestUrl,
                         BitmapCallBack callback) {
            mBitmap = bitmap;
            mRequestUrl = requestUrl;
            mCallback = callback;
        }

        public void cancelRequest() {
            if (mCallback == null) {
                return;
            }

            ImageRequestEven request = mRequestsMap.get(mRequestUrl);
            if (request != null) {
                boolean canceled = request.removeBale(this);
                if (canceled) {
                    mRequestsMap.remove(mRequestUrl);
                }
            } else {
                request = mResponsesMap.get(mRequestUrl);
                if (request != null) {
                    request.removeBale(this);
                    if (request.mImageBales.size() == 0) {
                        mResponsesMap.remove(mRequestUrl);
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
    private void batchResponse(String url, final ImageRequestEven request) {
        mResponsesMap.put(url, request);
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
     * @param url key
     */
    public void cancel(String url) {
        kjHttp.cancel(url);
    }

    /**
     * 内存缓存接口定义
     *
     * @author kymjs
     */
    public interface ImageCache {
        Bitmap getBitmap(String url);

        void remove(String key);

        void clean();

        void putBitmap(String url, Bitmap bitmap);
    }
}
