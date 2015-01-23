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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;

import org.kymjs.kjframe.bitmap.helper.BitmapCreate;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ImageView;

/**
 * 提供应用换肤功能
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class SkinUtils {

    public static final String SKIN_FILE = "org_kymjs_kjframe_skin_file_key";
    public static final String SKIN_KEY = "org_kymjs_kjframe_skin_key";

    /**
     * 返回皮肤文件夹路径集合,每个item是一个皮肤包解压后的文件夹地址
     * 
     * @param path
     * @return
     */
    public static ArrayList<String> skinFileHandler(Context cxt, String path) {
        return skinFileHandler("data/data/" + cxt.getPackageName(), path);
    }

    private static ArrayList<String> skinFileHandler(String pkgPath, String path) {
        ArrayList<String> skinFils = new ArrayList<String>();

        // 首先是遍历某指定路径，找出所有皮肤的压缩包
        List<String> zipPathList = getAllSkinZipFiles(new File(path));
        KJLoger.debug("找到了" + zipPathList.size() + "个皮肤包");

        // 然后是把压缩文件解压到指定路径中
        for (String zipPath : zipPathList) {
            File zipFile = new File(zipPath);
            String fileName = zipFile.getName().substring(0,
                    zipFile.getName().lastIndexOf("."));
            File skinDir = new File(pkgPath + "/skin", fileName.trim());
            skinFils.add(skinDir.getAbsolutePath() + File.separator);
            try {
                if (!skinDir.exists()) {
                    skinDir.mkdirs();
                    ZipUtils.upZipFile(zipFile, skinDir.getAbsolutePath());
                }
            } catch (ZipException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return skinFils;
    }

    /**
     * 到指定路径下获取zip皮肤资源
     * 
     * @param zipFolder
     *            zip所在文件的绝对路径
     * @return
     */
    public static List<String> getAllSkinZipFiles(File zipFolder) {
        List<String> zipPathList = new ArrayList<String>();
        if (zipFolder.exists()) {
            File[] files = zipFolder.listFiles();
            for (File zipFile : files) {
                String zipName = zipFile.getName();
                if (zipName.startsWith("KJSkin_") && zipName.endsWith("zip")) {
                    zipPathList.add(zipFile.getAbsolutePath());
                }
            }
        }
        return zipPathList;
    }

    /**
     * 从指定路径加载一个
     * 
     * @param view
     *            欲显示图片的控件
     * @param resName
     *            资源地址
     * @return
     */
    public static Drawable loadDrawable(View view, String resName) {
        Drawable drawable = null;
        Context cxt = view.getContext();
        String skinPath = PreferenceHelper.readString(cxt, SKIN_FILE, SKIN_KEY);
        if (!StringUtils.isEmpty(skinPath)) {
            view.measure(0, 0);
            int w = view.getMeasuredWidth();
            int h = view.getMeasuredHeight();
            String path = skinPath + File.separator + resName;
            if (w != 0 && h != 0) {
                drawable = new BitmapDrawable(view.getResources(),
                        BitmapCreate.bitmapFromFile(path, w, h));
            } else {
                drawable = new BitmapDrawable(view.getResources(), path);
            }
        }
        return drawable;
    }

    /**
     * 载入一个SelectorDrawable
     * 
     * @param pressName
     * @param normalName
     * @return
     */
    public static StateListDrawable loadStateListDrawable(View view,
            String pressName, String normalName) {
        StateListDrawable sd = null;
        // sd.addState(new int[] { android.R.attr.state_selected }, dw_press);
        // sd.addState(new int[] { android.R.attr.state_pressed }, dw_press);
        // sd.addState(new int[] { android.R.attr.state_focused }, dw_press);
        Drawable drawPress = loadDrawable(view, pressName);
        Drawable drawNormal = loadDrawable(view, normalName);
        if (drawPress != null && drawNormal != null) {
            sd = new StateListDrawable();
            sd.addState(new int[] { android.R.attr.state_pressed }, drawPress);
            sd.addState(new int[] {}, drawNormal);
        }
        return sd;
    }

    public static void displayImageResource(ImageView view, String resName) {
        Drawable drawable = loadDrawable(view, resName);
        if (drawable != null) {
            view.setImageDrawable(drawable);
        }
    }

    public static void displayImageResource(ImageView view, String pressName,
            String normalName) {
        Drawable drawable = loadStateListDrawable(view, pressName, normalName);
        if (drawable != null) {
            view.setImageDrawable(drawable);
        }
    }

    @SuppressLint("NewApi")
    public static void displayBackground(View view, String resName) {
        Drawable drawable = loadDrawable(view, resName);
        if (drawable != null) {
            if (SystemTool.getSDKVersion() >= 16) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    @SuppressLint("NewApi")
    public static void displayBackground(View view, String pressName,
            String normalName) {
        Drawable drawable = loadStateListDrawable(view, pressName, normalName);
        if (drawable != null) {
            if (SystemTool.getSDKVersion() >= 16) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    /**
     * 根据资源ID返回对应的Drawable
     * 
     * @param cxt
     * @param resId
     * @return
     */
    public static Drawable getDrawableFromId(Context cxt, int resId) {
        return cxt.getResources().getDrawable(resId);
    }
}
