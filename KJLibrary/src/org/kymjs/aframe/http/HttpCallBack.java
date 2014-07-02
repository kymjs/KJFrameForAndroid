package org.kymjs.aframe.http;

/**
 * 网络请求回调接口类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-6-5
 */
public abstract class HttpCallBack<T> {

    private boolean progress = false;
    private int rate = 1000 * 1;// 每秒

    public boolean isProgress() {
        return progress;
    }

    public int getRate() {
        return rate;
    }

    /**
     * 设置进度,而且只有设置了这个了以后，onLoading才能有效。
     * 
     * @param progress
     *            是否启用进度显示
     * @param rate
     *            进度更新频率
     */
    public HttpCallBack<T> progress(boolean progress, int rate) {
        this.progress = progress;
        this.rate = rate;
        return this;
    }

    /**
     * onLoading方法有效progress
     * 
     * @param count
     * @param current
     */
    public void onLoading(long count, long current) {
    };

    public void onSuccess(T t) {
    };

    public void onFailure(Throwable t, int errorNo, String strMsg) {
    };
}
