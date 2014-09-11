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

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private ProgressBar progressBar; // 刷新中的环形等待条
    private TextView hintTextView; // 刷新提示文字（上拉刷新、下拉刷新、正在刷新）
    RelativeLayout layout; // 头部layout
    TextView timeTextView; // 刷新时间

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
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout l = new LinearLayout(context);
        l.setGravity(Gravity.CENTER);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(params);
        hintTextView = new TextView(context);
        hintTextView.setGravity(Gravity.CENTER);
        timeTextView = new TextView(context);
        timeTextView.setGravity(Gravity.CENTER);
        l.addView(hintTextView);
        l.addView(timeTextView);
        layout.addView(l);
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(progressParams);
        progressBar.setPadding(20, 0, 0, 0);
        layout.addView(progressBar);
        addView(layout, lp);
        setGravity(Gravity.BOTTOM);

        // 初始化箭头方向
        rotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateUpAnim.setFillAfter(true);
        rotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
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
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
        case STATE_NORMAL:
            hintTextView.setText("有一种下拉可以刷新");
            break;
        case STATE_READY:
            if (mState != RefreshState.STATE_READY) {
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
