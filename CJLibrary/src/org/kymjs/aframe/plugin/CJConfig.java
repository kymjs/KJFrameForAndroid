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

/**
 * CJFrame中的常量声明配置器 <br>
 * 
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJConfig {
    public static final String DEF_STR = "CJFrameForAndroid_defualt_Str";
    public static final String FROM = "fromWhichActivity";
    public static final int FROM_PLUGIN = 0;
    public static final int FROM_PROXY_APP = 1;

    public static final String KEY_DEX_PATH = "dex_path_key";
    public static final String KEY_EXTRA_CLASS = "extra_class";
    public static final String KEY_ATY_INDEX = "aty_index";

    public static enum ActivityType {
        UNKNOWN, NORMAL, FRAGMENT, ACTIONBAR
    }

    public static final String PROXY_ACTIVITY = "org.kymjs.cjframe.activity";
    public static final String PROXY_FRAGMENT = "org.kymjs.cjframe.fragment";
}
