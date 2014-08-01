package org.kymjs.example;

import org.kymjs.aframe.ui.activity.BaseSplash;
import org.kymjs.example.activity.TabExample;

import android.widget.ImageView;

/**
 * 启动界面效果展示
 */
public class MainActivity extends BaseSplash {
    @Override
    protected void setRootBackground(ImageView view) {
        view.setBackgroundResource(R.drawable.bg);
    }

    @Override
    protected void redirectTo() {
        skipActivity(this, TabExample.class);
    }

}
