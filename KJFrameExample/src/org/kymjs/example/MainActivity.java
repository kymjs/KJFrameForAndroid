package org.kymjs.example;

import org.kymjs.aframe.ui.ActivityUtils;
import org.kymjs.aframe.ui.activity.BaseSplash;
import org.kymjs.example.activity.TabExample;

public class MainActivity extends BaseSplash {
    @Override
    protected void initWidget() {
        super.initWidget();
        mImageView.setBackgroundResource(R.drawable.bg_img_menuback_cool);
    }

    @Override
    protected void redirectTo() {
        ActivityUtils.skipActivity(this, TabExample.class);
    }
}
