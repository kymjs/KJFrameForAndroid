/*
 * Copyright (c) 2015, 张涛.
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
package org.kymjs.blog.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Path动画类
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class KJAnimations {

    /**
     * 旋转 Rotate
     */
    public static Animation getRotateAnimation(float fromDegrees,
            float toDegrees, long durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        return rotate;
    }

    /**
     * 透明度 Alpha
     */
    public static Animation getAlphaAnimation(float fromAlpha, float toAlpha,
            long durationMillis) {
        AlphaAnimation alpha = new AlphaAnimation(fromAlpha, toAlpha);
        alpha.setDuration(durationMillis);
        alpha.setFillAfter(true);
        return alpha;
    }

    /**
     * 缩放 Scale
     */
    public static Animation getScaleAnimation(long durationMillis) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(durationMillis);
        return scale;
    }

    /**
     * 缩放 Scale
     */
    public static Animation getScaleAnimation(float scaleXY, long durationMillis) {
        ScaleAnimation scale = new ScaleAnimation(1.0f, scaleXY, 1.0f, scaleXY,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(durationMillis);
        return scale;
    }

    /**
     * 位移 Translate
     */
    public static Animation getTranslateAnimation(float fromXDelta,
            float toXDelta, float fromYDelta, float toYDelta,
            long durationMillis) {
        TranslateAnimation translate = new TranslateAnimation(fromXDelta,
                toXDelta, fromYDelta, toYDelta);
        translate.setDuration(durationMillis);
        translate.setFillAfter(true);
        return translate;
    }

    public static void openLoginAnim(View v) {
        AnimationSet set = new AnimationSet(true);
        v.measure(0, 0);
        set.addAnimation(getTranslateAnimation(0, 0, v.getMeasuredHeight(), 0,
                600));
        set.addAnimation(getAlphaAnimation(0.8F, 1, 600));
        v.setAnimation(set);
    }

    public static Animation clickAnimation(float scaleXY, long durationMillis) {
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(getScaleAnimation(scaleXY, durationMillis));
        set.setDuration(durationMillis);
        return set;
    }

    public static void shakeCurtain(View v) {
        AnimationSet set = new AnimationSet(false);
        Animation anim1 = getTranslateAnimation(0, 0, 0, -200, 110);
        Animation anim2 = getTranslateAnimation(0, 0, -200, 0, 80);
        Animation anim3 = getTranslateAnimation(0, 0, 0, -50, 25);
        Animation anim4 = getTranslateAnimation(0, 0, -50, 0, 25);
        anim1.setStartOffset(20);
        anim2.setStartOffset(230);
        anim3.setStartOffset(360);
        anim4.setStartOffset(400);
        set.addAnimation(anim1);
        set.addAnimation(anim2);
        set.addAnimation(anim3);
        set.addAnimation(anim4);
        v.startAnimation(set);
    }

    public static void clickCurtain(View v) {
        AnimationSet set = new AnimationSet(false);
        Animation anim1 = getTranslateAnimation(0, 0, 0, -50, 110);
        Animation anim2 = getTranslateAnimation(0, 0, -50, 0, 80);
        Animation anim3 = getTranslateAnimation(0, 0, 0, -25, 25);
        Animation anim4 = getTranslateAnimation(0, 0, -25, 0, 25);
        anim1.setStartOffset(20);
        anim2.setStartOffset(230);
        anim3.setStartOffset(360);
        anim4.setStartOffset(400);
        set.addAnimation(anim1);
        set.addAnimation(anim2);
        set.addAnimation(anim3);
        set.addAnimation(anim4);
        v.startAnimation(set);
    }

    /**
     * 打开的动画
     * 
     * @param relativeLayout
     *            子菜单容器
     * @param background
     *            子菜单背景
     * @param menu
     *            菜单按钮
     * @param durationMillis
     *            动画时间
     */
    public static void openAnimation(RelativeLayout relativeLayout,
            ImageView menu, long durationMillis) {
        relativeLayout.setVisibility(View.VISIBLE);
        for (int i = 1; i < relativeLayout.getChildCount(); i++) {
            ImageView imageView = null;
            if (relativeLayout.getChildAt(i) instanceof ImageView) {
                imageView = (ImageView) relativeLayout.getChildAt(i);
            } else {
                continue;
            }

            AnimationSet set = new AnimationSet(true);
            set.addAnimation(getRotateAnimation(-360, 0, durationMillis));
            set.addAnimation(getAlphaAnimation(0.5f, 1.0f, durationMillis));
            set.addAnimation(getTranslateAnimation(
                    menu.getLeft() - imageView.getLeft(), 0, menu.getTop()
                            - imageView.getTop(), 0, durationMillis));
            set.setFillAfter(true);
            set.setDuration(durationMillis);
            set.setStartOffset((i * 100)
                    / (-1 + relativeLayout.getChildCount()));
            set.setInterpolator(new OvershootInterpolator(1f));
            imageView.startAnimation(set);
        }
    }

    /**
     * 关闭的动画
     * 
     * @param relativeLayout
     *            子菜单容器
     * @param background
     *            子菜单背景
     * @param menu
     *            菜单按钮
     * @param durationMillis
     *            动画时间
     */
    public static void closeAnimation(final RelativeLayout relativeLayout,
            final ImageView menu, long durationMillis) {
        for (int i = 1; i < relativeLayout.getChildCount(); i++) {
            ImageView imageView = null;
            if (relativeLayout.getChildAt(i) instanceof ImageView) {
                imageView = (ImageView) relativeLayout.getChildAt(i);
            } else {
                continue;
            }

            AnimationSet set = new AnimationSet(true);
            set.addAnimation(getRotateAnimation(0, -360, durationMillis));
            set.addAnimation(getAlphaAnimation(1.0f, 0.5f, durationMillis));
            set.addAnimation(getTranslateAnimation(0, menu.getLeft()
                    - imageView.getLeft(), 0,
                    menu.getTop() - imageView.getTop(), durationMillis));
            set.setFillAfter(true);
            set.setDuration(durationMillis);
            set.setStartOffset(((relativeLayout.getChildCount() - i) * 100)
                    / (-1 + relativeLayout.getChildCount()));
            set.setInterpolator(new AnticipateInterpolator(1f));
            set.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {}

                @Override
                public void onAnimationRepeat(Animation arg0) {}

                @Override
                public void onAnimationEnd(Animation arg0) {
                    relativeLayout.setVisibility(View.GONE);
                }
            });
            imageView.startAnimation(set);
        }
    }

    public static Animation clickAnimation(long durationMillis) {
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(getAlphaAnimation(1.0f, 0.3f, durationMillis));
        set.addAnimation(getScaleAnimation(durationMillis));
        set.setDuration(durationMillis);
        return set;
    }
}
