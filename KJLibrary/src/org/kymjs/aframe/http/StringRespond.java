package org.kymjs.aframe.http;

/**
 * 网络请求中响应应字符串的回调接口类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-14
 */
public abstract class StringRespond implements I_HttpRespond {

    @Override
    public void loading(long count, long current) {};

    @Override
    public void success(Object t) {
        onSuccess(t.toString());
    };

    @Override
    public void failure(Throwable t, int errorNo, String strMsg) {};

    public abstract void onSuccess(String t);
}
