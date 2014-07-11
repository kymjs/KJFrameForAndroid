package org.kymjs.aframe.bitmap;

import android.graphics.Bitmap;

public class KJBitmapConfig {
    int memoryCacheSize;
    Bitmap loadingBitmap;

    public KJBitmapConfig() {
        memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
    }
}
