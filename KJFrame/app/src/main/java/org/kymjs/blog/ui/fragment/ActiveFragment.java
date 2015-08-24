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
package org.kymjs.blog.ui.fragment;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.ActiveAdapter;
import org.kymjs.blog.domain.ActiveList;
import org.kymjs.blog.ui.Browser;
import org.kymjs.blog.ui.widget.EmptyLayout;
import org.kymjs.blog.ui.widget.listview.FooterLoadingLayout;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;
import org.kymjs.blog.ui.widget.listview.PullToRefreshList;
import org.kymjs.blog.utils.Parser;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * [猿活动]界面
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class ActiveFragment extends TitleBarFragment implements
        OnItemClickListener {

    public static final String TAG = ActiveFragment.class.getSimpleName();

    @BindView(id = R.id.empty_layout)
    private EmptyLayout mEmptyLayout;
    @BindView(id = R.id.listview)
    private PullToRefreshList mRefreshLayout;
    private ListView mListView;

    private ActiveAdapter adapter;

    private KJHttp kjh;
    private final String ACTIVE_HOST = "http://www.oschina.net/action/api/event_list";
    private String cache;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View root = View.inflate(outsideAty,
                R.layout.frag_pull_refresh_listview, null);
        return root;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.str_active_title);
        actionBarRes.backImageId = R.drawable.titlebar_back;
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        config.useDelayCache = false;
        kjh = new KJHttp(config);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mEmptyLayout.setOnLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                refresh();
            }
        });

        mListView = mRefreshLayout.getRefreshView();
        mListView.setDivider(new ColorDrawable(android.R.color.transparent));
        mListView.setSelector(new ColorDrawable(android.R.color.transparent));
        mListView.setOnItemClickListener(this);
        mRefreshLayout.setPullLoadEnabled(true);
        ((FooterLoadingLayout) mRefreshLayout.getFooterLoadingLayout())
                .setNoMoreDataText("暂时还没有更多活动");
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                mRefreshLayout.setHasMoreData(false);
            }
        });
        fillUI();
    }

    /**
     * 首次进入时填充数据
     */
    private void fillUI() {
        cache = kjh.getStringCache(ACTIVE_HOST);
        if (!StringUtils.isEmpty(cache)) {
            ActiveList dataRes = Parser.xmlToBean(ActiveList.class, cache);
            if (adapter == null) {
                adapter = new ActiveAdapter(mListView, dataRes.getEvents(),
                        R.layout.item_list_active);
                mListView.setAdapter(adapter);
            } else {
                adapter.refresh(dataRes.getEvents());
            }
            mEmptyLayout.dismiss();
        }
        refresh();
    }

    private void refresh() {
        kjh.get(ACTIVE_HOST, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug(TAG + "网络请求：" + t);
                if (t != null && !t.equals(cache)) {
                    ActiveList dataRes = Parser.xmlToBean(ActiveList.class, t);
                    if (adapter == null) {
                        adapter = new ActiveAdapter(mListView, dataRes
                                .getEvents(), R.layout.item_list_active);
                        mListView.setAdapter(adapter);
                    } else {
                        adapter.refresh(dataRes.getEvents());
                    }
                }
                mEmptyLayout.dismiss();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                if (adapter != null && adapter.getCount() > 0) {
                    return;
                } else {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onPullDownRefreshComplete();
            }
        });
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Intent it = new Intent(outsideAty, Browser.class);
        it.putExtra(Browser.BROWSER_KEY, adapter.getItem(position).getUrl());
        it.putExtra(Browser.BROWSER_TITLE_KEY, adapter.getItem(position)
                .getTitle());
        outsideAty.showActivity(outsideAty, it);
    }
}
