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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 启动一个CJFrame的插件Activity的工具类<br>
 * 
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJActivityUtils {

    /**
     * 获取一个调转到插件Activity的Intent<br>
     * 默认启动插件Manifest.xml中第一个声明的Activity
     * 
     * @param cxt
     *            上下文
     * @param apkPath
     *            插件所在绝对路径
     */
    public static Intent getPluginIntent(Context cxt, String apkPath) {
        Intent intent = new Intent();
        intent.setClass(cxt, CJProxyActivity.class);
        intent.putExtra(CJConfig.KEY_DEX_PATH, apkPath);
        return intent;
    }

    /**
     * 获取一个调转到指定插件Activity的Intent<br>
     * 
     * @param cxt
     *            上下文
     * @param apkPath
     *            插件所在绝对路径
     * @param pluginClass
     *            要启动的Activity的clazz，该clazz必须是CJActivity的子类
     */
    public static Intent getPluginIntent(Context cxt, String apkPath,
            Class<?> pluginClass) {
        Intent intent = getPluginIntent(cxt, apkPath);
        if (pluginClass.asSubclass(CJActivity.class) != null) {
            intent.putExtra(CJConfig.KEY_EXTRA_CLASS, pluginClass.getName());
        }
        return intent;
    }

    /**
     * 启动插件Activity<br>
     * 默认启动插件Manifest.xml中第一个声明的Activity
     * 
     * @param cxt
     *            上下文
     * @param apkPath
     *            插件所在绝对路径
     */
    public static void launchPlugin(Context cxt, String apkPath) {
        cxt.startActivity(getPluginIntent(cxt, apkPath));
    }

    /**
     * 启动插件Activity<br>
     * 
     * @param cxt
     *            上下文
     * @param apkPath
     *            插件所在绝对路径
     * @param pluginClass
     *            要启动的Activity的clazz，该clazz必须是CJActivity的子类
     */
    public static void launchPlugin(Context cxt, String apkPath,
            Class<?> pluginClass) {
        cxt.startActivity(getPluginIntent(cxt, apkPath, pluginClass));
    }

    /**
     * 启动插件Activity<br>
     * <b>注意</b>本方法仅能用在插件做为独立APP运行时使用<br>
     * 
     * @param cxt
     *            上下文
     * @param pluginClass
     *            要启动的Activity的clazz，该clazz必须是CJActivity的子类
     */
    public static void launchPlugin(Context cxt, Class<?> pluginClass) {
        cxt.startActivity(getPluginIntent(cxt, CJConfig.DEF_STR, pluginClass));
    }

    /**
     * 跳转到插件Activity<br>
     * 默认启动插件Manifest.xml中第一个声明的Activity
     * 
     * @param aty
     *            上下文
     * @param apkPath
     *            插件所在绝对路径
     */
    public static void skipPlugin(Activity aty, String apkPath) {
        launchPlugin(aty, apkPath);
        aty.finish();
    }

    /**
     * 跳转到插件Activity<br>
     * <b>注意</b>本方法仅能用在插件做为独立APP运行时使用<br>
     * 
     * @param aty
     *            上下文
     * @param pluginClass
     *            要启动的Activity的clazz，该clazz必须是CJActivity的子类
     */
    public static void skipPlugin(Activity aty, Class<?> pluginClass) {
        launchPlugin(aty, pluginClass);
        aty.finish();
    }

    /**
     * 跳转到插件Activity<br>
     * 
     * @param aty
     * @param pluginClass
     */
    public static void skipPlugin(Activity aty, String dexPath,
            Class<?> pluginClass) {
        launchPlugin(aty, dexPath, pluginClass);
        aty.finish();
    }
}
