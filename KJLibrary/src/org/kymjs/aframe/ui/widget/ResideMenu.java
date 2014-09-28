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
package org.kymjs.aframe.ui.widget;

import java.util.ArrayList;
import java.util.List;

import org.kymjs.aframe.utils.DensityUtils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * 本类是对菜单界面设置动画与样式<br>
 * 
 * <b>创建时间</b> 2014-5-31
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class ResideMenu extends FrameLayout implements
        GestureDetector.OnGestureListener {

    // 菜单布局控件
    private ImageView mImgShadow;
    private ImageView mImgBg;
    private LinearLayout mLayoutMenu;
    private KJScrollView mScrollMenu;

    // 动画效果
    private AnimatorSet animCloseForShadow;
    private AnimatorSet animCloseForAty;
    private AnimatorSet animOpenForShadow;
    private AnimatorSet animOpenForAty;

    // 数据信息
    /** 阴影缩放比例 */
    private float shadowScaleX;
    private Activity aty;
    /** 不想拦截点击事件的View集合 */
    private List<View> ignoredViews;
    private List<ResideMenuItem> menuItems; // 菜单项集合

    private boolean isOpened;
    private OnMenuListener menuStateListener; // 菜单开关监听器
    private GestureDetector gestureDetector; // 触摸事件

    /** Activity的布局控件 */
    private ViewGroup parentView;
    /** Activity菜单布局部分的父控件 */
    private ViewGroup menuParentView;

    public interface OnMenuListener {
        public void openMenu();

        public void closeMenu();
    }

    public ResideMenu(Context context) {
        super(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        mImgBg = new ImageView(context);
        mImgBg.setAdjustViewBounds(true);
        mImgBg.setScaleType(ScaleType.CENTER_CROP);
        mImgBg.setLayoutParams(params);
        this.addView(mImgBg);

        mImgShadow = new ImageView(context);
        mImgShadow.setScaleType(ScaleType.FIT_XY);
        mImgShadow.setLayoutParams(params);
        this.addView(mImgShadow);

        mLayoutMenu = new LinearLayout(context);
        mLayoutMenu.setOrientation(LinearLayout.VERTICAL);
        KJScrollView.LayoutParams menuParams = new KJScrollView.LayoutParams(
                KJScrollView.LayoutParams.WRAP_CONTENT,
                KJScrollView.LayoutParams.MATCH_PARENT);
        mLayoutMenu.setLayoutParams(menuParams);

        mScrollMenu = new KJScrollView(context);
        mScrollMenu.setPadding(40, 0, 0, 0);
        mScrollMenu.setVerticalScrollBarEnabled(false);
        mScrollMenu.addView(mLayoutMenu, 0);
        FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(
                DensityUtils.getScreenW((Activity) context) / 2 + 40,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mScrollMenu.setLayoutParams(scrollParams);
        this.addView(mScrollMenu);
    }

    /******************************* 初始化 ***********************************/

    /**
     * 设置哪个Activity需要显示Slid菜单
     */
    public void attachToActivity(Activity aty) {
        initData(aty);
        setShadowScaleXByOrientation();
        buildAnimationSet();
    }

    /**
     * 初始化一些数据
     */
    private void initData(Activity aty) {
        this.aty = aty;
        ignoredViews = new ArrayList<View>();
        menuItems = new ArrayList<ResideMenuItem>();
        gestureDetector = new GestureDetector(aty, this);
        parentView = (ViewGroup) aty.getWindow().getDecorView();
        menuParentView = (ViewGroup) parentView.getChildAt(0);
    }

    /**
     * 根据屏幕方向设置阴影缩放比例
     */
    private void setShadowScaleXByOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏时阴影的宽度
            shadowScaleX = 0.5335f;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏时阴影的宽度
            shadowScaleX = 0.56f;
        }
    }

    /**
     * 创建动画
     */
    private void buildAnimationSet() {
        AnimListener animationListener = new AnimListener();
        animCloseForAty = getCloseAnimation(menuParentView, 1.0f,
                1.0f);
        animCloseForShadow = getCloseAnimation(mImgShadow, 1.0f, 1.0f);
        animOpenForAty = getOpenAnimation(menuParentView, 0.5f, 0.5f);
        animOpenForShadow = getOpenAnimation(mImgShadow,
                shadowScaleX, 0.59f);
        animCloseForAty.addListener(animationListener);
        animCloseForAty.playTogether(animCloseForShadow);
        animOpenForShadow.addListener(animationListener);
        animOpenForAty.playTogether(animOpenForShadow);
    }

    /**
     * 创建菜单打开的动画效果
     * 
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     */
    private AnimatorSet getOpenAnimation(View target,
            float targetScaleX, float targetScaleY) {
        int pivotX = (int) (DensityUtils.getScreenW(aty) * 1.5);
        int pivotY = (int) (DensityUtils.getScreenH(aty) * 0.5);

        target.setPivotX(pivotX);
        target.setPivotY(pivotY);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(ObjectAnimator.ofFloat(target,
                "scaleX", targetScaleX), ObjectAnimator.ofFloat(
                target, "scaleY", targetScaleY));

        scaleDown.setInterpolator(AnimationUtils.loadInterpolator(
                aty, android.R.anim.decelerate_interpolator));
        scaleDown.setDuration(250);
        return scaleDown;
    }

    /**
     * 创建菜单关闭的动画效果
     * 
     * @param target
     * @param targetScaleX
     * @param targetScaleY
     */
    private AnimatorSet getCloseAnimation(View target,
            float targetScaleX, float targetScaleY) {
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(ObjectAnimator.ofFloat(target, "scaleX",
                targetScaleX), ObjectAnimator.ofFloat(target,
                "scaleY", targetScaleY));
        scaleUp.setDuration(250);
        return scaleUp;
    }

    /**************************** public method ******************************/

    /**
     * 设置背景图片
     */
    public void setBackground(int imageResrouce) {
        mImgBg.setImageResource(imageResrouce);
    }

    /**
     * 在activity下面显示阴影
     */
    public void setShadowVisible(boolean isVisible) {
        if (isVisible) {
            mImgShadow.setVisibility(View.VISIBLE);
        } else {
            mImgShadow.setVisibility(View.GONE);
        }
    }

    /**
     * 设置在activity下面显示的阴影图片
     * 
     * @param resId
     *            图片的资源ID
     */
    public void setShadowImg(int resId) {
        mImgShadow.setImageResource(resId);
    }

    public boolean isOpened() {
        return isOpened;
    }

    public List<ResideMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<ResideMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(ResideMenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

    /**
     * 如果你需要在activity关闭或打开时设置某事件，设置这个监听器
     */
    public void setMenuListener(OnMenuListener menuListener) {
        this.menuStateListener = menuListener;
    }

    public void openMenu() {
        if (isOpened) {
            return;
        }
        isOpened = true;
        setViewPadding();
        animOpenForAty.start();
        if (getParent() != null) {
            // 如果本控件不是根控件则证明已经有一个本控件存在
            parentView.removeView(this);
        }
        if (mScrollMenu.getParent() != null) {
            // 如果菜单控件不是顶层控件则证明已经有一个菜单控件存在
            ViewGroup parent = ((ViewGroup) mScrollMenu.getParent());
            parent.removeView(mScrollMenu);
        }
        parentView.addView(this, 0);
        parentView.addView(mScrollMenu);
    }

    public void closeMenu() {
        if (isOpened) {
            isOpened = false;
            animCloseForAty.start();
        }
    }

    /**
     * 添加不拦截触摸事件的控件
     */
    public void addIgnoredView(View v) {
        for (int i = 0; i < ignoredViews.size(); i++) {
            if (v == ignoredViews.get(i)) {
                return;
            }
        }
        ignoredViews.add(v);
    }

    /**
     * 移除不拦截触摸事件的控件
     * 
     * @param v
     */
    public void removeIgnoredView(View v) {
        ignoredViews.remove(v);
    }

    /**
     * 清空不拦截触摸事件的控件
     */
    public void clearIgnoredViewList() {
        ignoredViews.clear();
    }

    /************************ private method ********************************/

    /**
     * 必须在菜单显示之前，因为padding属性需要在onCreateView()之前设置
     */
    private void setViewPadding() {
        this.setPadding(menuParentView.getPaddingLeft(),
                menuParentView.getPaddingTop(),
                menuParentView.getPaddingRight(),
                menuParentView.getPaddingBottom());
    }

    /**
     * 显示全部菜单项
     */
    private void showMenuDelay() {
        mLayoutMenu.removeAllViews();
        for (int i = 0; i < menuItems.size(); i++) {
            showMenuItem(menuItems.get(i), i);
        }
    }

    /**
     * 显示某一个菜单项的显示
     */
    private void showMenuItem(ResideMenuItem menuItem, int index) {
        if (index == 0 && menuItems.size() != 0) {
            android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            menuItem.measure(0, 0);
            int h = menuItem.getMeasuredHeight() * menuItems.size();
            params.topMargin = (DensityUtils.getScreenH(aty) - h) / 2;
            menuItem.setLayoutParams(params);
        }
        mLayoutMenu.addView(menuItem);
        menuItem.setAlpha(0.5F);
        AnimatorSet anim = new AnimatorSet();
        anim.playTogether(ObjectAnimator.ofFloat(menuItem,
                "translationX", -150f, 0f), ObjectAnimator.ofFloat(
                menuItem, "alpha", 0f, 1f));

        anim.setInterpolator(AnimationUtils.loadInterpolator(aty,
                android.R.anim.anticipate_overshoot_interpolator));
        // with animation;
        anim.setStartDelay(50 * index);
        anim.setDuration(400).start();
    }

    /**
     * 判断触摸是否发生在 不拦截触摸事件的控件上
     */
    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : ignoredViews) {
            v.getGlobalVisibleRect(rect); // 将view的可见区域并保存到rect中
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }

    /**************************** GestureListener ****************************/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent,
            MotionEvent motionEvent2, float v, float v2) {
        if (isInIgnoredView(motionEvent)
                || isInIgnoredView(motionEvent2))
            return false;
        int distanceX = (int) (motionEvent2.getX() - motionEvent
                .getX());
        int distanceY = (int) (motionEvent2.getY() - motionEvent
                .getY());
        int screenWidth = (int) DensityUtils.getScreenW(aty);
        if (Math.abs(distanceY) > screenWidth * 0.3)
            return false;
        if (Math.abs(distanceX) > screenWidth * 0.3) {
            if (distanceX > 0 && !isOpened) {
                // 从左到右
                openMenu();
            } else if (distanceX < 0 && isOpened) {
                // 从右到左
                closeMenu();
            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {}

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent,
            MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {}

    /**************************** Listener ****************************/
    class AnimListener implements AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
            if (isOpened) {
                mLayoutMenu.removeAllViews();
                showMenuDelay();
                if (menuStateListener != null)
                    menuStateListener.openMenu();
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!isOpened) {
                parentView.removeView(ResideMenu.this);
                parentView.removeView(mScrollMenu);
                if (menuStateListener != null)
                    menuStateListener.closeMenu();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {}

        @Override
        public void onAnimationRepeat(Animator animation) {}
    }
}
