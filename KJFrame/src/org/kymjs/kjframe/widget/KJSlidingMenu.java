/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
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
package org.kymjs.kjframe.widget;

import org.kymjs.kjframe.utils.DensityUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 应用主界面跟布局ViewGroup，侧滑界面控件
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class KJSlidingMenu extends HorizontalScrollView {
    private final int mScreenWidth; // 屏幕宽度
    private final int mMenuRightPadding; // 菜单右侧空白
    private int mMenuWidth; // 菜单宽度
    private boolean isOpen;
    private boolean once; // 本控件是否首次创建

    private boolean showAnim;

    private int mHalfMenuWidth; // 改变菜单状态时手指滑动的最大值:默认菜单宽度的1/3
    public static final int SNAP_VELOCITY = 270; // 改变菜单状态时手指滑动的最大速度
    private VelocityTracker mVelocityTracker; // 用于计算手指滑动的速度
    private float prevX = 0; // 初始按下时的X坐标
    private float maxX = 0; // 移动过程中X的极大值
    private float minX = 0; // 移动过程中X的极小值

    private OnScrollProgressListener progressListener;

    private ViewGroup mMenu;
    private ViewGroup mContent;

    public KJSlidingMenu(Context context) {
        this(context, null, 0);
    }

    public KJSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KJSlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScreenWidth = DensityUtils.getScreenW(context);
        mMenuRightPadding = (int) (mScreenWidth * 0.27);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);// 获取到根布局
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);
            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mHalfMenuWidth = mMenuWidth / 3;
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

    /**
     * 滑动时的动画操作，采用了开源项目http://nineoldandroids.com/
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (showAnim) {
            float scale = l * 1.0f / mMenuWidth;
            float leftScale = 1 - 0.3f * scale;
            float rightScale = 0.8f + scale * 0.2f;

            ViewHelper.setScaleX(mMenu, leftScale);
            ViewHelper.setScaleY(mMenu, leftScale);
            ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
            ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);

            ViewHelper.setPivotX(mContent, 0);
            ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
            ViewHelper.setScaleX(mContent, rightScale);
            ViewHelper.setScaleY(mContent, rightScale);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        createVelocityTracker(ev);
        switch (ev.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float x = ev.getX();
            if (prevX == 0) { // 放在move事件中初始化，防止down事件不调用
                maxX = minX = prevX = x;
            }
            if (maxX < x) {
                maxX = x;
            }
            if (minX > x) {
                minX = x;
            }
            break;
        case MotionEvent.ACTION_UP:
            if (getScrollVelocity() > SNAP_VELOCITY) { // 如果滑动达到一定速度
                checkMenuByOrientation(prevX - maxX, prevX - minX); // 检测这个手势是想打开还是关闭并执行
            } else { // 如果达不到，就检测移动的距离
                checkMenuByDistance();
            }
            maxX = minX = prevX = 0;
            recycleVelocityTracker();
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
            boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (progressListener != null) {
            progressListener.onProgress(scrollX, mMenuWidth);
        }
    }

    /************************ private method ****************************/
    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     * 
     * @param event
     *            content界面的滑动事件
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在content界面滑动的速度。
     * 
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 通过方向，判断当前手势是想打开还是想关闭（设置两个值的目的是为了防止手指在滑动过程中左右抖动，影响判断）
     * 
     * @param maxMove
     *            最大值到初始值的移动距离
     * @param minMove
     *            最小值到当前值的移动距离
     */
    private void checkMenuByOrientation(float maxMove, float minMove) {
        float availValue = (Math.abs(maxMove) > Math.abs(minMove)) ? maxMove
                : minMove;
        if (availValue > 0) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**
     * 通过移动距离判断当前手势是想打开还是想关闭
     */
    private void checkMenuByDistance() {
        if (getScrollX() > mHalfMenuWidth) {
            close();
        } else {
            open();
        }
    }

    /**
     * 打开菜单
     */
    private void open() {
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    private void close() {
        this.smoothScrollTo(mMenuWidth, 0);
        isOpen = false;
    }

    /************************ public method *****************************/
    public void openMenu() {
        if (!isOpen) {
            open();
        }
    }

    public void closeMenu() {
        if (isOpen) {
            close();
        }
    }

    public void changeMenu() {
        if (isOpen) {
            close();
        } else {
            open();
        }
    }

    public boolean isShowAnim() {
        return showAnim;
    }

    public void setShowAnim(boolean showAnim) {
        this.showAnim = showAnim;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 设置滑动过程监听器
     */
    public void setOnScrollProgressListener(OnScrollProgressListener l) {
        this.progressListener = l;
    }

    public interface OnScrollProgressListener {
        /**
         * 会在滑动过程中调用，具体请看
         * {@link #onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) }
         * 
         * @param scrollX
         *            当前滑动值
         * @param total
         *            总滑动值
         */
        void onProgress(int scrollX, int total);
    }
}
