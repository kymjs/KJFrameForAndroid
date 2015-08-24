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
package org.kymjs.blog.ui.widget.dobmenu;

import org.kymjs.blog.AppContext;
import org.kymjs.blog.ui.widget.dobmenu.CurtainItem.SlidingType;
import org.kymjs.blog.utils.KJAnimations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

/**
 * 窗帘控件代理类控制器，这个才是控件的核心类
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class CurtainViewController {

    public enum SlidingStatus {
        COLLAPSED, EXPANDED, ANIMATING
    }

    public static final float DEFAULT_JUMP_LINE_PERCENTAGE = 0.6f;
    public static final int DEFAULT_INT = -1;

    private final Activity activity;
    private CurtainItem curtainItem;

    public View actionBarView;
    private ViewGroup content;

    private FrameLayout curtainParent;
    private FrameLayout.LayoutParams curtainLayoutParams;
    protected int curtainHeight;

    private float jumpLine;
    private AnimationExecutor animationExecutor;

    public CurtainViewController(Activity activity, CurtainItem slidingItem,
            int actionBarId) {
        super();
        this.activity = activity;
        this.curtainItem = slidingItem;
        init(actionBarId);
    }

    private void init(int actionBarId) {
        curtainParent = new FrameLayout(activity);
        content = (ViewGroup) activity.findViewById(android.R.id.content);
        content.addView(curtainParent);

        actionBarView = activity.findViewById(actionBarId); // 设置ActionBar
        setSlidingType(curtainItem.getSlidingType()); // 设置开关动画模式：卷动或平移
        animationExecutor = new AnimationExecutor(this);
    }

    public void setCurtainView(View curtainView) {
        if (curtainParent.getChildCount() > 0) {
            curtainParent.removeViewAt(0);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        curtainView.setLayoutParams(params);
        this.curtainParent.addView(curtainView);

        prepareSlidingLayout();
        hideCurtainLayout();
    }

    /**
     * 为控件做一些属性设置
     */
    protected void prepareSlidingLayout() {
        curtainLayoutParams = (FrameLayout.LayoutParams) curtainParent
                .getLayoutParams();
        ViewTreeObserver vto = curtainParent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            @SuppressLint("NewApi")
            public void onGlobalLayout() {
                hideCurtainLayout();
                ViewTreeObserver obs = curtainParent.getViewTreeObserver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });

        curtainParent.setOnTouchListener(new OnContentTouchListener());

        curtainParent.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && getSlidingStatus() == SlidingStatus.EXPANDED) {
                    collapse();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 定义curtainView最初的位置，如果是卷动模式初始值在屏幕顶端，如果是平移模式初始值在负屏幕高度处
     */
    protected void hideCurtainLayout() {
        curtainHeight = content.getHeight();
        jumpLine = curtainHeight * curtainItem.getJumpLinePercentage();

        curtainLayoutParams.height = curtainHeight;
        curtainParent.setLayoutParams(curtainLayoutParams);
        // 不同模式不同设置
        if (curtainItem.getSlidingType() == SlidingType.SIZE) {
            curtainLayoutParams.height = 0;
            curtainLayoutParams.topMargin = 0;
        } else if (curtainItem.getSlidingType() == SlidingType.MOVE) {
            curtainLayoutParams.topMargin = -curtainHeight;
            curtainLayoutParams.height = curtainHeight;
        }
        curtainParent.setLayoutParams(curtainLayoutParams);
    }

    /**
     * 窗帘拉下
     */
    public void expand() {
        KJAnimations.shakeCurtain(curtainParent);
        animateSliding(0, curtainHeight);
        focusOnSliding();
    }

    /**
     * 窗帘拉起
     */
    public void collapse() {
        animateSliding(400, curtainHeight, 0);
    }

    public void finish() {
        collapse();
    }

    /**
     * 滑动时的焦点处理
     */
    public void focusOnSliding() {
        curtainParent.setFocusable(true);
        curtainParent.setFocusableInTouchMode(true);
        curtainParent.requestFocus();
    }

    /**
     * 滑动动画
     */
    public void animateSliding(int duration, int fromY, int toY) {
        if (curtainItem.isEnabled()) {
            animationExecutor.animateView(duration, fromY, toY);
        }
    }

    public void animateSliding(int fromY, int toY) {
        animateSliding(80, fromY, toY);
    }

    public CurtainItem getSlidingItem() {
        return curtainItem;
    }

    public void setSlidingItem(CurtainItem slidingItem) {
        this.curtainItem = slidingItem;
    }

    public void setEnabled(boolean enabled) {
        hideCurtainLayout();
    }

    public SlidingStatus getSlidingStatus() {
        return getSlidingStatus(this);
    }

    public void setViewHeight(int viewHeight) {
        curtainLayoutParams.height = viewHeight;
        curtainParent.setLayoutParams(curtainLayoutParams);
    }

    public int getViewHeight() {
        return curtainLayoutParams.height;
    }

    public void setViewTop(int viewTop) {
        curtainLayoutParams.topMargin = viewTop - curtainHeight;
        curtainParent.setLayoutParams(curtainLayoutParams);
    }

    public int getViewTop() {
        return curtainLayoutParams.topMargin;
    }

    public void setSlidingType(SlidingType slidingType) {
        if (slidingType == SlidingType.SIZE) {
            actionBarView.setOnTouchListener(new OnSizingTouchListener());
        } else if (slidingType == SlidingType.MOVE) {
            actionBarView.setOnTouchListener(new OnMovingTouchListener());
        }
        if (curtainItem.getSlidingView() != null) {
            hideCurtainLayout();
        }
    }

    public FrameLayout getSlidingParent() {
        return curtainParent;
    }

    public int getSlidingHeight() {
        return curtainHeight;
    }

    public float getJumpLine() {
        return jumpLine;
    }

    public static final CurtainViewController.SlidingStatus getSlidingStatus(
            CurtainViewController mCurtainViewController) {

        FrameLayout slidingParent = mCurtainViewController.getSlidingParent();
        FrameLayout.LayoutParams slidingLayoutParams = (FrameLayout.LayoutParams) slidingParent
                .getLayoutParams();

        if (mCurtainViewController.getSlidingItem().getSlidingType() == SlidingType.SIZE) {

            int currentSlidingHeight = slidingParent.getHeight();

            if (currentSlidingHeight == 0) {
                return CurtainViewController.SlidingStatus.COLLAPSED;

            } else if (currentSlidingHeight >= mCurtainViewController
                    .getSlidingHeight()) {
                return CurtainViewController.SlidingStatus.EXPANDED;

            } else {
                return CurtainViewController.SlidingStatus.ANIMATING;
            }

        } else if (mCurtainViewController.getSlidingItem().getSlidingType() == SlidingType.MOVE) {

            int currentSlidingTop = slidingLayoutParams.topMargin;

            if (currentSlidingTop <= -mCurtainViewController.getSlidingHeight()) {
                return CurtainViewController.SlidingStatus.COLLAPSED;

            } else if (currentSlidingTop >= 0) {
                return CurtainViewController.SlidingStatus.EXPANDED;

            } else {
                return CurtainViewController.SlidingStatus.ANIMATING;
            }

        } else {
            return CurtainViewController.SlidingStatus.ANIMATING;
        }
    }

    /** 用于计算手指滑动的速度。 */
    private VelocityTracker mVelocityTracker;

    /** 手指滑动需要达到的速度。 */
    public static final int SNAP_VELOCITY = 300;

    /**
     * 获取手指在content界面滑动的速度。
     * 
     * @author kymjs (https://github.com/kymjs)
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getYVelocity();
        return velocity;
    }

    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     * 
     * @author kymjs (https://github.com/kymjs)
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     * 
     * @author kymjs (https://github.com/kymjs)
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 缩放模式开窗帘的ActionBar触摸事件监听器
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public class OnSizingTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getSlidingItem().isEnabled()) {
                float y = event.getY();
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    focusOnSliding();
                    if (curtainParent.getHeight() > 0) {
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    curtainLayoutParams.height = (int) y;
                    curtainParent.setLayoutParams(curtainLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (y > getJumpLine()) {
                        animateSliding(400, (int) y, getSlidingHeight());
                    } else {
                        animateSliding(400, (int) y, 0);
                    }
                    break;
                }
            }
            return true;
        }
    }

    /**
     * 平移模式开窗帘的ActionBar触摸事件监听器
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public class OnMovingTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            createVelocityTracker(event);
            if (getSlidingItem().isEnabled()) {
                float y = event.getY();

                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    focusOnSliding();
                    if (curtainLayoutParams.bottomMargin > 0) {
                        recycleVelocityTracker();
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    curtainLayoutParams.topMargin = (int) y
                            - curtainParent.getHeight();
                    curtainParent.setLayoutParams(curtainLayoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    if (y > getJumpLine()
                            || getScrollVelocity() > SNAP_VELOCITY) {
                        animateSliding(400, (int) y, curtainParent.getHeight());
                    } else {
                        animateSliding(400, (int) y, 0);
                    }
                    recycleVelocityTracker();
                    break;
                }
            }
            return true;
        }
    }

    /**
     * 平移模式关窗帘的Content触摸事件监听器（卷动模式的就不写了，需要的时候再说）
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public class OnContentTouchListener implements OnTouchListener {
        float downY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float y = event.getY();
            createVelocityTracker(event);
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                focusOnSliding();
                if (curtainLayoutParams.bottomMargin > 0) {
                    recycleVelocityTracker();
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int move = (int) (downY - y);
                if (move > 0) {
                    curtainLayoutParams.topMargin = -move;
                    curtainParent.setLayoutParams(curtainLayoutParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                int moveY = (int) (downY - y);
                int top = curtainLayoutParams.topMargin + curtainHeight;
                if (20 > Math.abs(moveY)) {
                    v.performClick();
                    animateSliding(400, top, curtainParent.getHeight());
                } else {
                    if (moveY > AppContext.screenH
                            || getScrollVelocity() >= SNAP_VELOCITY) {
                        animateSliding(400, top, curtainParent.getHeight());
                    } else {
                        animateSliding(400, top, 0);
                    }
                }
                recycleVelocityTracker();
                break;
            default:
                break;
            }
            return true;
        }
    }
}
