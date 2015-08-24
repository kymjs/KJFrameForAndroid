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
package org.kymjs.blog.ui.widget.listview;

import org.kymjs.blog.ui.widget.listview.ILoadingLayout.State;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 * 
 * @param <T>
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public abstract class PullToRefreshBase<T extends View> extends LinearLayout
        implements IPullToRefresh<T> {
    /**
     * 定义了下拉刷新和上拉加载更多的接口。
     * 
     * @author kymjs (https://github.com/kymjs)
     * @since 2015-3
     */
    public interface OnRefreshListener<V extends View> {

        /**
         * 下拉松手后会被调用
         * 
         * @param refreshView
         *            刷新的View
         */
        void onPullDownToRefresh(final PullToRefreshBase<V> refreshView);

        /**
         * 加载更多时会被调用或上拉时调用
         * 
         * @param refreshView
         *            刷新的View
         */
        void onPullUpToRefresh(final PullToRefreshBase<V> refreshView);
    }

    /** 回滚的时间 */
    private static final int SCROLL_DURATION = 150;
    /** 阻尼系数 */
    private static final float OFFSET_RADIO = 2.5f;
    /** 上一次移动的点 */
    private float mLastMotionY = -1;
    /** 下拉刷新和加载更多的监听器 */
    private OnRefreshListener<T> mRefreshListener;
    /** 下拉刷新的布局 */
    private LoadingLayout mHeaderLayout;
    /** 上拉加载更多的布局 */
    private LoadingLayout mFooterLayout;
    /** HeaderView的高度 */
    private int mHeaderHeight;
    /** FooterView的高度 */
    private int mFooterHeight;
    /** 下拉刷新是否可用 */
    private boolean mPullRefreshEnabled = true;
    /** 上拉加载是否可用 */
    private boolean mPullLoadEnabled = false;
    /** 判断滑动到底部加载是否可用 */
    private boolean mScrollLoadEnabled = false;
    /** 是否截断touch事件 */
    private boolean mInterceptEventEnable = true;
    /** 表示是否消费了touch事件，如果是，则不调用父类的onTouchEvent方法 */
    private boolean mIsHandledTouchEvent = false;
    /** 移动点的保护范围值 */
    private int mTouchSlop;
    /** 下拉的状态 */
    private State mPullDownState = State.NONE;
    /** 上拉的状态 */
    private State mPullUpState = State.NONE;
    /** 可以下拉刷新的View */
    T mRefreshableView;
    /** 平滑滚动的Runnable */
    private SmoothScrollRunnable mSmoothScrollRunnable;
    /** 可刷新View的包装布局 */
    private FrameLayout mRefreshableViewWrapper;

    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mHeaderLayout = createHeaderLoadingLayout(context, attrs);
        mFooterLayout = createFooterLoadingLayout(context, attrs);
        mRefreshableView = createRefreshView(context, attrs);

        if (null == mRefreshableView) {
            throw new NullPointerException("Refreshable view can not be null.");
        }

        addRefreshableView(context, mRefreshableView);
        addHeaderAndFooter(context);

        // 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
        getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        refreshLoadingViewsSize();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    /**
     * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
     */
    private void refreshLoadingViewsSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        int headerHeight = (null != mHeaderLayout) ? mHeaderLayout
                .getContentSize() : 0;
        int footerHeight = (null != mFooterLayout) ? mFooterLayout
                .getContentSize() : 0;

        if (headerHeight < 0) {
            headerHeight = 0;
        }

        if (footerHeight < 0) {
            footerHeight = 0;
        }

        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;

        // 这里得到Header和Footer的高度，设置的padding的top和bottom就应该是header和footer的高度
        // 因为header和footer是完全看不见的
        headerHeight = (null != mHeaderLayout) ? mHeaderLayout
                .getMeasuredHeight() : 0;
        footerHeight = (null != mFooterLayout) ? mFooterLayout
                .getMeasuredHeight() : 0;
        if (0 == footerHeight) {
            footerHeight = mFooterHeight;
        }

        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();

        pTop = -headerHeight;
        pBottom = -footerHeight;

        setPadding(pLeft, pTop, pRight, pBottom);
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 当大小改变时我们需要更新 header/footer
        refreshLoadingViewsSize();

        // 设置刷新View的大小
        refreshRefreshableViewSize(w, h);

        /**
         * As we're currently in a Layout Pass, we need to schedule another one
         * to layout any changes we've made here
         */
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    public void setOrientation(int orientation) {
        if (LinearLayout.VERTICAL != orientation) {
            throw new IllegalArgumentException(
                    "This class only supports VERTICAL orientation.");
        }

        // Only support vertical orientation
        super.setOrientation(orientation);
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isInterceptTouchEventEnabled()) {
            return false;
        }

        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }

        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionY = event.getY();
            mIsHandledTouchEvent = false;
            break;

        case MotionEvent.ACTION_MOVE:
            final float deltaY = event.getY() - mLastMotionY;
            final float absDiff = Math.abs(deltaY);
            // 这里有三个条件：
            // 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
            // 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
            // 3，isPullLoading()，理由与第2条相同
            if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading()) {
                mLastMotionY = event.getY();
                // 第一个显示出来，Header已经显示或拉下
                if (isPullRefreshEnabled() && isReadyForPullDown()) {
                    // 1，Math.abs(getScrollY()) >
                    // 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
                    // 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
                    // 依然能向上滑动，直到HeaderView完全不可见
                    // 2，deltaY > 0.5f：表示下拉的值大于0.5f
                    mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY > 0.5f);
                    // 如果截断事件，我们则仍然把这个事件交给刷新View去处理，典型的情况是让ListView/GridView将按下
                    // Child的Selector隐藏
                    if (mIsHandledTouchEvent) {
                        mRefreshableView.onTouchEvent(event);
                    }
                } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                    // 原理如上
                    mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY < -0.5f);
                }
            }
            break;

        default:
            break;
        }

        return mIsHandledTouchEvent;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastMotionY = ev.getY();
            mIsHandledTouchEvent = false;
            break;

        case MotionEvent.ACTION_MOVE:
            final float deltaY = ev.getY() - mLastMotionY;
            mLastMotionY = ev.getY();
            if (isPullRefreshEnabled() && isReadyForPullDown()) {
                pullHeaderLayout(deltaY / OFFSET_RADIO);
                handled = true;
            } else if (isPullLoadEnabled() && isReadyForPullUp()) {
                pullFooterLayout(deltaY / OFFSET_RADIO);
                handled = true;
            } else {
                mIsHandledTouchEvent = false;
            }
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (mIsHandledTouchEvent) {
                mIsHandledTouchEvent = false;
                // 当第一个显示出来时
                if (isReadyForPullDown()) {
                    // 调用刷新
                    if (mPullRefreshEnabled
                            && (mPullDownState == State.RELEASE_TO_REFRESH)) {
                        startRefreshing();
                        handled = true;
                    }
                    resetHeaderLayout();
                } else if (isReadyForPullUp()) {
                    // 加载更多
                    if (isPullLoadEnabled()
                            && (mPullUpState == State.RELEASE_TO_REFRESH)) {
                        startLoading();
                        handled = true;
                    }
                    resetFooterLayout();
                }
            }
            break;

        default:
            break;
        }

        return handled;
    }

    @Override
    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        mPullRefreshEnabled = pullRefreshEnabled;
    }

    @Override
    public void setPullLoadEnabled(boolean pullLoadEnabled) {
        mPullLoadEnabled = pullLoadEnabled;
    }

    @Override
    public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
        mScrollLoadEnabled = scrollLoadEnabled;
    }

    @Override
    public boolean isPullRefreshEnabled() {
        return mPullRefreshEnabled && (null != mHeaderLayout);
    }

    @Override
    public boolean isPullLoadEnabled() {
        return mPullLoadEnabled && (null != mFooterLayout);
    }

    @Override
    public boolean isScrollLoadEnabled() {
        return mScrollLoadEnabled;
    }

    @Override
    public void setOnRefreshListener(OnRefreshListener<T> refreshListener) {
        mRefreshListener = refreshListener;
    }

    @Override
    public void onPullDownRefreshComplete() {
        if (isPullRefreshing()) {
            mPullDownState = State.RESET;
            onStateChanged(State.RESET, true);

            // 回滚动有一个时间，我们在回滚完成后再设置状态为normal
            // 在将LoadingLayout的状态设置为normal之前，我们应该禁止
            // 截断Touch事件，因为设里有一个post状态，如果有post的Runnable
            // 未被执行时，用户再一次发起下拉刷新，如果正在刷新时，这个Runnable
            // 再次被执行到，那么就会把正在刷新的状态改为正常状态，这就不符合期望
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mHeaderLayout.setState(State.RESET);
                }
            }, getSmoothScrollDuration());

            resetHeaderLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    @Override
    public void onPullUpRefreshComplete() {
        if (isPullLoading()) {
            mPullUpState = State.RESET;
            onStateChanged(State.RESET, false);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setInterceptTouchEventEnabled(true);
                    mFooterLayout.setState(State.RESET);
                }
            }, getSmoothScrollDuration());

            resetFooterLayout();
            setInterceptTouchEventEnabled(false);
        }
    }

    @Override
    public T getRefreshView() {
        return mRefreshableView;
    }

    @Override
    public LoadingLayout getHeaderLoadingLayout() {
        return mHeaderLayout;
    }

    @Override
    public LoadingLayout getFooterLoadingLayout() {
        return mFooterLayout;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        if (null != mHeaderLayout) {
            mHeaderLayout.setLastUpdatedLabel(label);
        }

        if (null != mFooterLayout) {
            mFooterLayout.setLastUpdatedLabel(label);
        }
    }

    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     * 
     * @param smoothScroll
     *            表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
     * @param delayMillis
     *            延迟时间
     */
    public void doPullRefreshing(final boolean smoothScroll,
            final long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int newScrollValue = -mHeaderHeight;
                int duration = smoothScroll ? SCROLL_DURATION : 0;

                startRefreshing();
                smoothScrollTo(newScrollValue, duration, 0);
            }
        }, delayMillis);
    }

    /**
     * 创建可以刷新的View
     * 
     * @param context
     *            context
     * @param attrs
     *            属性
     * @return View
     */
    protected abstract T createRefreshView(Context context, AttributeSet attrs);

    /**
     * 判断刷新的View是否滑动到顶部
     * 
     * @return true表示已经滑动到顶部，否则false
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * 判断刷新的View是否滑动到底
     * 
     * @return true表示已经滑动到底部，否则false
     */
    protected abstract boolean isReadyForPullUp();

    /**
     * 创建Header的布局
     * 
     * @param context
     *            context
     * @param attrs
     *            属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createHeaderLoadingLayout(Context context,
            AttributeSet attrs) {
        return new HeaderLoadingLayout(context);
    }

    /**
     * 创建Footer的布局
     * 
     * @param context
     *            context
     * @param attrs
     *            属性
     * @return LoadingLayout对象
     */
    protected LoadingLayout createFooterLoadingLayout(Context context,
            AttributeSet attrs) {
        return new FooterLoadingLayout(context);
    }

    /**
     * 得到平滑滚动的时间，派生类可以重写这个方法来控件滚动时间
     * 
     * @return 返回值时间为毫秒
     */
    protected long getSmoothScrollDuration() {
        return SCROLL_DURATION;
    }

    /**
     * 计算刷新View的大小
     * 
     * @param width
     *            当前容器的宽度
     * @param height
     *            当前容器的宽度
     */
    protected void refreshRefreshableViewSize(int width, int height) {
        if (null != mRefreshableViewWrapper) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRefreshableViewWrapper
                    .getLayoutParams();
            if (lp.height != height) {
                lp.height = height;
                mRefreshableViewWrapper.requestLayout();
            }
        }
    }

    /**
     * 将刷新View添加到当前容器中
     * 
     * @param context
     *            context
     * @param refreshableView
     *            可以刷新的View
     */
    protected void addRefreshableView(Context context, T refreshableView) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // 创建一个包装容器
        mRefreshableViewWrapper = new FrameLayout(context);
        mRefreshableViewWrapper.addView(refreshableView, width, height);

        // 这里把Refresh view的高度设置为一个很小的值，它的高度最终会在onSizeChanged()方法中设置为MATCH_PARENT
        // 这样做的原因是，如果此是它的height是MATCH_PARENT，那么footer得到的高度就是0，所以，我们先设置高度很小
        // 我们就可以得到header和footer的正常高度，当onSizeChanged后，Refresh view的高度又会变为正常。
        height = 10;
        addView(mRefreshableViewWrapper, new LinearLayout.LayoutParams(width,
                height));
    }

    /**
     * 添加Header和Footer
     * 
     * @param context
     *            context
     */
    protected void addHeaderAndFooter(Context context) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final LoadingLayout headerLayout = mHeaderLayout;
        final LoadingLayout footerLayout = mFooterLayout;

        if (null != headerLayout) {
            if (this == headerLayout.getParent()) {
                removeView(headerLayout);
            }

            addView(headerLayout, 0, params);
        }

        if (null != footerLayout) {
            if (this == footerLayout.getParent()) {
                removeView(footerLayout);
            }

            addView(footerLayout, -1, params);
        }
    }

    /**
     * 拉动Header Layout时调用
     * 
     * @param delta
     *            移动的距离
     */
    protected void pullHeaderLayout(float delta) {
        // 向上滑动，并且当前scrollY为0时，不滑动
        int oldScrollY = getScrollYValue();
        if (delta < 0 && (oldScrollY - delta) >= 0) {
            setScrollTo(0, 0);
            return;
        }

        // 向下滑动布局
        setScrollBy(0, -(int) delta);

        if (null != mHeaderLayout && 0 != mHeaderHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mHeaderHeight;
            mHeaderLayout.onPull(scale);
        }

        // 未处于刷新状态，更新箭头
        int scrollY = Math.abs(getScrollYValue());
        if (isPullRefreshEnabled() && !isPullRefreshing()) {
            if (scrollY > mHeaderHeight) {
                mPullDownState = State.RELEASE_TO_REFRESH;
            } else {
                mPullDownState = State.PULL_TO_REFRESH;
            }

            mHeaderLayout.setState(mPullDownState);
            onStateChanged(mPullDownState, true);
        }
    }

    /**
     * 拉Footer时调用
     * 
     * @param delta
     *            移动的距离
     */
    protected void pullFooterLayout(float delta) {
        int oldScrollY = getScrollYValue();
        if (delta > 0 && (oldScrollY - delta) <= 0) {
            setScrollTo(0, 0);
            return;
        }

        setScrollBy(0, -(int) delta);

        if (null != mFooterLayout && 0 != mFooterHeight) {
            float scale = Math.abs(getScrollYValue()) / (float) mFooterHeight;
            mFooterLayout.onPull(scale);
        }

        int scrollY = Math.abs(getScrollYValue());
        if (isPullLoadEnabled() && !isPullLoading()) {
            if (scrollY > mFooterHeight) {
                mPullUpState = State.RELEASE_TO_REFRESH;
            } else {
                mPullUpState = State.PULL_TO_REFRESH;
            }

            mFooterLayout.setState(mPullUpState);
            onStateChanged(mPullUpState, false);
        }
    }

    /**
     * 重置header
     */
    protected void resetHeaderLayout() {
        final int scrollY = Math.abs(getScrollYValue());
        final boolean refreshing = isPullRefreshing();

        if (refreshing && scrollY <= mHeaderHeight) {
            smoothScrollTo(0);
            return;
        }

        if (refreshing) {
            smoothScrollTo(-mHeaderHeight);
        } else {
            smoothScrollTo(0);
        }
    }

    /**
     * 重置footer
     */
    protected void resetFooterLayout() {
        int scrollY = Math.abs(getScrollYValue());
        boolean isPullLoading = isPullLoading();

        if (isPullLoading && scrollY <= mFooterHeight) {
            smoothScrollTo(0);
            return;
        }

        if (isPullLoading) {
            smoothScrollTo(mFooterHeight);
        } else {
            smoothScrollTo(0);
        }
    }

    /**
     * 判断是否正在下拉刷新
     * 
     * @return true正在刷新，否则false
     */
    protected boolean isPullRefreshing() {
        return (mPullDownState == State.REFRESHING);
    }

    /**
     * 是否正的上拉加载更多
     * 
     * @return true正在加载更多，否则false
     */
    protected boolean isPullLoading() {
        return (mPullUpState == State.REFRESHING);
    }

    /**
     * 开始刷新，当下拉松开后被调用
     */
    protected void startRefreshing() {
        // 如果正在刷新
        if (isPullRefreshing()) {
            return;
        }

        mPullDownState = State.REFRESHING;
        onStateChanged(State.REFRESHING, true);

        if (null != mHeaderLayout) {
            mHeaderLayout.setState(State.REFRESHING);
        }

        if (null != mRefreshListener) {
            // 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行刷新回调
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullDownToRefresh(PullToRefreshBase.this);
                }
            }, getSmoothScrollDuration());
        }
    }

    /**
     * 开始加载更多，上拉松开后调用
     */
    protected void startLoading() {
        // 如果正在加载
        if (isPullLoading()) {
            return;
        }

        mPullUpState = State.REFRESHING;
        onStateChanged(State.REFRESHING, false);

        if (null != mFooterLayout) {
            mFooterLayout.setState(State.REFRESHING);
        }

        if (null != mRefreshListener) {
            // 因为滚动回原始位置的时间是200，我们需要等回滚完后才执行加载回调
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshListener.onPullUpToRefresh(PullToRefreshBase.this);
                }
            }, getSmoothScrollDuration());
        }
    }

    /**
     * 当状态发生变化时调用
     * 
     * @param state
     *            状态
     * @param isPullDown
     *            是否向下
     */
    protected void onStateChanged(State state, boolean isPullDown) {

    }

    /**
     * 设置滚动位置
     * 
     * @param x
     *            滚动到的x位置
     * @param y
     *            滚动到的y位置
     */
    private void setScrollTo(int x, int y) {
        scrollTo(x, y);
    }

    /**
     * 设置滚动的偏移
     * 
     * @param x
     *            滚动x位置
     * @param y
     *            滚动y位置
     */
    private void setScrollBy(int x, int y) {
        scrollBy(x, y);
    }

    /**
     * 得到当前Y的滚动值
     * 
     * @return 滚动值
     */
    private int getScrollYValue() {
        return getScrollY();
    }

    /**
     * 平滑滚动
     * 
     * @param newScrollValue
     *            滚动的值
     */
    private void smoothScrollTo(int newScrollValue) {
        smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0);
    }

    /**
     * 平滑滚动
     * 
     * @param newScrollValue
     *            滚动的值
     * @param duration
     *            滚动时候
     * @param delayMillis
     *            延迟时间，0代表不延迟
     */
    private void smoothScrollTo(int newScrollValue, long duration,
            long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        int oldScrollValue = this.getScrollYValue();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue,
                    newScrollValue, duration);
        }

        if (post) {
            if (delayMillis > 0) {
                postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                post(mSmoothScrollRunnable);
            }
        }
    }

    /**
     * 设置是否截断touch事件
     * 
     * @param enabled
     *            true截断，false不截断
     */
    private void setInterceptTouchEventEnabled(boolean enabled) {
        mInterceptEventEnable = enabled;
    }

    /**
     * 标志是否截断touch事件
     * 
     * @return true截断，false不截断
     */
    private boolean isInterceptTouchEventEnabled() {
        return mInterceptEventEnable;
    }

    /**
     * 实现了平滑滚动的Runnable
     * 
     * @author Li Hong
     * @since 2013-8-22
     */
    final class SmoothScrollRunnable implements Runnable {
        /** 动画效果 */
        private final Interpolator mInterpolator;
        /** 结束Y */
        private final int mScrollToY;
        /** 开始Y */
        private final int mScrollFromY;
        /** 滑动时间 */
        private final long mDuration;
        /** 是否继续运行 */
        private boolean mContinueRunning = true;
        /** 开始时刻 */
        private long mStartTime = -1;
        /** 当前Y */
        private int mCurrentY = -1;

        /**
         * 构造方法
         * 
         * @param fromY
         *            开始Y
         * @param toY
         *            结束Y
         * @param duration
         *            动画时间
         */
        public SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * If the duration is 0, we scroll the view to target y directly.
             */
            if (mDuration <= 0) {
                setScrollTo(0, mScrollToY);
                return;
            }

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                final long oneSecond = 1000; // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime))
                        / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond),
                        0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime
                                / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;

                setScrollTo(0, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullToRefreshBase.this.postDelayed(this, 16);// SUPPRESS
                                                             // CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }
}
