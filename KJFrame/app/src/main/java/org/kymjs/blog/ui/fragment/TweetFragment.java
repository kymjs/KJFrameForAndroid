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

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.TweetAdapter;
import org.kymjs.blog.domain.SimpleBackPage;
import org.kymjs.blog.domain.Tweet;
import org.kymjs.blog.domain.TweetsList;
import org.kymjs.blog.ui.SimpleBackActivity;
import org.kymjs.blog.ui.widget.EmptyLayout;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase;
import org.kymjs.blog.ui.widget.listview.PullToRefreshBase.OnRefreshListener;
import org.kymjs.blog.ui.widget.listview.PullToRefreshList;
import org.kymjs.blog.utils.Parser;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * [吐槽]界面
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class TweetFragment extends TitleBarFragment {

    public static final String TAG = TweetFragment.class.getSimpleName();

    @BindView(id = R.id.empty_layout)
    private EmptyLayout mEmptyLayout;
    @BindView(id = R.id.listview)
    private PullToRefreshList mRefreshLayout;
    private ListView mListView;

    private TweetAdapter adapter;

    private KJHttp kjh;
    private final Set<Tweet> tweets = new TreeSet<Tweet>();
    private final String OSCTWEET_HOST = "http://www.oschina.net/action/api/tweet_list?pageSize=20&pageIndex=";

    public static final int REQUEST_CODE_RECORD = 1;
    public static final int REQUEST_CODE_IMAGE = 2;
    public static final String CONTENT_KEY = "Tweet_content_key";
    public static final String AUDIOPATH_KEY = "Tweet_audiopath_key";
    public static final String IMAGEPATH_KEY = "Tweet_imagepath_key";

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
        actionBarRes.title = getString(R.string.str_tweet_title);
        actionBarRes.backImageId = R.drawable.titlebar_back;
        actionBarRes.menuImageId = R.drawable.titlebar_add;
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        config.cacheTime = 0;
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
        mListView.setDivider(new ColorDrawable(0x00000000));
        mListView.setSelector(new ColorDrawable(0x00000000));
        mRefreshLayout.setPullLoadEnabled(true);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh(0);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                refresh();
            }
        });
        fillUI();
    }

    /**
     * 首次进入时填充数据
     */
    private void fillUI() {
        refresh(0);
    }

    /**
     * 刷新
     */
    private void refresh() {
        double page = tweets.size() / 20;
        page += 1.9; // 因为服务器返回的可能会少于20条，所以采用小数进一法加载下一页
        refresh((int) page);
    }

    private void refresh(final int page) {
        kjh.get(OSCTWEET_HOST + page, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug(TAG + "网络请求" + t);
                List<Tweet> datas = Parser.xmlToBean(TweetsList.class, t)
                        .getList();
                tweets.addAll(datas);
                if (adapter == null) {
                    adapter = new TweetAdapter(mListView, tweets,
                            R.layout.item_list_tweet);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.refresh(tweets);
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
                mRefreshLayout.onPullUpRefreshComplete();
            }
        });
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        SimpleBackActivity.postShowForResult(this, 1,
                SimpleBackPage.OSC_TWEET_SEND);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (resultCode) {
        case REQUEST_CODE_RECORD:
            handleSubmit(data.getStringExtra(CONTENT_KEY), null,
                    data.getStringExtra(AUDIOPATH_KEY));
            break;
        case REQUEST_CODE_IMAGE:
            handleSubmit(data.getStringExtra(CONTENT_KEY),
                    new File(data.getStringExtra(IMAGEPATH_KEY)), null);
            break;
        default:
            break;
        }
    }

    /**
     * 发布动弹
     */
    private void handleSubmit(String strSpeech, File imageFile, String audioPath) {
        HttpConfig config = new HttpConfig();
        config.cacheTime = 0;
        KJHttp kjh = new KJHttp(config);
        HttpParams params = new HttpParams();
        params.putHeaders("cookie", UIHelper.getUser(outsideAty).getCookie());
        params.put("uid", UIHelper.getUser(outsideAty).getUid());
        params.put("msg", strSpeech + "          ——————我只是一条小尾巴");

        if (imageFile != null && imageFile.exists()) {
            params.put("img", imageFile);
        }
        if (!StringUtils.isEmpty(audioPath)) {
            params.put("amr", new File(audioPath));
        }
        kjh.post("http://www.oschina.net/action/api/tweet_pub", params,
                new HttpCallBack() {
                    @Override
                    public void onPreStart() {
                        super.onPreStart();
                        // 设置上传动画
                    }

                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        KJLoger.debug("发表动弹:" + t);
                        // 隐藏上传动画
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        // 设置上传动画失败图标
                    }
                });
    }
}
