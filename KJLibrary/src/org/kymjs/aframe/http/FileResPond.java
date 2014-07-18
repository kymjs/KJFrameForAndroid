package org.kymjs.aframe.http;

/**
 * 网络请求中响应文件的回调接口类
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-14
 */
public class FileResPond implements I_HttpRespond {

    @Override
    public void loading(long count, long current) {}

    @Override
    public void success(Object t) {}

    @Override
    public void failure(Throwable t, int errorNo, String strMsg) {}

}
