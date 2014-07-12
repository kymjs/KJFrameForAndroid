package org.kymjs.aframe.bitmap;

import android.graphics.Bitmap;

/**
 * bitmapLibrary的配置器
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class KJBitmapConfig {
    /** 网络连接等待时间 */
    public int timeOut = 5000;
    /** 内存缓存大小 */
    public int memoryCacheSize;

    /** 图片的宽度 */
    public int width = 1000; // 不足1000则显示图片默认大小
    /** 图片的高度 */
    public int height = 1000; // 不足1000则显示图片默认大小
    /** 载入时的图片 */
    public Bitmap loadingBitmap;
    /** 图片载入状态将会回调相应的方法 */
    public BitmapCallBack callBack;
    /** 是否开启载入图片时显示环形progressBar效果 */
    public boolean openProgress = false;

    public KJBitmapConfig() {
        memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
    }
}
