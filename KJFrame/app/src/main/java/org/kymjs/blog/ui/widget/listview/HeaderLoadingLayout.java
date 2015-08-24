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

import org.kymjs.blog.R;
import org.kymjs.blog.utils.PullTip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class HeaderLoadingLayout extends LoadingLayout {
    /** 旋转动画的时间 */
    static final int ROTATION_ANIMATION_DURATION = 1200;
    /** 动画插值 */
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    /** Header的容器 */
    private RelativeLayout mHeaderContainer;
    /** 箭头图片 */
    private ImageView mArrowImageView;
    /** 状态提示TextView */
    private TextView mHintTextView;
    /** 旋转的动画 */
    private Animation mRotateAnimation;

    private String refreshTip = PullTip.tips[(int) (Math.random() * PullTip.tips.length)];

    public HeaderLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    public HeaderLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
        mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
        mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);

        mArrowImageView.setScaleType(ScaleType.CENTER);
        mArrowImageView.setImageResource(R.drawable.default_ptr_rotate);

        float pivotValue = 0.5f; // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f; // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(0.0f, toDegree,
                Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = View.inflate(context, R.layout.pull_to_refresh_header,
                null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {}

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            return mHeaderContainer.getHeight();
        }

        return (int) (getResources().getDisplayMetrics().density * 60);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        resetRotation();
        // mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
        refreshTip = PullTip.tips[(int) (Math.random() * PullTip.tips.length)];
        mHintTextView.setText(refreshTip);
    }

    @Override
    protected void onReleaseToRefresh() {
        // mHintTextView.setText(R.string.pull_to_refresh_header_hint_ready);
        mHintTextView.setText(refreshTip);
    }

    @Override
    protected void onPullToRefresh() {
        // mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
        mHintTextView.setText(refreshTip);
    }

    @Override
    protected void onRefreshing() {
        resetRotation();
        mArrowImageView.startAnimation(mRotateAnimation);
        // mHintTextView.setText(R.string.pull_to_refresh_header_hint_loading);
        mHintTextView.setText(refreshTip);
    }

    @Override
    public void onPull(float scale) {
        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
        mArrowImageView.setRotation(angle);
    }

    /**
     * 重置动画
     */
    private void resetRotation() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setRotation(0);
    }
}
