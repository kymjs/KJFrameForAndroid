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
package org.kymjs.aframe.ui.activity;

import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 应用启动的欢迎界面模板
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public abstract class BaseSplash extends BaseActivity {

    /**
     * 用于显示启动界面的背景图片
     */
    protected ImageView mImageView;

    protected abstract void setRootBackground(ImageView view);

    /**
     * 默认设置为全屏、竖屏锁定显示
     */
    public BaseSplash() {
        setAllowFullScreen(true);
        setHiddenActionBar(true);
        setScreenOrientation(ScreenOrientation.VERTICAL);
    }

    @Override
    public void setRootView() {
        mImageView = new ImageView(this);
        mImageView.setScaleType(ScaleType.FIT_XY);
        setContentView(mImageView);
        setRootBackground(mImageView);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2F, 0.9F);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        // 监听动画过程
        animationSet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                checkVersion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
            }
        });
        mImageView.startAnimation(animationSet);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    /**
     * 跳转到...
     */
    protected void redirectTo() {
        if (firstsInstall()) {
        }
    }

    /**
     * 判断首次使用
     */
    protected boolean firstsInstall() {
        return true;
    }

    /**
     * 检查更新
     */
    protected void checkVersion() {}
}
