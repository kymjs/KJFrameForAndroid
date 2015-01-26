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
package org.kymjs.kjframe.plugin;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * CJFrame的一个工具类
 * 
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJTool {

    /**
     * 获取一个apk的信息
     * 
     * @param cxt
     *            应用上下文
     * @param apkPath
     *            apk所在绝对路径
     * @return
     */
    public static PackageInfo getAppInfo(Context cxt, String apkPath)
            throws NameNotFoundException {
        PackageManager pm = cxt.getPackageManager();
        PackageInfo pkgInfo = null;
        pkgInfo = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        return pkgInfo;
    }

    /**
     * 获取一个apk中，在Manifest.xml中声明的第一个Activity的信息
     * 
     * @param cxt
     *            应用上下文
     * @param apkPath
     *            apk所在绝对路径
     * @return
     */
    public static ActivityInfo getActivityInfo(Context cxt, String apkPath)
            throws NameNotFoundException {
        return getActivityInfo(getAppInfo(cxt, apkPath), 0);
    }

    /**
     * 获取一个apk中，在Manifest.xml中声明的第index个Activity的信息<br>
     * <b>注意</b>index的大小不正确可能会报ArrayIndexOutOfBoundsException
     * 
     * @param cxt
     *            应用上下文
     * @param apkPath
     *            apk所在绝对路径
     * @param index
     *            要获取的Activity在Manifest.xml中声明的序列（从0开始）
     * @throws ArrayIndexOutOfBoundsException
     *             index超出范围会报
     * @return
     */
    public static ActivityInfo getActivityInfo(Context cxt, String apkPath,
            int index) throws NameNotFoundException {
        return getActivityInfo(getAppInfo(cxt, apkPath), index);
    }

    /**
     * 获取一个apk中，在Manifest.xml中声明的第index个Activity的信息<br>
     * <b>注意</b>index的大小不正确可能会报ArrayIndexOutOfBoundsException
     * 
     * @param pkgInfo
     *            Activity所在应用的PackageInfo
     * @param index
     *            Activity在插件Manifest.xml中的序列（从0开始）
     * @throws ArrayIndexOutOfBoundsException
     *             index超出范围会报
     * @return
     */
    public static ActivityInfo getActivityInfo(PackageInfo pkgInfo, int index) {
        return pkgInfo.activities[index];
    }

    /**
     * 获取应用图标
     * 
     * @param cxt
     *            应用上下文
     * @param apkPath
     *            apk所在绝对路径
     * @return
     */
    public static Drawable getAppIcon(Context cxt, String apkPath)
            throws NameNotFoundException {
        PackageManager pm = cxt.getPackageManager();
        PackageInfo pkgInfo = getAppInfo(cxt, apkPath);
        if (pkgInfo == null) {
            return null;
        } else {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            if (Build.VERSION.SDK_INT >= 8) {
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
            }
            return pm.getApplicationIcon(appInfo);
        }
    }

    /**
     * 获取指定APK应用名
     * 
     * @param cxt
     *            应用上下文
     * @param apkPath
     *            apk所在绝对路径
     * @return
     */
    public static CharSequence getAppName(Context cxt, String apkPath)
            throws NameNotFoundException {
        PackageManager pm = cxt.getPackageManager();
        PackageInfo pkgInfo = getAppInfo(cxt, apkPath);
        if (pkgInfo == null) {
            return null;
        } else {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            if (Build.VERSION.SDK_INT >= 8) {
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
            }
            return pm.getApplicationLabel(appInfo);
        }
    }

    /**
     * 获取到指定包名的插件的资源属性
     * 
     * @param cxt
     *            当前Context
     * @param pkgName
     *            要打开的资源所在的包名
     * @return
     */
    public static Resources getResFromPkgName(Context cxt, String pkgName)
            throws NameNotFoundException {
        return getCxtFromPkgName(cxt, pkgName).getResources();
    }

    /**
     * 获取到指定包名的插件的资源属性
     * 
     * @param cxt
     *            当前Context
     * @param apkPath
     *            要打开的apk所在的路径
     * @return
     */
    public static Resources getResFromApkPath(Context cxt, String apkPath)
            throws NameNotFoundException {
        return getCxtFromApkPath(cxt, apkPath).getResources();
    }

    /**
     * 获取指定APP包名的Context对象
     * 
     * @param cxt
     *            当前Context
     * @param pkgName
     *            要打开的资源所在的包名
     * @return
     */
    public static Context getCxtFromPkgName(Context cxt, String pkgName)
            throws NameNotFoundException {
        return cxt.createPackageContext(pkgName,
                Context.CONTEXT_IGNORE_SECURITY);
    }

    /**
     * 获取指定APP包名的Context对象
     * 
     * @param cxt
     *            当前Context
     * @param apkPath
     *            要打开的apk所在的路径
     * @return
     */
    public static Context getCxtFromApkPath(Context cxt, String apkPath)
            throws NameNotFoundException {
        return getCxtFromPkgName(cxt, getAppInfo(cxt, apkPath).packageName);
    }
}