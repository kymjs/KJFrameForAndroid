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

import java.util.HashMap;

import android.content.Context;
import dalvik.system.DexClassLoader;

/**
 * 一个CJFrame专用类加载器,以加载文件系统上的jar、dex、apk<br>
 * 使用本加载器替代系统的DexClassLoader，当同时管理多个插件的时候，可以避免多个加载器加载同一个类时的转换错误<br>
 * 
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJClassLoader extends DexClassLoader {

    private static final HashMap<String, CJClassLoader> pluginLoader = new HashMap<String, CJClassLoader>();

    protected CJClassLoader(String dexPath, String optimizedDirectory,
            String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

    /**
     * 返回dexPath对应的加载器
     */
    public static CJClassLoader getClassLoader(String dexPath, Context cxt,
            ClassLoader parent) {
        CJClassLoader cjLoader = pluginLoader.get(dexPath);
        if (cjLoader == null) {
            // 获取到app的启动路径
            final String dexOutputPath = cxt
                    .getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
            cjLoader = new CJClassLoader(dexPath, dexOutputPath, null, parent);
            pluginLoader.put(dexPath, cjLoader);
        }
        return cjLoader;
    }
}
