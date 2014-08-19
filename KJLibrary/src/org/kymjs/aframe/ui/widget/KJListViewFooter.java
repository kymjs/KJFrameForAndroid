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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 上拉加载ListView的底部<br>
 * 
 * <b>创建时间</b> 2014-7-5
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class KJListViewFooter extends LinearLayout {
    /** 头部刷新状态 */
    public enum LoadMoreState {
        STATE_NORMAL, // 原样
        STATE_READY, // 完成
        STATE_LOADING // 正在刷新
    }

    private View contentView;
    private View progressBar;
    private TextView hintView;

    public KJListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 初始化底部组件
     */
    private void initView(Context context) {
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.pagination_listview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        contentView = moreView.findViewById(R.id.pagination_footer_content);
        progressBar = moreView.findViewById(R.id.pagination_footer_progressbar);
        hintView = (TextView) moreView
                .findViewById(R.id.pagination_footer_hint_textview);
    }

    /**
     * 设置底部组件的显示
     * 
     * @param state
     *            底部组件当前状态
     */
    public void setState(LoadMoreState state) {
        hintView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        if (state == LoadMoreState.STATE_READY) {
            hintView.setVisibility(View.VISIBLE);
            hintView.setText("松开载入更多");
        } else if (state == LoadMoreState.STATE_LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            hintView.setVisibility(View.VISIBLE);
            hintView.setText("上拉查看更多");
        }
    }

    /**
     * 设置底边距
     * 
     * @param height
     */
    public void setBottomMargin(int height) {
        if (height < 0) {
            return;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.bottomMargin = height;
        contentView.setLayoutParams(params);
    }

    /**
     * 获取底边距
     */
    public int getBottomMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        return params.bottomMargin;
    }

    /**
     * 普通状态
     */
    public void normal() {
        hintView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 加载状态
     */
    public void loading() {
        hintView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 没有更多时隐藏底部
     */
    public void hide() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.height = 0;
        contentView.setLayoutParams(params);
    }

    /**
     * 显示底部
     */
    public void show() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.height = LayoutParams.WRAP_CONTENT;
        contentView.setLayoutParams(params);
    }
}
