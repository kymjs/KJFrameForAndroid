/*
 * Copyright (c) 2014, CJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.plugin;

import org.kymjs.aframe.plugin.CJConfig.ActivityType;
import org.kymjs.aframe.plugin.activity.CJActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
    public static ActivityInfo getActivityInfo(Context cxt,
            String apkPath) throws NameNotFoundException {
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
    public static ActivityInfo getActivityInfo(Context cxt,
            String apkPath, int index) throws NameNotFoundException {
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
    public static ActivityInfo getActivityInfo(PackageInfo pkgInfo,
            int index) {
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
     * 返回指定Activity的类型：<br>
     * ActivityType.UNKNOWN 未知类型<br>
     * ActivityType.NORMAL Activity<br>
     * ActivityType.FRAGMENT Fragment<br>
     * ActivityType.ACTIONBAR ActionBar<br>
     * 
     * @param cls
     *            Activity对应的clazz
     * @return
     */
    private static ActivityType getActivityType(Class<?> cls) {
        ActivityType type = ActivityType.UNKNOWN;
        try {
            // 类型降级（父类转为子类）
            if (cls.asSubclass(CJActivity.class) != null) {
                type = ActivityType.NORMAL;
                return type;
            }
        } catch (ClassCastException e) {
        }
        return type;
    }

    /**
     * 返回指定Activity的类型：<br>
     * ActivityType.UNKNOWN 未知类型<br>
     * ActivityType.NORMAL Activity<br>
     * ActivityType.FRAGMENT Fragment<br>
     * ActivityType.ACTIONBAR ActionBar<br>
     * 
     * @param className
     *            clazz完整名
     * @param classLoader
     *            类加载器
     * @return
     */
    private static ActivityType getActivityType(String className,
            ClassLoader classLoader) {
        Class<?> cls = null;
        try {
            cls = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return getActivityType(cls);
    }

    /**
     * 
     * @param className
     *            clazz完整名
     * @param classLoader
     *            类加载器
     * @return
     */
    public static String getProxyViewAction(String className,
            ClassLoader classLoader) {
        ActivityType type = getActivityType(className, classLoader);
        return getProxyByActivityType(type);
    }

    /**
     * 
     * @param cls
     *            Activity对应的clazz
     * @return
     */
    public static String getProxyViewAction(Class<?> cls) {
        ActivityType type = getActivityType(cls);
        return getProxyByActivityType(type);
    }

    /**
     * 获取插件界面的类型（Activity? Fragment? ...）
     * 
     * @param type
     *            ActivityType.UNKNOWN, ActivityType.NORMAL,
     *            ActivityType.FRAGMENT, ActivityType.ACTIONBAR
     * @return
     */
    private static String getProxyByActivityType(ActivityType type) {
        String proxyViewAction = null;
        switch (type) {
        case NORMAL: {
            proxyViewAction = CJConfig.PROXY_ACTIVITY;
            break;
        }
        case FRAGMENT: {
            proxyViewAction = CJConfig.PROXY_FRAGMENT;
            break;
        }
        case ACTIONBAR:
        case UNKNOWN:
        }
        return proxyViewAction;
    }
}