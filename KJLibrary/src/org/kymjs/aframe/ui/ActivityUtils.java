package org.kymjs.aframe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * skipActivity()
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class ActivityUtils {

    public static void skipActivity(Activity aty, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    public static void skipActivity(Activity aty, Intent it) {
        aty.startActivity(it);
    }

    public static void skipActivity(Activity aty, Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }
}
