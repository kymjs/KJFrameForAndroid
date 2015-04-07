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
package org.kymjs.kjframe.bitmap.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * LRU算法实现的磁盘缓存器，只是一个简单版本的实现。 可以在ICS源代码中找到一个更强大和高效的磁盘LRU缓存解决方案
 * (libcore/luni/src/main/java/libcore/io/DiskLruCache.java). <br>
 * 
 * <b>创建时间</b> 2014-7-16
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 */
public final class DiskCache {
    private boolean debug = KJLoger.IS_DEBUG;
    private static long maxSize;
    // constant
    private static String CACHE_FILENAME_PREFIX = BitmapConfig.CACHE_FILENAME_PREFIX;
    private static final int MAX_REMOVALS = 4;
    private static final int INITIAL_CAPACITY = 32;
    private static final float LOAD_FACTOR = 0.75f;
    private final File mFileDir;
    private CompressFormat mCompressFormat = CompressFormat.PNG;

    private int cacheSize = 0;
    private int cacheByteSize = 0;
    private final int maxCacheItemSize = 8192; // 8192 item default
    private int mCompressQuality = 70;

    private final Map<String, String> mLinkedHashMap = Collections
            .synchronizedMap(new LinkedHashMap<String, String>(
                    INITIAL_CAPACITY, LOAD_FACTOR, true));
    /** 用于标识缓存路径下哪些是需要的缓存文件(也就是Cache头部标识) */
    private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith(CACHE_FILENAME_PREFIX);
        }
    };

    public DiskCache(String folderName) {
        this(folderName, 16 * 1024 * 1024, false);
    }

    public DiskCache(String folderName, long maxByteSize) {
        this(folderName, maxByteSize, false);
    }

    public DiskCache(String folderName, long maxByteSize, boolean isDebug) {
        mFileDir = FileUtils.getSaveFolder(folderName);
        maxSize = maxByteSize;
        debug = isDebug;
    }

    /*********************************************************************/

    /**
     * 将bitmap写入文件缓存，然后再put
     * 
     * @param key
     * @param data
     */
    public void put(String key, Bitmap data) {
        synchronized (mLinkedHashMap) {
            if (mLinkedHashMap.get(key) == null) {
                try {
                    final String file = createFilePath(mFileDir, key);
                    if (writeBitmapToFile(data, file)) { // 如果成功将图片写入文件
                        put(key, file);
                        debug("put - Added cache file, " + file);
                        flushCache();
                    }
                } catch (final FileNotFoundException e) {
                    debug("Error in put: " + e.getMessage());
                } catch (final IOException e) {
                    debug("Error in put: " + e.getMessage());
                }
            }
        }
    }

    private void put(String key, String file) {
        mLinkedHashMap.put(key, file);
        cacheSize = mLinkedHashMap.size();
        cacheByteSize += new File(file).length();
    }

    /**
     * 刷新缓存，当前Cache占用空间超过了最大空间，从最少使用的entry开始删除，直到占用空间小于标准
     */
    private void flushCache() {
        Entry<String, String> eldestEntry;
        File eldestFile;
        long eldestFileSize;
        int count = 0;
        while (count < MAX_REMOVALS
                && (cacheSize > maxCacheItemSize || cacheByteSize > maxSize)) {
            Iterator<Entry<String, String>> iterator = mLinkedHashMap
                    .entrySet().iterator();
            if (!iterator.hasNext()) {
                return;
            }
            eldestEntry = iterator.next();
            eldestFile = new File(eldestEntry.getValue());
            eldestFileSize = eldestFile.length();
            mLinkedHashMap.remove(eldestEntry.getKey());
            eldestFile.delete();
            cacheSize = mLinkedHashMap.size();
            cacheByteSize -= eldestFileSize;
            count++;
            debug("flushCache - Removed cache file, " + eldestFile + ", "
                    + eldestFileSize);
        }
    }

    /**
     * 从缓存读取bitmap
     * 
     * @return The bitmap or null if not found
     */
    public Bitmap get(String key) {
        synchronized (mLinkedHashMap) {
            final String file = mLinkedHashMap.get(key);
            if (file != null) {
                debug("Disk cache hit");
                return BitmapFactory.decodeFile(file);
            } else {
                final String existingFile = createFilePath(mFileDir, key);
                if (new File(existingFile).exists()) {
                    put(key, existingFile);
                    debug("Disk cache hit (existing file)");
                    return BitmapFactory.decodeFile(existingFile);
                }
            }
            return null;
        }
    }

    /**
     * 从缓存读取byte数组
     * 
     * @return The bitmap or null if not found
     */
    public byte[] getByteArray(String key) {
        synchronized (mLinkedHashMap) {
            final String file = mLinkedHashMap.get(key);
            byte[] data = null;
            FileInputStream fis = null;
            if (file != null) {
                debug("Disk cache hit");
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    fis = null;
                    e.printStackTrace();
                }
                data = FileUtils.input2byte(fis);
            } else {
                final String existingFile = createFilePath(mFileDir, key);
                if (new File(existingFile).exists()) {
                    put(key, existingFile);
                    debug("Disk cache hit (existing file)");
                    try {
                        fis = new FileInputStream(existingFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        fis = null;
                    }
                    data = FileUtils.input2byte(fis);
                }
            }
            FileUtils.closeIO(fis);
            return data;
        }
    }

    /**
     * 检测key是否有对应的value存在
     * 
     * @param key
     *            The unique identifier for the bitmap
     * @return true if found, false otherwise
     */
    public boolean containsKey(String key) {
        if (mLinkedHashMap.containsKey(key)) {
            return true;
        }

        // 检测key是否对应一个实际的文件
        final String existingFile = createFilePath(mFileDir, key);
        if (new File(existingFile).exists()) {
            // 如果找到key对应的实际文件，则加入map
            put(key, existingFile);
            return true;
        }
        return false;
    }

    /**
     * 清除全部缓存
     */
    public void clearCache() {
        clearCache(mFileDir);
    }

    /**
     * 本类不可以被直接调用应该通过调用
     * {@link DiskLruCache#clearCache(android.content.Context, String)} or
     * {@link DiskLruCache#clearCache()} 来调用它.
     * 
     * @param cacheDir
     *            The directory to remove the cache files from
     */
    private static void clearCache(File cacheDir) {
        final File[] files = cacheDir.listFiles(cacheFileFilter);
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    /**
     * Removes all disk cache entries from the application cache directory in
     * the uniqueName sub-directory.
     * 
     * @param context
     *            The context to use
     * @param uniqueName
     *            A unique cache directory name to append to the app cache
     *            directory
     */
    public static void clearCache(Context context, String uniqueName) {
        File cacheDir = getDiskCacheDir(context, uniqueName);
        clearCache(cacheDir);
    }

    /**
     * 查找一个可用的缓存文件夹 (首先查找外部存储，然后再找内部存储).
     * 
     * @param context
     *            The context to use
     * @param uniqueName
     *            A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !isExternalStorageRemovable() ? getExternalCacheDir(context)
                .getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 返回文件的绝对路径
     * 
     * @param cacheDir
     *            文件在SD卡的目录
     * @param fileName
     *            文件名
     * @return
     */
    public static String createFilePath(File cacheDir, String fileName) {
        try {
            return cacheDir.getAbsolutePath() + File.separator
                    + CACHE_FILENAME_PREFIX
                    + URLEncoder.encode(fileName.replace("*", ""), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            KJLoger.debug(e.getMessage());
        }
        return null;
    }

    /**
     * 在缓存目录下创建一个缓存文件
     * 
     * @param fileName
     *            缓存文件名
     */
    public String createFilePath(String fileName) {
        return createFilePath(mFileDir, fileName);
    }

    /**
     * 设置压缩格式与压缩质量
     * 
     * @param compressFormat
     * @param quality
     */
    public void setCompressParams(CompressFormat compressFormat, int quality) {
        mCompressFormat = compressFormat;
        mCompressQuality = quality;
    }

    /**
     * 图片写入文件
     */
    private boolean writeBitmapToFile(Bitmap bitmap, String file)
            throws IOException, FileNotFoundException {
        if (bitmap == null)
            return false;
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file),
                    IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /******************* 替换原DiskLruCache.jar中Utils.java的函数 ********************/
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static long getUsableSpace(File path) {
        /*
         * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
         * return path.getUsableSpace(); }
         */
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    private static boolean isExternalStorageRemovable() {
        /*
         * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
         * return Environment.isExternalStorageRemovable(); }
         */
        return true;
    }

    private static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    private static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    /******************************* 辅助函数 ************************************/

    private void debug(String msg) {
        if (debug) {
            KJLoger.debug(msg);
        }
    }
}
