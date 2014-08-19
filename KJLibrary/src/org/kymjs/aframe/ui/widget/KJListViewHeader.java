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

import org.kymjs.kjlibrary.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 下拉刷新控件的头部（可供KJListView、KJScrollView使用）
 * 
 * <b>创建时间</b> 2014-7-5
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class KJListViewHeader extends LinearLayout {
    /** 头部刷新状态 */
    public enum RefreshState {
        STATE_NORMAL, // 原样
        STATE_READY, // 完成
        STATE_REFRESHING // 正在刷新
    }

    // flag
    private RefreshState mState = RefreshState.STATE_NORMAL;

    // widget
    private LinearLayout layout; // 头部layout
    private ImageView arrowImageView; // 指示箭头图片
    private ProgressBar progressBar; // 刷新中的环形等待条
    private TextView hintTextView; // 刷新提示文字（上拉刷新、下拉刷新、正在刷新）

    // anim
    private Animation rotateUpAnim;
    private Animation rotateDownAnim;

    // data
    private final int ROTATE_ANIM_DURATION = 180;

    public KJListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 初始化组件
     */
    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        layout = (LinearLayout) View.inflate(context,
                R.layout.pagination_listview_header, null);
        addView(layout, lp);
        setGravity(Gravity.BOTTOM);
        arrowImageView = (ImageView) findViewById(R.id.pagination_header_arrow);
        hintTextView = (TextView) findViewById(R.id.pagination_header_hint_textview);
        progressBar = (ProgressBar) findViewById(R.id.pagination_header_progressbar);

        // 初始化箭头方向
        rotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateUpAnim.setFillAfter(true);
        rotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateDownAnim.setFillAfter(true);
    }

    /**
     * 设置顶部组件的显示
     * 
     * @param state
     *            顶部组件当前状态
     */
    public void setState(RefreshState state) {
        if (state == mState)
            return;
        // 刷新状态
        if (state == RefreshState.STATE_REFRESHING) {
            arrowImageView.clearAnimation();
            arrowImageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            // 显示箭头图片
            arrowImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
        case STATE_NORMAL:
            if (mState == RefreshState.STATE_READY) {
                arrowImageView.startAnimation(rotateDownAnim);
            }
            if (mState == RefreshState.STATE_REFRESHING) {
                arrowImageView.clearAnimation();
            }
            hintTextView.setText("有一种下拉可以刷新");
            break;
        case STATE_READY:
            if (mState != RefreshState.STATE_READY) {
                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(rotateUpAnim);
                hintTextView.setText("有一种刷新叫做放手");
            }
            break;
        case STATE_REFRESHING:
            hintTextView.setText("正在刷新…");
            break;
        default:
        }
        mState = state;
    }

    /**
     * 设置显示高度
     */
    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
                .getLayoutParams();
        params.height = height;
        layout.setLayoutParams(params);
    }

    /**
     * 获取高度
     */
    public int getVisibleHeight() {
        return layout.getHeight();
    }
}
