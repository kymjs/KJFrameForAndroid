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

import org.kymjs.aframe.ui.widget.KJListViewFooter.LoadMoreState;
import org.kymjs.aframe.ui.widget.KJListViewHeader.RefreshState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * 可上下拉刷新listview<br>
 * 
 * <b>创建时间</b> 2014-7-5
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class KJListView extends ListView implements OnScrollListener {
    // listener
    private OnScrollListener mScrollListener;
    private KJRefreshListener mListViewListener;

    // widget
    private KJListViewHeader mHeaderView; // 头部控件
    private TextView mHeaderTimeView; // 头部显示上次刷新时间的控件
    private RelativeLayout mHeaderViewContent;// 头部控件的layout（依靠它计算头部的显示高度）
    private KJListViewFooter mFooterView; // 底部控件
    private RelativeLayout mFooterViewContent; // 底部内容View

    // data
    private float mLastY = -1;
    private int mHeaderViewHeight; // 头部高度
    private int mTotalItemCount; // item的总数

    // flag
    private boolean mEnablePullRefresh = true; // 是否可以下拉刷新
    private boolean mPullRefreshing = false; // 是否正在下拉刷新
    private boolean mEnablePullLoad = false; // 是否可以上拉刷新
    private boolean mPullLoading = false; // 是否正在上拉刷新
    private boolean mIsFooterReady = false; // 用于确保footer控件只加载一次

    enum ScrollBackEnum {
        SCROLLBACK_HEADER, // 滚动到头部
        SCROLLBACK_FOOTER // 滚动到底部
    }

    // 处理滚动的功能
    private Scroller mScroller;
    private ScrollBackEnum mScrollBack; // 应该回滚的位置
    private final static int SCROLL_DURATION = 400; // 滚动时间
    private final static int PULL_LOAD_MORE_DELTA = 50; // 拉动距离大于50px 加载更多
    private final static float OFFSET_RADIO = 1.8f;

    public KJListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public KJListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public KJListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    /**
     * 初始化listview
     */
    private void initWithContext(Context context) {
        mScroller = new Scroller(context,
                new DecelerateInterpolator());
        // 绑定监听
        super.setOnScrollListener(this);

        // 初始化头部
        mHeaderView = new KJListViewHeader(context);
        mHeaderViewContent = mHeaderView.layout;
        mHeaderTimeView = mHeaderView.timeTextView;

        addHeaderView(mHeaderView);
        // 初始化头部高度
        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    public void onGlobalLayout() {
                        mHeaderViewHeight = mHeaderViewContent
                                .getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
        // 初始化底部
        mFooterView = new KJListViewFooter(context);
        mFooterViewContent = mFooterView.contentView;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mIsFooterReady == false) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastY = ev.getRawY();
            break;
        case MotionEvent.ACTION_MOVE:
            final float deltaY = ev.getRawY() - mLastY;
            mLastY = ev.getRawY();
            if (getFirstVisiblePosition() == 0
                    && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                // 第一项正在显示，标题显示或拉下来。
                updateHeaderHeight(deltaY / OFFSET_RADIO);
                invokeOnScrolling();
            } else if (getLastVisiblePosition() == mTotalItemCount - 1
                    && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                // 最后一项，已经拉起或想拉起来。
                updateFooterHeight(-deltaY / OFFSET_RADIO);
            }
            break;
        default:
            mLastY = -1; // reset
            if (getFirstVisiblePosition() == 0) {
                // 调用刷新
                if (mEnablePullRefresh
                        && mHeaderView.getVisibleHeight() > mHeaderViewHeight) {
                    mPullRefreshing = true;
                    mHeaderView
                            .setState(RefreshState.STATE_REFRESHING);
                    if (mListViewListener != null) {
                        mListViewListener.onRefresh();
                    }
                }
                resetHeaderHeight();
            } else if (getLastVisiblePosition() <= mTotalItemCount) {
                // 调用加载更多
                if (mEnablePullLoad
                        && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                    startLoadMore();
                }
                resetFooterHeight();
            }
            break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == ScrollBackEnum.SCROLLBACK_HEADER) {
                mHeaderView.setVisibleHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    /**
     * 滚动状态改变
     */
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // 发送给自己的监听器
        mTotalItemCount = totalItemCount;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    /************************ private config method ***************************/
    /**
     * 当头部或底部拉起后，调用它返回重置
     */
    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnKJScrollListener) {
            OnKJScrollListener l = (OnKJScrollListener) mScrollListener;
            l.onKJScrolling(this);
        }
    }

    /**
     * 更新头部高度
     */
    private void updateHeaderHeight(float delta) {
        mHeaderView.setVisibleHeight((int) delta
                + mHeaderView.getVisibleHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mHeaderView.getVisibleHeight() > mHeaderViewHeight) {
                mHeaderView.setState(RefreshState.STATE_READY);
            } else {
                mHeaderView.setState(RefreshState.STATE_NORMAL);
            }
        }
        setSelection(0); // scroll to top each time
    }

    /**
     * 重置头部高度
     */
    private void resetHeaderHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height <= 0) {
            return;
        }
        // 正在刷新，或头部没有完全显示，不做任务动作
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        // 默认高度
        int finalHeight = 0;
        // 若正在刷新，头部显示完全高度
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = ScrollBackEnum.SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        invalidate();
    }

    /**
     * 更新底部高度
     */
    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) {
                // 上拉足够高时，加载更多
                mFooterView.setState(LoadMoreState.STATE_READY);
            } else {
                mFooterView.setState(LoadMoreState.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);
    }

    /**
     * 重置底部高度
     */
    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = ScrollBackEnum.SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
                    SCROLL_DURATION);
            invalidate();
        }
    }

    /**
     * 加载更多
     */
    private void startLoadMore() {
        mPullLoading = true;
        mFooterView.setState(LoadMoreState.STATE_LOADING);
        if (mListViewListener != null) {
            mListViewListener.onLoadMore();
        }
    }

    /************************ public config method ****************************/

    /**
     * 返回头控件布局
     * 
     * @return 返回一个相对布局，开发者可自定义布局内容
     */
    public RelativeLayout getHeadView() {
        return mHeaderViewContent;
    }

    /**
     * 返回尾控件布局
     * 
     * @return 返回一个相对布局，开发者可自定义布局内容
     */
    public RelativeLayout getFooterView() {
        return mFooterViewContent;
    }

    /**
     * 设置自己的监听器
     */
    public void setOnRefreshListener(KJRefreshListener l) {
        mListViewListener = l;
    }

    /**
     * 设置下拉时的显示文字
     * 
     * @param normal
     *            刚开始下拉，还没有到放手的状态
     */
    public void setNormalText(String normal) {
        mHeaderView.setNormal(normal);
    }

    /**
     * 设置下拉回放时的显示文字
     * 
     * @param ready
     *            下拉完成后向上收缩，准备刷新时的状态
     */
    public void setReady(String ready) {
        mHeaderView.setReady(ready);
    }

    /**
     * 设置下拉刷新时的文字
     * 
     * @param refreshing
     *            正在刷新的状态
     */
    public void setRefreshingText(String refreshing) {
        mHeaderView.setRefreshing(refreshing);
    }

    /**
     * 设置上拉刷新时的文字
     * 
     * @param refreshing
     *            正在刷新的状态
     */
    public void setLoadMoreText(String refreshing) {
        mFooterView.setRefreshing(refreshing);
    }

    /**
     * 设置刷新时间
     * 
     * @param time
     */
    public void setRefreshTime(String time) {
        mHeaderTimeView.setText(time);
    }

    /**
     * 是否开启下拉刷新功能
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) {
            // 不允许
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否开启上拉加载更多数据功能
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            // 不允许
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
        } else {
            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(LoadMoreState.STATE_NORMAL);
            // 上拉或点击最后一行 都可以加载更多
            mFooterView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 停止刷新（方便外界调用，做一次封装）
     */
    public void stopRefreshData() {
        stopPullRefresh();
        stopLoadMore();
    }

    /**
     * 停止刷新，重置头部
     */
    public void stopPullRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * 停止加载更多，重置底部
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(LoadMoreState.STATE_NORMAL);
        }
    }

    /************************* interface call back ****************************/

    /**
     * 它将调用onxscrolling当页眉/页脚退回。
     */
    public interface OnKJScrollListener extends OnScrollListener {
        public void onKJScrolling(View view);
    }
}
