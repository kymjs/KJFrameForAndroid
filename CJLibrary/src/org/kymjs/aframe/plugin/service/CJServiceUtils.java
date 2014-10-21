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
package org.kymjs.aframe.plugin.service;

import org.kymjs.aframe.plugin.CJConfig;

import android.content.Context;
import android.content.Intent;

/**
 * 启动一个CJFrame的插件Service的工具类<br>
 * 
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJServiceUtils {

    /**
     * 获取一个启动插件Service的Intent<br>
     * <b>注意：</b>本方法仅可在插件作为独立运行包使用时被调用<br>
     * 
     * @param cxt
     *            上下文
     * @param pluginClass
     *            要启动的Service的clazz，该clazz必须是CJService的子类
     */
    public static Intent getPluginIntent(Context cxt,
            Class<?> pluginClass) {
        Intent intent = new Intent();
        intent.setClass(cxt, CJProxyService.class);
        intent.putExtra(CJConfig.KEY_DEX_PATH, CJConfig.DEF_STR);
        if (pluginClass.asSubclass(CJService.class) != null) {
            intent.putExtra(CJConfig.KEY_EXTRA_CLASS,
                    pluginClass.getName());
        } else {
            throw new ClassCastException(
                    "plugin class must extends CJService");
        }
        return intent;
    }

    /**
     * 获取一个调转到指定插件Service的Intent<br>
     * 
     * @param cxt
     *            上下文
     * @param apkPath
     *            插件所在绝对路径
     * @param pluginClass
     *            要启动的Service的clazz，该clazz必须是CJService的子类
     */
    public static Intent getPluginIntent(Context cxt, String apkPath,
            Class<?> pluginClass) {
        Intent intent = getPluginIntent(cxt, pluginClass);
        intent.putExtra(CJConfig.KEY_DEX_PATH, apkPath);
        return intent;
    }
}
