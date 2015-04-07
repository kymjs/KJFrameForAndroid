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
import org.kymjs.kjframe.utils.KJLoger;

import android.app.Activity;

/**
 * Bitmap配置器
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class BitmapConfig {

    public boolean isDEBUG = KJLoger.DEBUG_LOG;
    public int memoryCacheSize;

    public Activity cxt;

    public static String CACHEPATH = "KJLibrary/image";
    public static String CACHE_FILENAME_PREFIX = "KJLibrary_";
    public int diskCacheSize = 41943040; // 40M

    public I_ImageLoader downloader;

    public BitmapConfig() {
        memoryCacheSize = (int) (Runtime.getRuntime().maxMemory());
        downloader = new BitmapDownloader(this);
    }

    public void setDefaultWidth(int w) {
        if (w > 0) {
            BitmapCreate.DEFAULT_W = w;
        }
    }

    public int getDefaultWidth() {
        return BitmapCreate.DEFAULT_W;
    }

    public void setDefaultHeight(int h) {
        if (h > 0) {
            BitmapCreate.DEFAULT_H = h;
        }
    }

    public int getDefaultHeight() {
        return BitmapCreate.DEFAULT_H;
    }
}
