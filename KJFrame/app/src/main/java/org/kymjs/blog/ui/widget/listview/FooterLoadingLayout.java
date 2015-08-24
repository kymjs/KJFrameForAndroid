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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 这个类封装了上拉加载的布局
 * 
 * @author kymjs (https://github.com/kymjs)
 * @since 2015-3
 */
public class FooterLoadingLayout extends LoadingLayout {
    private ProgressBar mProgressBar;
    private TextView mHintView;

    private CharSequence strNoMoreData;
    private CharSequence strRefreshing;
    private CharSequence strPullToRefresh;
    private CharSequence strReleaseToRefresh;

    public FooterLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    public FooterLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mProgressBar = (ProgressBar) findViewById(R.id.pull_to_load_footer_progressbar);
        mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);
        setState(State.RESET);
        strNoMoreData = context.getString(R.string.pushmsg_center_no_more_msg);
        strRefreshing = context
                .getString(R.string.pull_to_refresh_header_hint_loading);
        strReleaseToRefresh = context
                .getString(R.string.pull_to_refresh_header_hint_ready);
        strPullToRefresh = context
                .getString(R.string.pull_to_refresh_header_hint_normal2);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = View.inflate(context, R.layout.pull_to_load_footer,
                null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {}

    @Override
    public int getContentSize() {
        View view = findViewById(R.id.pull_to_load_footer_content);
        if (null != view) {
            return view.getHeight();
        }

        return (int) (getResources().getDisplayMetrics().density * 40);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.INVISIBLE);

        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        mHintView.setText(strRefreshing);
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(strPullToRefresh);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(strReleaseToRefresh);
    }

    @Override
    protected void onRefreshing() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(strRefreshing);
    }

    @Override
    protected void onNoMoreData() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(strNoMoreData);
    }

    public void setNoMoreDataText(CharSequence label) {
        this.strNoMoreData = label;
    }

    public void setRefreshingText(CharSequence label) {
        this.strRefreshing = label;
    }

    public void setPullToRefreshText(CharSequence label) {
        this.strPullToRefresh = label;
    }

    public void setReleaseToRefresh(CharSequence label) {
        this.strReleaseToRefresh = label;
    }

}
