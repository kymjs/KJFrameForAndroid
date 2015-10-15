/*
 * Copyright (c) 2014, 张涛.
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
package org.kymjs.kjframe.http;

import android.net.TrafficStats;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.utils.KJLoger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * 一个请求基类
 *
 * @param <T> Http返回类型
 * @author kymjs (http://www.kymjs.com/) .
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * 默认编码 {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * 支持的请求方式
     */
    public interface HttpMethod {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    private static final long SLOW_REQUEST_THRESHOLD_MS = 3000; // 请求超时时间

    private final String mUrl;
    private final int mDefaultTrafficStatsTag; // 默认tag {@link TrafficStats}
    private Integer mSequence; // 本次请求的优先级

    private final int mMethod; // 请求方式
    private final long mRequestBirthTime = 0;// 用于转储慢的请求。

    private boolean mShouldCache = true; // 是否缓存本次请求
    private boolean mCanceled = false; // 是否取消本次请求
    private boolean mResponseDelivered = false; // 是否再次分发本次响应

    protected final HttpCallBack mCallback;
    protected KJHttp mRequestQueue;
    protected HttpConfig mConfig;

    private Object mTag; // 本次请求的tag，方便在取消时找到它
    private Cache.Entry mCacheEntry = null;

    public Request(int method, String url, HttpCallBack callback) {
        mMethod = method;
        mUrl = url;
        mCallback = callback;

        mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(url);
    }

    public HttpCallBack getCallback() {
        return mCallback;
    }

    public int getMethod() {
        return mMethod;
    }

    public void setConfig(HttpConfig config) {
        this.mConfig = config;
    }

    /**
     * 设置tag，方便取消本次请求时能找到它
     */
    public Request<?> setTag(Object tag) {
        mTag = tag;
        return this;
    }

    /**
     * 设置tag，方便取消本次请求时能找到它
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * @return A tag for use with {@link TrafficStats#setThreadStatsTag(int)}
     */
    public int getTrafficStatsTag() {
        return mDefaultTrafficStatsTag;
    }

    /**
     * @return The hashcode of the URL's host component, or 0 if there is none.
     */
    private static int findDefaultTrafficStatsTag(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String host = uri.getHost();
                if (host != null) {
                    return host.hashCode();
                }
            }
        }
        return 0;
    }

    /**
     * 通知请求队列，本次请求已经完成
     */
    public void finish(final String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }
        long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
        if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
            KJLoger.debug("%d ms: %s", requestTime, this.toString());
        }
    }

    public Request<?> setRequestQueue(KJHttp requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    public final Request<?> setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException(
                    "getSequence called before setSequence");
        }
        return mSequence;
    }

    public String getUrl() {
        return mUrl;
    }

    public abstract String getCacheKey();

    public Request<?> setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    public void cancel() {
        mCanceled = true;
    }

    public void resume() {
        mCanceled = false;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public Map<String, String> getParams() {
        return null;
    }

    public Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset="
                + getParamsEncoding();
    }

    /**
     * 返回Http请求的body
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * 对中文参数做URL转码
     */
    private byte[] encodeParameters(Map<String, String> params,
                                    String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(),
                        paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(),
                        paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: "
                    + paramsEncoding, uee);
        }
    }

    public final Request<?> setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
        return this;
    }

    public boolean shouldCache() {
        return mShouldCache;
    }

    /**
     * 本次请求的优先级，四种
     */
    public enum Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public final int getTimeoutMs() {
        return HttpConfig.TIMEOUT;
    }

    /**
     * 标记为已经分发过的
     */
    public void markDelivered() {
        mResponseDelivered = true;
    }

    /**
     * 是否已经被分发过
     */
    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }

    /**
     * 将网络请求执行器(NetWork)返回的NetWork响应转换为Http响应
     *
     * @param response 网络请求执行器(NetWork)返回的NetWork响应
     * @return 转换后的HttpRespond, or null in the case of an error
     */
    abstract public Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     * 如果需要根据不同错误做不同的处理策略，可以在子类重写本方法
     */
    protected KJHttpException parseNetworkError(KJHttpException volleyError) {
        return volleyError;
    }

    /**
     * 将Http请求结果分发到主线程
     *
     * @param response {@link #parseNetworkResponse(NetworkResponse)}
     */
    abstract protected void deliverResponse(Map<String, String> headers,
                                            T response);

    /**
     * 响应Http请求异常的回调
     *
     * @param error 原因
     */
    public void deliverError(KJHttpException error) {
        if (mCallback != null) {
            int errorNo;
            String strMsg;
            if (error != null) {
                if (error.networkResponse != null) {
                    errorNo = error.networkResponse.statusCode;
                } else {
                    errorNo = -1;
                }
                strMsg = error.getMessage();
            } else {
                errorNo = -1;
                strMsg = "unknow";
            }
            mCallback.onFailure(errorNo, strMsg);
        }
    }

    /**
     * Http请求成功后，在异步调用本方法，本方法执行完成才会继续调用onSuccess()
     *
     * @param t 请求成功后的数据
     */
    protected void onAsyncSuccess(byte[] t) {
        if (mCallback != null) {
            mCallback.onSuccessInAsync(t);
        }
    }

    /**
     * Http请求完成(不论成功失败)
     */
    public void requestFinish() {
        mCallback.onFinish();
    }

    /**
     * 用于线程优先级排序
     */
    @Override
    public int compareTo(Request<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();
        return left == right ? this.mSequence - other.mSequence : right
                .ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
        String trafficStatsTag = "0x"
                + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag
                + " " + getPriority() + " " + mSequence;
    }
}
