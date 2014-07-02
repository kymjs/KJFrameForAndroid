package org.kymjs.aframe.utils;

import org.kymjs.aframe.KJConfig;

import android.content.Context;
import android.content.Intent;

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
