/**
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.kymjs.aframe.ui.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * https://github.com/dinocore1/DevsmartLib-Android
 * 
 * <br>
 * <b>警告</b> 注意本类协议：MIT
 * 
 * @author dinocore1
 */
public class HorizontalListView extends AdapterView<ListAdapter> {
    /**
     * 定义item插入到ViewGroup的哪里
     * {@code ViewGroup #addViewInLayout(View, int, LayoutParams, boolean)}
     */
    private static final int INSERT_AT_END_OF_LIST = -1;
    private static final int INSERT_AT_START_OF_LIST = 0;

    /** 停止滑动后的速度衰减 */
    private static final float FLING_DEFAULT_ABSORB_VELOCITY = 30f;

    /** The friction amount to use for the fling tracker */
    private static final float FLING_FRICTION = 0.009f;

    /**
     * 用于在HorizontalListView滑动后将追踪器滑动到之前的状态
     */
    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";

    /**
     * 父ID状态。滚动发生后用来恢复父的状态
     */
    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";

    /** Tracks ongoing flings */
    protected Scroller mFlingTracker = new Scroller(getContext());

    /** Gesture listener to receive callbacks when gestures are detected */
    private final GestureListener mGestureListener = new GestureListener();

    /** Used for detecting gestures within this view so they can be handled */
    private GestureDetector mGestureDetector;

    /** This tracks the starting layout position of the leftmost view */
    private int mDisplayOffset;

    protected ListAdapter mAdapter;

    /** Holds a cache of recycled views to be reused as needed */
    private List<Queue<View>> mRemovedViewsCache = new ArrayList<Queue<View>>();

    /**
     * Flag used to mark when the adapters data has changed, so the view can be
     * relaid out
     */
    private boolean mDataChanged = false;

    /** Temporary rectangle to be used for measurements */
    private Rect mRect = new Rect();

    /**
     * 当前被触摸的点，用来代表触摸到的视图
     */
    private View mViewBeingTouched = null;

    private int mDividerWidth = 0;

    private Drawable mDivider = null;

    /** The x position of the currently rendered view */
    protected int mCurrentX;

    /** The x position of the next to be rendered view */
    protected int mNextX;

    /** 用于恢复滚动 */
    private Integer mRestoreX = null;

    private int mMaxX = Integer.MAX_VALUE;

    /** 最左边第一个可见item的下标 */
    private int mLeftViewAdapterIndex;

    /** 最右边第一个可见item的下标 */
    private int mRightViewAdapterIndex;

    /** 当前选中的item的下标 */
    private int mCurrentlySelectedAdapterIndex;

    /**
     * Callback interface to notify listener that the user has scrolled this
     * view to the point that it is low on data.
     */
    private RunningOutOfDataListener mRunningOutOfDataListener = null;

    /**
     * This tracks the user value set of how many items from the end will be
     * considered running out of data.
     */
    private int mRunningOutOfDataThreshold = 0;

    /**
     * Tracks if we have told the listener that we are running low on data. We
     * only want to tell them once.
     */
    private boolean mHasNotifiedRunningLowOnData = false;

    /**
     * Callback interface to be invoked when the scroll state has changed.
     */
    private OnScrollStateChangedListener mOnScrollStateChangedListener = null;

    /**
     * Represents the current scroll state of this view. Needed so we can detect
     * when the state changes so scroll listener can be notified.
     */
    private OnScrollStateChangedListener.ScrollState mCurrentScrollState = OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE;

    /**
     * Tracks the state of the left edge glow.
     */
    private EdgeEffectCompat mEdgeGlowLeft;

    /**
     * Tracks the state of the right edge glow.
     */
    private EdgeEffectCompat mEdgeGlowRight;

    /** The height measure spec for this view, used to help size children views */
    private int mHeightMeasureSpec;

    /**
     * Used to track if a view touch should be blocked because it stopped a
     * fling
     */
    private boolean mBlockTouchAction = false;

    /**
     * 如果父控件能垂直滚动则为true
     */
    private boolean mIsParentVerticalScroll = false;

    /**
     * The listener that receives notifications when this view is clicked.
     */
    private OnClickListener mOnClickListener;

    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEdgeGlowLeft = new EdgeEffectCompat(context);
        mEdgeGlowRight = new EdgeEffectCompat(context);
        mGestureDetector = new GestureDetector(context,
                mGestureListener);
        bindGestureDetector();
        initView();
        retrieveXmlConfiguration(context, attrs);
        setWillNotDraw(false);

