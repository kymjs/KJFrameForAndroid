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

import java.util.Set;
import java.util.TreeSet;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.OSCBlogAdapter;
import org.kymjs.blog.domain.OSCBlog;
import org.kymjs.blog.domain.OSCBlogList;
import org.kymjs.blog.domain.SimpleBackPage;
import org.kymjs.blog.ui.SimpleBackActivity;
import org.kymjs.blog.ui.widget.EmptyLayout;
import org.kymjs.blog.ui.widget.listview.FooterLoadingLayout;
import org.kymjs.blog.ui.widget.listview.ILoadingLayout.State;
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
 * 
 * 第三方博客列表
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class OSCBlogListFragment extends TitleBarFragment {

    public static final String TAG = OSCBlogListFragment.class.getSimpleName();

    @BindView(id = R.id.empty_layout)
    private EmptyLayout mEmptyLayout;
    @BindView(id = R.id.listview)
    private PullToRefreshList mRefreshLayout;
    private ListView mListView;

    private OSCBlogAdapter adapter;
    private final Set<OSCBlog> mDatas = new TreeSet<OSCBlog>();
    private KJHttp kjh;

    private final String OSCBLOG_HOST = "http://www.oschina.net/action/api/userblog_list?authoruid=";
    private final String OSCBLOG_INDEX = "&pageSize=20&pageIndex=";
    private int BLOGLIST_ID = 1428332;
    private String cache;
    private SimpleBackActivity aty;
    private String titleBarName;

    public static String BLOGLIST_KEY = "osc_blog_id_key";

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View root = View.inflate(outsideAty,
                R.layout.frag_pull_refresh_listview, null);
        aty = (SimpleBackActivity) getActivity();
        return root;
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        int hour = StringUtils.toInt(StringUtils.getDataTime("HH"), 0);
        if (hour > 7 && hour < 10) { // 如果是在早上7点到10点，就缓存的时间短一点
            config.cacheTime = 10;
        } else {
            config.cacheTime = 300;
        }
        config.useDelayCache = true;
        kjh = new KJHttp(config);

        Bundle bundle = aty.getBundleData();
        String name = null;
        if (bundle != null) {
            BLOGLIST_ID = bundle.getInt(BLOGLIST_KEY, 1428332);
            name = bundle.getString(BlogAuthorFragment.AUTHOR_NAME_KEY);
        }
        if (StringUtils.isEmpty(name)) {
            titleBarName = getString(R.string.osc_joke);
        } else {
            titleBarName = name + "的博客";
        }
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
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (parent.getAdapter() instanceof OSCBlogAdapter) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("oscblog_id", ((OSCBlog) parent.getAdapter()
                            .getItem(position)).getId());
                    SimpleBackActivity.postShowWith(outsideAty,
                            SimpleBackPage.OSC_BLOG_DETAIL, bundle);
                }
            }
        });
        mRefreshLayout.setPullLoadEnabled(true);
        ((FooterLoadingLayout) mRefreshLayout.getFooterLoadingLayout())
                .setNoMoreDataText("已经没有更多了~");
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh(mDatas.size() / 20);
            }
        });

        fillUI();
    }

    private void fillUI() {
        cache = kjh.getStringCache(OSCBLOG_HOST + BLOGLIST_ID);
        if (!StringUtils.isEmpty(cache)) {
            OSCBlogList dataRes = Parser.xmlToBean(OSCBlogList.class, cache);
            mDatas.addAll(dataRes.getBloglist());
            if (adapter == null) {
                adapter = new OSCBlogAdapter(mListView, mDatas,
                        R.layout.item_list_blog);
                mListView.setAdapter(adapter);
            } else {
                adapter.refresh(mDatas);
            }
            mEmptyLayout.dismiss();
        }
        refresh();
    }

    private void refresh() {
        refresh(0);
    }

    private void refresh(int index) {
        kjh.get(OSCBLOG_HOST + BLOGLIST_ID + OSCBLOG_INDEX + index,
                new HttpCallBack() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        KJLoger.debug(TAG + "网络请求：" + t);
                        if (t != null && !t.equals(cache)) {
                            OSCBlogList dataRes = Parser.xmlToBean(
                                    OSCBlogList.class, t);
                            int prevCount = mDatas.size();
                            mDatas.addAll(dataRes.getBloglist());
                            // 是否还有下一页
                            if (prevCount == mDatas.size()) {
                                mRefreshLayout.setHasMoreData(false);
                            }

                            if (adapter == null) {
                                adapter = new OSCBlogAdapter(mListView, mDatas,
                                        R.layout.item_list_blog);
                                mListView.setAdapter(adapter);
                            } else {
                                adapter.refresh(mDatas);
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
                        if (mRefreshLayout.getFooterLoadingLayout().getState() != State.NO_MORE_DATA) {
                            mRefreshLayout.onPullUpRefreshComplete();
                        }
                    }
                });
    }

    /**
     * 将在onResume方法中调用
     */
    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = titleBarName;
        actionBarRes.backImageId = R.drawable.titlebar_back;
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }

}
