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

import java.io.File;

import org.kymjs.kjframe.bitmap.ImageDisplayer.ImageCache;
import org.kymjs.kjframe.http.Cache;
import org.kymjs.kjframe.http.DiskCache;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;

/**
 * Bitmap配置器
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class BitmapConfig {

    public boolean isDEBUG = KJLoger.DEBUG_LOG;

    public static String CACHEPATH = "KJLibrary/image";

    /** 磁盘缓存大小 */
    public static int DISK_CACHE_SIZE = 10 * 1024 * 1024;
    /** 磁盘缓存器 **/
    public static Cache mCache;
    public static ImageCache mMemoryCache;

    public int cacheTime = 1440000;

    /** 已使用全新的DiskCache，不再需要prefix参数 */
    @Deprecated
    public static String CACHE_FILENAME_PREFIX = "KJLibrary_";

    public BitmapConfig() {
        File folder = FileUtils.getSaveFolder(CACHEPATH);
        if (mCache == null) {
            mCache = new DiskCache(folder, DISK_CACHE_SIZE);
            mMemoryCache = new BitmapMemoryCache();
        }
    }
}