        // If the OS version is high enough then set the friction on the fling
        // tracker */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            HoneycombPlus.setFriction(mFlingTracker, FLING_FRICTION);
        }
    }

    /**
     * 注册手势监听器
     */
    private void bindGestureDetector() {
        // Generic touch listener that can be applied to any view that needs to
        // process gestures
        final View.OnTouchListener gestureListenerHandler = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v,
                    final MotionEvent event) {
                // Delegate the touch event to our gesture detector
                return mGestureDetector.onTouchEvent(event);
            }
        };

        setOnTouchListener(gestureListenerHandler);
    }

    /**
     * 当此HorizontalListView被嵌入一个垂直滚动视图中时，将启用或禁用父触摸拦截。
     * 
     * @param disallowIntercept
     *            如果为true父控件将阻止截获的孩子触摸事件
     */
    private void requestParentListViewToNotInterceptTouchEvents(
            Boolean disallowIntercept) {
        if (mIsParentVerticalScroll != disallowIntercept) {
            View view = this;
            while (view.getParent() instanceof View) {
                if (view.getParent() instanceof ListView
                        || view.getParent() instanceof ScrollView) {
                    view.getParent()
                            .requestDisallowInterceptTouchEvent(
                                    disallowIntercept);
                    mIsParentVerticalScroll = disallowIntercept;
                    return;
                }
                view = (View) view.getParent();
            }
        }
    }

    private void retrieveXmlConfiguration(Context context,
            AttributeSet attrs) {}

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        // Add the parent state to the bundle
        bundle.putParcelable(BUNDLE_ID_PARENT_STATE,
                super.onSaveInstanceState());

        // Add our state to the bundle
        bundle.putInt(BUNDLE_ID_CURRENT_X, mCurrentX);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            // Restore our state from the bundle
            mRestoreX = Integer.valueOf((bundle
                    .getInt(BUNDLE_ID_CURRENT_X)));

            // Restore out parent's state from the bundle
            super.onRestoreInstanceState(bundle
                    .getParcelable(BUNDLE_ID_PARENT_STATE));
        }
    }

    public void setDivider(Drawable divider) {
        mDivider = divider;
        if (divider != null) {
            setDividerWidth(divider.getIntrinsicWidth());
        } else {
            setDividerWidth(0);
        }
    }

    public void setDividerWidth(int width) {
        mDividerWidth = width;
        requestLayout();
        invalidate();
    }

    private void initView() {
        mLeftViewAdapterIndex = -1;
        mRightViewAdapterIndex = -1;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
    }

    /**
     * 这个控件将初始化 并移除全部孩子View
     */
    private void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    /** 用来捕获适配器的数据更改事件 */
    private DataSetObserver mAdapterDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mDataChanged = true;
            // 清除我们已经用完的数据，方便再次使用
            mHasNotifiedRunningLowOnData = false;
            unpressTouchedChild();
            invalidate();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            // 清除我们已经用完的数据，方便再次使用
            mHasNotifiedRunningLowOnData = false;
            unpressTouchedChild();
            reset();
            invalidate();
            requestLayout();
        }
    };

    @Override
    public void setSelection(int position) {
        mCurrentlySelectedAdapterIndex = position;
    }

    @Override
    public View getSelectedView() {
        return getChild(mCurrentlySelectedAdapterIndex);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterDataObserver);
        }

        if (adapter != null) {
            mHasNotifiedRunningLowOnData = false;

            mAdapter = adapter;
            mAdapter.registerDataSetObserver(mAdapterDataObserver);
        }

        initializeRecycledViewCache(mAdapter.getViewTypeCount());
        reset();
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 创建并根据viewTypeCount指定的View样式初始化一个View
     */
    private void initializeRecycledViewCache(int viewTypeCount) {
        mRemovedViewsCache.clear();
        for (int i = 0; i < viewTypeCount; i++) {
            mRemovedViewsCache.add(new LinkedList<View>());
        }
    }

    /**
     * 从缓存返回可以重复使用、回收的视图，如果不可用则为null
     * 
     * @param adapterIndex
     * @return
     */
    private View getRecycledView(int adapterIndex) {
        int itemViewType = mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            return mRemovedViewsCache.get(itemViewType).poll();
        }
        return null;
    }

    /**
     * 将View加入缓存队列
     * 
     * @param adapterIndex
     * @param view
     */
    private void recycleView(int adapterIndex, View view) {
        int itemViewType = mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            mRemovedViewsCache.get(itemViewType).offer(view);
        }
    }

    private boolean isItemViewTypeValid(int itemViewType) {
        return itemViewType < mRemovedViewsCache.size();
    }

    /**
     * 添加一个child到这个ViewGroup并计算正确的大小
     */
    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = getLayoutParams(child);
        addViewInLayout(child, viewPos, params, true);
        measureChild(child);
    }

    private void measureChild(View child) {
        ViewGroup.LayoutParams childParams = getLayoutParams(child);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(
                mHeightMeasureSpec, getPaddingTop()
                        + getPaddingBottom(), childParams.height);
        int childWidthSpec;
        if (childParams.width > 0) {
            childWidthSpec = MeasureSpec.makeMeasureSpec(
                    childParams.width, MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /** 获取一个 child的 layout parameters如果没有则返回默认值 */
    private ViewGroup.LayoutParams getLayoutParams(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return layoutParams;
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onLayout(boolean changed, int left, int top,
            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAdapter == null) {
            return;
        }
        // 强制重绘这个View
        invalidate();

        // 如果数据改变，则重置一切从零开始渲染偏移到上次一样
        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }
        // 如果是从一个滑动恢复
        if (mRestoreX != null) {
            mNextX = mRestoreX;
            mRestoreX = null;
        }
        // 如果是急速滑动
        if (mFlingTracker.computeScrollOffset()) {
            // 计算下一个position
            mNextX = mFlingTracker.getCurrX();
        }
        if (mNextX < 0) {
            mNextX = 0;
            // 显示一个边缘效应吸收流速
            if (mEdgeGlowLeft.isFinished()) {
                mEdgeGlowLeft
                        .onAbsorb((int) determineFlingAbsorbVelocity());
            }

            mFlingTracker.forceFinished(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        } else if (mNextX > mMaxX) {
            mNextX = mMaxX;
            // 显示一个边缘效应吸收流速
            if (mEdgeGlowRight.isFinished()) {
                mEdgeGlowRight
                        .onAbsorb((int) determineFlingAbsorbVelocity());
            }
            mFlingTracker.forceFinished(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        }

        // Calculate our delta from the last time the view was drawn
        int dx = mCurrentX - mNextX;
        removeNonVisibleChildren(dx);
        fillList(dx);
        positionChildren(dx);
        // 由于视图现在已经绘制，更新我们当前位置
        mCurrentX = mNextX;
        // 如果我们的滚动显示了全部的View那么现在确定最大滚动位置
        if (determineMaxX()) {
            // 以我们现在知道的最大滚动位置，重新布局
            onLayout(changed, left, top, right, bottom);
            return;
        }
        // 如果滑动已经完成
        if (mFlingTracker.isFinished()) {
            // 如果刚刚结束的滑动
            if (mCurrentScrollState == OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
        } else {
            // 仍处于滑动，调用的下一帧
            ViewCompat.postOnAnimation(this, mDelayedLayout);
        }
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();

        // If completely at the edge then disable the fading edge
        if (mCurrentX == 0) {
            return 0;
        } else if (mCurrentX < horizontalFadingEdgeLength) {
            // We are very close to the edge, so enable the fading edge
            // proportional to the distance from the edge, and the width of the
            // edge effect
            return (float) mCurrentX / horizontalFadingEdgeLength;
        } else {
            // The current x position is more then the width of the fading edge
            // so enable it fully.
            return 1;
        }
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();

        // If completely at the edge then disable the fading edge
        if (mCurrentX == mMaxX) {
            return 0;
        } else if ((mMaxX - mCurrentX) < horizontalFadingEdgeLength) {
            // We are very close to the edge, so enable the fading edge
            // proportional to the distance from the ednge, and the width of the
            // edge effect
            return (float) (mMaxX - mCurrentX)
                    / horizontalFadingEdgeLength;
        } else {
            // The distance from the maximum x position is more then the width
            // of the fading edge so enable it fully.
            return 1;
        }
    }

    /** 确定当前的滑动吸收速度 */
    private float determineFlingAbsorbVelocity() {
        // 如果操作系统版本足够高得到真正的速度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return IceCreamSandwichPlus
                    .getCurrVelocity(mFlingTracker);
        } else {
            // 无法获取速度，所以返回一个默认值
            // In actuality this is never used since EdgeEffectCompat does not
            // draw anything unless the device is ICS+.
            // Less then ICS EdgeEffectCompat essentially performs a NOP.
            return FLING_DEFAULT_ABSORB_VELOCITY;
        }
    }

    /** 通过使用一个线程请求布局 */
    private Runnable mDelayedLayout = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec,
            int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeightMeasureSpec = heightMeasureSpec;
    };

    /**
     * 确定最大X位置。这是用户能够滚动画面最远。直到最后适配器项目已经奠定了它是无法计算的，一旦这种情况的发生会进行计算，
     * 并在必要时武力这一观点的重绘和重新布局。
     * 
     * @return 如果可以确定maxX,则返回true
     */
    private boolean determineMaxX() {
        // If the last view has been laid out, then we can determine the maximum
        // x position
        if (isLastItemInAdapter(mRightViewAdapterIndex)) {
            View rightView = getRightmostChild();
            if (rightView != null) {
                int oldMaxX = mMaxX;
                // Determine the maximum x position
                mMaxX = mCurrentX
                        + (rightView.getRight() - getPaddingLeft())
                        - getRenderWidth();
                // Handle the case where the views do not fill at least 1 screen
                if (mMaxX < 0) {
                    mMaxX = 0;
                }
                if (mMaxX != oldMaxX) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 增加孩子到View的左右，直到满屏
     */
    private void fillList(final int dx) {
        // 获取最右边的孩子，并确定其右边缘
        int edge = 0;
        View child = getRightmostChild();
        if (child != null) {
            edge = child.getRight();
        }
        // 添加孩子View到右边
        fillListRight(edge, dx);
        // 获取最左边的孩子，并确定其左边缘
        edge = 0;
        child = getLeftmostChild();
        if (child != null) {
            edge = child.getLeft();
        }
        // 添加孩子View到右边
        fillListLeft(edge, dx);
    }

    private void removeNonVisibleChildren(final int dx) {
        View child = getLeftmostChild();

        // 环路消除最左边的孩子，直到那孩子是在屏幕上
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += isLastItemInAdapter(mLeftViewAdapterIndex) ? child
                    .getMeasuredWidth() : mDividerWidth
                    + child.getMeasuredWidth();

            // 移除的视图添加到缓存中
            recycleView(mLeftViewAdapterIndex, child);

            // 真正删除View
            removeViewInLayout(child);

            // 保持最左边的孩子的适配器指数跟踪
            mLeftViewAdapterIndex++;

            // 获取最左边的 child
            child = getLeftmostChild();
        }

        child = getRightmostChild();

        // Loop removing the rightmost child, until that child is on the screen
        while (child != null && child.getLeft() + dx >= getWidth()) {
            recycleView(mRightViewAdapterIndex, child);
            removeViewInLayout(child);
            mRightViewAdapterIndex--;
            child = getRightmostChild();
        }
    }

    private void fillListRight(int rightEdge, final int dx) {
        // Loop adding views to the right until the screen is filled
        while (rightEdge + dx + mDividerWidth < getWidth()
                && mRightViewAdapterIndex + 1 < mAdapter.getCount()) {
            mRightViewAdapterIndex++;
            if (mLeftViewAdapterIndex < 0) {
                // 第一个View
                mLeftViewAdapterIndex = mRightViewAdapterIndex;
            }

            // 使用缓存View，从adapter中getView()
            View child = mAdapter.getView(mRightViewAdapterIndex,
                    getRecycledView(mRightViewAdapterIndex), this);
            addAndMeasureChild(child, INSERT_AT_END_OF_LIST);

            // 第一个item是没有Divider的
            rightEdge += (mRightViewAdapterIndex == 0 ? 0
                    : mDividerWidth) + child.getMeasuredWidth();

            // 检测我们是否正在运行low数据，通知监听器去获取更多
            determineIfLowOnData();
        }
    }

    private void fillListLeft(int leftEdge, final int dx) {
        // Loop adding views to the left until the screen is filled
        while (leftEdge + dx - mDividerWidth > 0
                && mLeftViewAdapterIndex >= 1) {
            mLeftViewAdapterIndex--;
            View child = mAdapter.getView(mLeftViewAdapterIndex,
                    getRecycledView(mLeftViewAdapterIndex), this);
            addAndMeasureChild(child, INSERT_AT_START_OF_LIST);
            // 第一个item是没有Divider的
            leftEdge -= mLeftViewAdapterIndex == 0 ? child
                    .getMeasuredWidth() : mDividerWidth
                    + child.getMeasuredWidth();
            // If on a clean edge then just remove the child, otherwise remove
            // the divider as well
            mDisplayOffset -= leftEdge + dx == 0 ? child
                    .getMeasuredWidth() : mDividerWidth
                    + child.getMeasuredWidth();
        }
    }

    /** Loops through each child and positions them onto the screen */
    /** 循环遍历每个孩子，使他们显示在屏幕上 */
    private void positionChildren(final int dx) {
        int childCount = getChildCount();

        if (childCount > 0) {
            mDisplayOffset += dx;
            int leftOffset = mDisplayOffset;

            // Loop each child view
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int left = leftOffset + getPaddingLeft();
                int top = getPaddingTop();
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();

                // Layout the child
                child.layout(left, top, right, bottom);

                // Increment our offset by added child's size and divider width
                leftOffset += child.getMeasuredWidth()
                        + mDividerWidth;
            }
        }
    }

    /** Gets the current child that is leftmost on the screen. */
    private View getLeftmostChild() {
        return getChildAt(0);
    }

    /** Gets the current child that is rightmost on the screen. */
    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    /**
     * Finds a child view that is contained within this view, given the adapter
     * index.
     * 
     * @return View The child view, or or null if not found.
     */
    private View getChild(int adapterIndex) {
        if (adapterIndex >= mLeftViewAdapterIndex
                && adapterIndex <= mRightViewAdapterIndex) {
            getChildAt(adapterIndex - mLeftViewAdapterIndex);
        }

        return null;
    }

    /**
     * Returns the index of the child that contains the coordinates given. This
     * is useful to determine which child has been touched. This can be used for
     * a call to {@link #getChildAt(int)}
     * 
     * @param x
     *            X-coordinate
     * @param y
     *            Y-coordinate
     * @return The index of the child that contains the coordinates. If no child
     *         is found then returns -1
     */
    private int getChildIndex(final int x, final int y) {
        int childCount = getChildCount();

        for (int index = 0; index < childCount; index++) {
            getChildAt(index).getHitRect(mRect);
            if (mRect.contains(x, y)) {
                return index;
            }
        }

        return -1;
    }

    /**
     * Simple convenience method for determining if this index is the last index
     * in the adapter
     */
    private boolean isLastItemInAdapter(int index) {
        return index == mAdapter.getCount() - 1;
    }

    /** Gets the height in px this view will be rendered. (padding removed) */
    private int getRenderHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /** Gets the width in px this view will be rendered. (padding removed) */
    private int getRenderWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /** Scroll to the provided offset */
    public void scrollTo(int x) {
        mFlingTracker.startScroll(mNextX, 0, x - mNextX, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
    }

    @Override
    public int getFirstVisiblePosition() {
        return mLeftViewAdapterIndex;
    }

    @Override
    public int getLastVisiblePosition() {
        return mRightViewAdapterIndex;
    }

    /**
     * Draws the overscroll edge glow effect on the left and right sides of the
     * horizontal list
     */
    private void drawEdgeGlow(Canvas canvas) {
        if (mEdgeGlowLeft != null && !mEdgeGlowLeft.isFinished()
                && isEdgeGlowEnabled()) {
            // The Edge glow is meant to come from the top of the screen, so
            // rotate it to draw on the left side.
            final int restoreCount = canvas.save();
            final int height = getHeight();

            canvas.rotate(-90, 0, 0);
            canvas.translate(-height + getPaddingBottom(), 0);

            mEdgeGlowLeft
                    .setSize(getRenderHeight(), getRenderWidth());
            if (mEdgeGlowLeft.draw(canvas)) {
                invalidate();
            }

            canvas.restoreToCount(restoreCount);
        } else if (mEdgeGlowRight != null
                && !mEdgeGlowRight.isFinished()
                && isEdgeGlowEnabled()) {
            // The Edge glow is meant to come from the top of the screen, so
            // rotate it to draw on the right side.
            final int restoreCount = canvas.save();
            final int width = getWidth();

            canvas.rotate(90, 0, 0);
            canvas.translate(getPaddingTop(), -width);
            mEdgeGlowRight.setSize(getRenderHeight(),
                    getRenderWidth());
            if (mEdgeGlowRight.draw(canvas)) {
                invalidate();
            }

            canvas.restoreToCount(restoreCount);
        }
    }

    /** Draws the dividers that go in between the horizontal list view items */
    private void drawDividers(Canvas canvas) {
        final int count = getChildCount();

        // Only modify the left and right in the loop, we set the top and bottom
        // here since they are always the same
        final Rect bounds = mRect;
        mRect.top = getPaddingTop();
        mRect.bottom = mRect.top + getRenderHeight();

        // Draw the list dividers
        for (int i = 0; i < count; i++) {
            // Don't draw a divider to the right of the last item in the adapter
            if (!(i == count - 1 && isLastItemInAdapter(mRightViewAdapterIndex))) {
                View child = getChildAt(i);

                bounds.left = child.getRight();
                bounds.right = child.getRight() + mDividerWidth;

                // Clip at the left edge of the screen
                if (bounds.left < getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                }

                // Clip at the right edge of the screen
                if (bounds.right > getWidth() - getPaddingRight()) {
                    bounds.right = getWidth() - getPaddingRight();
                }

                // Draw a divider to the right of the child
                drawDivider(canvas, bounds);

                // If the first view, determine if a divider should be shown to
                // the left of it.
                // A divider should be shown if the left side of this view does
                // not fill to the left edge of the screen.
                if (i == 0 && child.getLeft() > getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                    bounds.right = child.getLeft();
                    drawDivider(canvas, bounds);
                }
            }
        }
    }

    /**
     * Draws a divider in the given bounds.
     * 
     * @param canvas
     *            The canvas to draw to.
     * @param bounds
     *            The bounds of the divider.
     */
    private void drawDivider(Canvas canvas, Rect bounds) {
        if (mDivider != null) {
            mDivider.setBounds(bounds);
            mDivider.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDividers(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawEdgeGlow(canvas);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        // Don't dispatch setPressed to our children. We call setPressed on
        // ourselves to
        // get the selector in the right state, but we don't want to press each
        // child.
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2,
            float velocityX, float velocityY) {
        mFlingTracker.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX,
                0, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    protected boolean onDown(MotionEvent e) {
        // If the user just caught a fling, then disable all touch actions until
        // they release their finger
        mBlockTouchAction = !mFlingTracker.isFinished();

        // Allow a finger down event to catch a fling
        mFlingTracker.forceFinished(true);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);

        unpressTouchedChild();

        if (!mBlockTouchAction) {
            // Find the child that was pressed
            final int index = getChildIndex((int) e.getX(),
                    (int) e.getY());
            if (index >= 0) {
                // Save off view being touched so it can later be released
                mViewBeingTouched = getChildAt(index);

                if (mViewBeingTouched != null) {
                    // Set the view as pressed
                    mViewBeingTouched.setPressed(true);
                    refreshDrawableState();
                }
            }
        }

        return true;
    }

    /** If a view is currently pressed then unpress it */
    private void unpressTouchedChild() {
        if (mViewBeingTouched != null) {
            // Set the view as not pressed
            mViewBeingTouched.setPressed(false);
            refreshDrawableState();

            // Null out the view so we don't leak it
            mViewBeingTouched = null;
        }
    }

    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return HorizontalListView.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                float velocityX, float velocityY) {
            return HorizontalListView.this.onFling(e1, e2, velocityX,
                    velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            // Lock the user into interacting just with this view
            requestParentListViewToNotInterceptTouchEvents(true);

            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_TOUCH_SCROLL);
            unpressTouchedChild();
            mNextX += (int) distanceX;
            updateOverscrollAnimation(Math.round(distanceX));
            requestLayout();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            unpressTouchedChild();
            OnItemClickListener onItemClickListener = getOnItemClickListener();

            final int index = getChildIndex((int) e.getX(),
                    (int) e.getY());

            // If the tap is inside one of the child views, and we are not
            // blocking touches
            if (index >= 0 && !mBlockTouchAction) {
                View child = getChildAt(index);
                int adapterIndex = mLeftViewAdapterIndex + index;

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(
                            HorizontalListView.this, child,
                            adapterIndex,
                            mAdapter.getItemId(adapterIndex));
                    return true;
                }
            }

            if (mOnClickListener != null && !mBlockTouchAction) {
                mOnClickListener.onClick(HorizontalListView.this);
            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            unpressTouchedChild();

            final int index = getChildIndex((int) e.getX(),
                    (int) e.getY());
            if (index >= 0 && !mBlockTouchAction) {
                View child = getChildAt(index);
                OnItemLongClickListener onItemLongClickListener = getOnItemLongClickListener();
                if (onItemLongClickListener != null) {
                    int adapterIndex = mLeftViewAdapterIndex + index;
                    boolean handled = onItemLongClickListener
                            .onItemLongClick(HorizontalListView.this,
                                    child, adapterIndex,
                                    mAdapter.getItemId(adapterIndex));

                    if (handled) {
                        // BZZZTT!!1!
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    }
                }
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Detect when the user lifts their finger off the screen after a touch
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // If not flinging then we are idle now. The user just finished a
            // finger scroll.
            if (mFlingTracker == null || mFlingTracker.isFinished()) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }

            // Allow the user to interact with parent views
            requestParentListViewToNotInterceptTouchEvents(false);

            releaseEdgeGlow();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            unpressTouchedChild();
            releaseEdgeGlow();

            // Allow the user to interact with parent views
            requestParentListViewToNotInterceptTouchEvents(false);
        }

        return super.onTouchEvent(event);
    }

    /** Release the EdgeGlow so it animates */
    private void releaseEdgeGlow() {
        if (mEdgeGlowLeft != null) {
            mEdgeGlowLeft.onRelease();
        }

        if (mEdgeGlowRight != null) {
            mEdgeGlowRight.onRelease();
        }
    }

    /**
     * Sets a listener to be called when the HorizontalListView has been
     * scrolled to a point where it is running low on data. An example use case
     * is wanting to auto download more data when the user has scrolled to the
     * point where only 10 items are left to be rendered off the right of the
     * screen. To get called back at that point just register with this function
     * with a numberOfItemsLeftConsideredLow value of 10. <br>
     * <br>
     * This will only be called once to notify that the HorizontalListView is
     * running low on data. Calling notifyDataSetChanged on the adapter will
     * allow this to be called again once low on data.
     * 
     * @param listener
     *            The listener to be notified when the number of array adapters
     *            items left to be shown is running low.
     * 
     * @param numberOfItemsLeftConsideredLow
     *            The number of array adapter items that have not yet been
     *            displayed that is considered too low.
     */
    public void setRunningOutOfDataListener(
            RunningOutOfDataListener listener,
            int numberOfItemsLeftConsideredLow) {
        mRunningOutOfDataListener = listener;
        mRunningOutOfDataThreshold = numberOfItemsLeftConsideredLow;
    }

    /**
     * This listener is used to allow notification when the HorizontalListView
     * is running low on data to display.
     */
    public static interface RunningOutOfDataListener {
        /**
         * Called when the HorizontalListView is running out of data and has
         * reached at least the provided threshold.
         */
        void onRunningOutOfData();
    }

    /**
     * Determines if we are low on data and if so will call to notify the
     * listener, if there is one, that we are running low on data.
     */
    private void determineIfLowOnData() {
        // Check if the threshold has been reached and a listener is registered
        if (mRunningOutOfDataListener != null
                && mAdapter != null
                && mAdapter.getCount() - (mRightViewAdapterIndex + 1) < mRunningOutOfDataThreshold) {

            // Prevent notification more than once
            if (!mHasNotifiedRunningLowOnData) {
                mHasNotifiedRunningLowOnData = true;
                mRunningOutOfDataListener.onRunningOutOfData();
            }
        }
    }

    /**
     * Register a callback to be invoked when the HorizontalListView has been
     * clicked.
     * 
     * @param listener
     *            The callback that will be invoked.
     */
    @Override
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when the view scroll
     * state has changed.
     */
    public interface OnScrollStateChangedListener {
        public enum ScrollState {
            /**
             * The view is not scrolling. Note navigating the list using the
             * trackball counts as being in the idle state since these
             * transitions are not animated.
             */
            SCROLL_STATE_IDLE,

            /**
             * The user is scrolling using touch, and their finger is still on
             * the screen
             */
            SCROLL_STATE_TOUCH_SCROLL,

            /**
             * The user had previously been scrolling using touch and had
             * performed a fling. The animation is now coasting to a stop
             */
            SCROLL_STATE_FLING
        }

        /**
         * Callback method to be invoked when the scroll state changes.
         * 
         * @param scrollState
         *            The current scroll state.
         */
        public void onScrollStateChanged(ScrollState scrollState);
    }

    /**
     * Sets a listener to be invoked when the scroll state has changed.
     * 
     * @param listener
     *            The listener to be invoked.
     */
    public void setOnScrollStateChangedListener(
            OnScrollStateChangedListener listener) {
        mOnScrollStateChangedListener = listener;
    }

    /**
     * Call to set the new scroll state. If it has changed and a listener is
     * registered then it will be notified.
     */
    private void setCurrentScrollState(
            OnScrollStateChangedListener.ScrollState newScrollState) {
        // If the state actually changed then notify listener if there is one
        if (mCurrentScrollState != newScrollState
                && mOnScrollStateChangedListener != null) {
            mOnScrollStateChangedListener
                    .onScrollStateChanged(newScrollState);
        }

        mCurrentScrollState = newScrollState;
    }

    /**
     * Updates the over scroll animation based on the scrolled offset.
     * 
     * @param scrolledOffset
     *            The scroll offset
     */
    private void updateOverscrollAnimation(final int scrolledOffset) {
        if (mEdgeGlowLeft == null || mEdgeGlowRight == null)
            return;

        // Calculate where the next scroll position would be
        int nextScrollPosition = mCurrentX + scrolledOffset;

        // If not currently in a fling (Don't want to allow fling offset updates
        // to cause over scroll animation)
        if (mFlingTracker == null || mFlingTracker.isFinished()) {
            // If currently scrolled off the left side of the list and the
            // adapter is not empty
            if (nextScrollPosition < 0) {

                // Calculate the amount we have scrolled since last frame
                int overscroll = Math.abs(scrolledOffset);

                // Tell the edge glow to redraw itself at the new offset
                mEdgeGlowLeft.onPull((float) overscroll
                        / getRenderWidth());

                // Cancel animating right glow
                if (!mEdgeGlowRight.isFinished()) {
                    mEdgeGlowRight.onRelease();
                }
            } else if (nextScrollPosition > mMaxX) {
                // Scrolled off the right of the list

                // Calculate the amount we have scrolled since last frame
                int overscroll = Math.abs(scrolledOffset);

                // Tell the edge glow to redraw itself at the new offset
                mEdgeGlowRight.onPull((float) overscroll
                        / getRenderWidth());

                // Cancel animating left glow
                if (!mEdgeGlowLeft.isFinished()) {
                    mEdgeGlowLeft.onRelease();
                }
            }
        }
    }

    /**
     * Checks if the edge glow should be used enabled. The glow is not enabled
     * unless there are more views than can fit on the screen at one time.
     */
    private boolean isEdgeGlowEnabled() {
        if (mAdapter == null || mAdapter.isEmpty())
            return false;

        // If the maxx is more then zero then the user can scroll, so the edge
        // effects should be shown
        return mMaxX > 0;
    }

    @TargetApi(11)
    /** Wrapper class to protect access to API version 11 and above features */
    private static final class HoneycombPlus {
        static {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                throw new RuntimeException(
                        "Should not get to HoneycombPlus class unless sdk is >= 11!");
            }
        }

        /** Sets the friction for the provided scroller */
        public static void setFriction(Scroller scroller,
                float friction) {
            if (scroller != null) {
                scroller.setFriction(friction);
            }
        }
    }

    @TargetApi(14)
    /** Wrapper class to protect access to API version 14 and above features */
    private static final class IceCreamSandwichPlus {
        static {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                throw new RuntimeException(
                        "Should not get to IceCreamSandwichPlus class unless sdk is >= 14!");
            }
        }

        /** Gets the velocity for the provided scroller */
        public static float getCurrVelocity(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }
}
