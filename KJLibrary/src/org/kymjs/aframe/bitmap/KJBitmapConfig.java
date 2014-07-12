package org.kymjs.aframe.bitmap;

import android.graphics.Bitmap;

public class KJBitmapConfig {
    /** 网络连接等待时间 */
    int timeOut = 5000;
    /** 内存缓存大小 */
    int memoryCacheSize;

    /** 图片的宽度 */
    int width = 1000; // 不足1000则显示图片默认大小
    /** 图片的高度 */
    int height = 1000; // 不足1000则显示图片默认大小
    /** 载入时的图片 */
    Bitmap loadingBitmap;

    public KJBitmapConfig() {
        memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
    }
}
