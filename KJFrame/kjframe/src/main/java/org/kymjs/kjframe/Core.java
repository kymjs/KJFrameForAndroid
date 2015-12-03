package org.kymjs.kjframe;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.http.DownloadTaskQueue;
import org.kymjs.kjframe.http.FileRequest;
import org.kymjs.kjframe.http.FormRequest;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.http.JsonRequest;
import org.kymjs.kjframe.http.Request;
import org.kymjs.kjframe.http.Request.HttpMethod;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;


/**
 * 对KJHttp与KJBitmap做封装
 *
 * @author kymjs (http://www.kymjs.com/) on 11/20/15.
 */
public final class Core {
    private Core() {
    }

    private static final class SingletonHolder {
        private static final KJHttp kjHttp = new KJHttp(null);
        private static final KJBitmap kjBitmap = new KJBitmap(kjHttp, null);
    }

    public static KJHttp getKJHttp() {
        return SingletonHolder.kjHttp;
    }

    public static KJBitmap getKJBitmap() {
        return SingletonHolder.kjBitmap;
    }

    /**
     * 发起get请求
     *
     * @param url      地址
     * @param callback 请求中的回调方法
     */
    public static Request<byte[]> get(String url, HttpCallBack callback) {
        return get(url, null, callback);
    }

    /**
     * 发起get请求
     *
     * @param url      地址
     * @param params   参数集
     * @param callback 请求中的回调方法
     */
    public static Request<byte[]> get(String url, HttpParams params,
                                      HttpCallBack callback) {
        return get(url, params, true, callback);
    }

    /**
     * 发起get请求
     *
     * @param url      地址
     * @param params   参数集
     * @param callback 请求中的回调方法
     * @param useCache 是否缓存本条请求
     */
    public static Request<byte[]> get(String url, HttpParams params, boolean useCache,
                                      HttpCallBack callback) {
        if (params != null) {
            url += params.getUrlParams();
        } else {
            params = new HttpParams();
        }
        Request<byte[]> request = new FormRequest(Request.HttpMethod.GET, url, params,
                callback);
        request.setShouldCache(useCache);
        getKJHttp().doRequest(request);
        return request;
    }

    /**
     * 发起post请求
     *
     * @param url      地址
     * @param params   参数集
     * @param callback 请求中的回调方法
     */
    public static Request<byte[]> post(String url, HttpParams params,
                                       HttpCallBack callback) {
        return post(url, params, true, callback);
    }

    /**
     * 发起post请求
     *
     * @param url      地址
     * @param params   参数集
     * @param callback 请求中的回调方法
     * @param useCache 是否缓存本条请求
     */
    public static Request<byte[]> post(String url, HttpParams params,
                                       boolean useCache, HttpCallBack callback) {
        Request<byte[]> request = new FormRequest(HttpMethod.POST, url, params,
                callback);
        request.setShouldCache(useCache);
        getKJHttp().doRequest(request);
        return request;
    }

    /**
     * 使用JSON传参的post请求
     *
     * @param url      地址
     * @param params   参数集
     * @param callback 请求中的回调方法
     * @param useCache 是否缓存本条请求
     */
    public Request<byte[]> jsonPost(String url, HttpParams params,
                                    boolean useCache, HttpCallBack callback) {
        Request<byte[]> request = new JsonRequest(HttpMethod.POST, url, params,
                callback);
        request.setShouldCache(useCache);
        getKJHttp().doRequest(request);
        return request;
    }

    /**
     * 使用JSON传参的get请求
     *
     * @param url      地址
     * @param params   参数集
     * @param callback 请求中的回调方法
     * @param useCache 是否缓存本条请求
     */
    public Request<byte[]> jsonGet(String url, HttpParams params,
                                   boolean useCache, HttpCallBack callback) {
        Request<byte[]> request = new JsonRequest(HttpMethod.GET, url, params,
                callback);
        request.setShouldCache(useCache);
        getKJHttp().doRequest(request);
        return request;
    }

    /**
     * 下载
     *
     * @param storeFilePath 文件保存路径。注，必须是一个file路径不能是folder
     * @param url           下载地址
     * @param callback      请求中的回调方法
     */
    public static DownloadTaskQueue download(String storeFilePath, String url,
                                             HttpCallBack callback) {
        FileRequest request = new FileRequest(storeFilePath, url, callback);
        HttpConfig config = getKJHttp().getConfig();
        request.setConfig(config);
        config.mController.add(request);
        return config.mController;
    }

    public void destroy() {
        getKJBitmap().finish();
        getKJHttp().destroy();
    }

