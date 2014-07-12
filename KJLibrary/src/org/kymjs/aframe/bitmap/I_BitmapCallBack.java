package org.kymjs.aframe.bitmap;

import android.view.View;

/**
 * BitmapLibrary中的回调方法
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public abstract class I_BitmapCallBack {
    /** bitmap载入时将回调 */
    public void imgLoading(final View view) {};

    /** bitmap载入完成将回调 */
    public void imgLoadSuccess(final View view) {};

    /** bitmap载入失败将回调 */
    public void imgLoadFailure(final String url, String msg) {};

}
