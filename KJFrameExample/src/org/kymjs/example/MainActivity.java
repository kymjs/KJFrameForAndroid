package org.kymjs.example;

import org.kymjs.aframe.ui.ActivityUtils;
import org.kymjs.aframe.ui.activity.BaseSplash;
import org.kymjs.example.activity.TabExample;

/**
 * 启动界面效果展示
 */
public class MainActivity extends BaseSplash {
    @Override
    protected void initWidget() {
        super.initWidget();
        mImageView.setBackgroundResource(R.drawable.bg);
    }

    @Override
    protected void redirectTo() {
        ActivityUtils.skipActivity(this, TabExample.class);
    }
}
