package org.kymjs.aframe;

import android.util.Log;

/**
 * 应用程序的Log管理
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-2-28
 */
public class KJLoger {
    private static final boolean IS_DEBUG = true;
    private static final boolean SHOW_ACTIVITY_STATE = false;

    public static final void debug(String msg) {
        if (IS_DEBUG) {
            Log.i("debug", msg);
        }
    }

    public static final void debug(String msg, Throwable tr) {
        if (IS_DEBUG) {
            Log.i("debug", msg, tr);
        }
    }

    public static final void state(String packName, String state) {
        if (SHOW_ACTIVITY_STATE) {
            Log.d("state", packName + state);
        }
    }
}