    public static class Builder {
        private String url;
        private HttpParams params;
        private boolean useCache = true;
        private Request<?> request;
        private HttpConfig httpConfig;
        private int httpMethod;
        private int contentType;
        private int cacheTime = 5;
        private HttpCallBack callback;

        public static final int DEF_WIDTH_HEIGHT = -100;
        private View imageView;
        private int width = DEF_WIDTH_HEIGHT;
        private int height = DEF_WIDTH_HEIGHT;
        private Drawable loadBitmap;
        private Drawable errorBitmap;
        private int loadBitmapRes;
        private int errorBitmapRes;
        private BitmapCallBack bitmapCallBack;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder params(HttpParams params) {
            this.params = params;
            return this;
        }

        public Builder useCache(boolean useCache) {
            this.useCache = useCache;
            return this;
        }

        public Builder setRequest(Request<?> request) {
            this.request = request;
            return this;
        }

        public Builder httpConfig(HttpConfig httpConfig) {
            this.httpConfig = httpConfig;
            return this;
        }

        public Builder cacheTime(int cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        public Builder httpMethod(int httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder contentType(int contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder callback(HttpCallBack callback) {
            this.callback = callback;
            return this;
        }

        public Builder view(View imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder size(int width, int height) {
            this.height = height;
            this.width = width;
            return this;
        }

        public Builder loadBitmapRes(int loadBitmapRes) {
            this.loadBitmapRes = loadBitmapRes;
            return this;
        }

        public Builder errorBitmapRes(int errorBitmapRes) {
            this.errorBitmapRes = errorBitmapRes;
            return this;
        }

        public Builder loadBitmap(Drawable loadBitmap) {
            this.loadBitmap = loadBitmap;
            return this;
        }

        public Builder errorBitmap(Drawable errorBitmap) {
            this.errorBitmap = errorBitmap;
            return this;
        }

        public Builder bitmapCallBack(BitmapCallBack bitmapCallBack) {
            this.bitmapCallBack = bitmapCallBack;
            return this;
        }

        public void doTask() {
            if (imageView == null) {
                doHttp();
            } else {
                display();
            }
        }

        public void display() {
            if (StringUtils.isEmpty(url)) {
                KJLoger.debug("image url is empty");
                KJBitmap.doFailure(imageView, errorBitmap, errorBitmapRes);
                if (callback != null)
                    callback.onFailure(-1, "image url is empty");
                return;
            }

            if (width == DEF_WIDTH_HEIGHT && height == DEF_WIDTH_HEIGHT) {
                width = imageView.getWidth();
                height = imageView.getHeight();
                if (width <= 0) {
                    width = DensityUtils.getScreenW(imageView.getContext()) / 2;
                }
                if (height <= 0) {
                    height = DensityUtils.getScreenH(imageView.getContext()) / 2;
                }
            } else if (width == DEF_WIDTH_HEIGHT) {
                width = DensityUtils.getScreenW(imageView.getContext());
            } else if (height == DEF_WIDTH_HEIGHT) {
                height = DensityUtils.getScreenH(imageView.getContext());
            }

            if (loadBitmapRes == 0 && loadBitmap == null) {
                loadBitmap = new ColorDrawable(0xFFCFCFCF);
            }

            getKJBitmap().doDisplay(imageView, url, width, height, loadBitmap, loadBitmapRes,
                    errorBitmap, errorBitmapRes, bitmapCallBack);
        }

        /**
         * 做Http请求
         */
        private void doHttp() {
            if (request != null) {
                getKJHttp().doRequest(request);
                return;
            }
            if (httpConfig != null) {
                getKJHttp().setConfig(httpConfig);
            } else {
                httpConfig = new HttpConfig();
                httpConfig.cacheTime = cacheTime;
                getKJHttp().setConfig(httpConfig);
            }
            if (callback == null) {
                callback = new HttpCallBack() {
                };
            }

            if (params == null) {
                params = new HttpParams();
            } else {
                if (httpMethod == HttpMethod.GET)
                    url += params.getUrlParams();
            }

            if (contentType == KJHttp.ContentType.FORM) {
                FormRequest request = new FormRequest(httpMethod, url, params, callback);
                request.setShouldCache(useCache);
                getKJHttp().doRequest(request);
            } else if (contentType == KJHttp.ContentType.JSON) {
                JsonRequest request = new JsonRequest(httpMethod, url, params, callback);
                request.setShouldCache(useCache);
                getKJHttp().doRequest(request);
            }
        }
    }
}
