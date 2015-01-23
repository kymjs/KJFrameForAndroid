/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
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

import org.kymjs.kjframe.bitmap.helper.BitmapCreate;

import android.util.Log;

/**
 * Bitmap配置器
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class BitmapConfig {

    public boolean isDEBUG = true;
    public int memoryCacheSize;

    public String cachePath = "KJLibrary/image";
    public int diskCacheSize = 41943040; // 40M

    public I_ImageLoader downloader;

    public BitmapConfig() {
        memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        downloader = new BitmapDownloader(this, 0, 0);
    }

    public void setDefaultWidth(int w) {
        if (w > 0) {
            BitmapCreate.DEFAULT_W = w;
        } else {
            Log.e("KJFrame", "default width is not 0");
        }
    }

    public int getDefaultWidth() {
        return BitmapCreate.DEFAULT_W;
    }

    public void setDefaultHeight(int h) {
        if (h > 0) {
            BitmapCreate.DEFAULT_H = h;
        } else {
            Log.e("KJFrame", "default height is not 0");
        }
    }

    public int getDefaultHeight() {
        return BitmapCreate.DEFAULT_H;
    }
}
