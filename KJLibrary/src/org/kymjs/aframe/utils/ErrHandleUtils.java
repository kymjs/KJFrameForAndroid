/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.utils;

import org.kymjs.aframe.KJConfig;

import android.content.Context;
import android.content.Intent;

/**
 * 系统硬件错误处理<br>
 * 
 * <b>创建时间</b> 2014-8-14
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 */
public class ErrHandleUtils {
    /**
     * 用于显示错误信息
     * 
     * @param context
     * @param errInfo
     */
    public static void sendErrInfo(Context context, String errInfo) {
        Intent errIntent = new Intent();
        errIntent.setAction(KJConfig.RECEIVER_ERROR);
        errIntent.putExtra("error", errInfo);
        context.sendBroadcast(errIntent);
    }

    /**
     * 用于显示没有网络的Toast
     * 
     * @param context
     */
    public static void sendNotNetReceiver(Context context) {
        Intent intent = new Intent();
        intent.setAction(KJConfig.RECEIVER_NOT_NET_WARN);
        context.sendBroadcast(intent);
    }
}
