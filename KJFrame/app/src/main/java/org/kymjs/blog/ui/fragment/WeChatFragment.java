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

import java.util.List;

import org.kymjs.blog.R;
import org.kymjs.blog.adapter.WeChatAdapter;
import org.kymjs.blog.domain.EverydayMessage;
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
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 仿照公众号推送界面创建的每日推送列表<br>
 * 为什么有些人对外表现的那么大家风范乐于分享，可是私底下却什么都不肯说呢，难道仅仅是因为指导一个人得不到那种被众人膜拜的感觉吗？<br>
 * 有关本界面有一段很有意思的故事：最初做这个界面的时候，出于偷懒，并没有打算自己写，在网上搜索到了一篇写这种布局的博客，博主叫秦元培。(http://
 * blog.csdn.net/qinyuanpei/article/details/17734755)。心想，这次又可以少写好多代码了。
 * 可是博客内并没有提供项目介绍的sample,于是根据博客中QQ信息发送了好友请求:你好，刚看了你的博客，麻烦你能发一份Demo给我吗。结果被拒绝了。。。<br>
 * 诶，还是自己写吧,于是才有了这个界面
 * 
 * @author kymjs (http://www.kymjs.com/)
 * @since 2015-3
 */
public class WeChatFragment extends TitleBarFragment {

    public static final String TAG = WeChatFragment.class.getSimpleName();

    @BindView(id = R.id.wechat_listview)
    private ListView mListView;

    private WeChatAdapter adapter;

    private KJHttp kjh;

    private final String EVERYDAY_HOST = "http://www.kymjs.com/api/json_every_message";
    private String cache;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View root = View.inflate(outsideAty, R.layout.frag_wechat, null);
        return root;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.today_message);
        actionBarRes.backImageId = R.drawable.titlebar_back;
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        config.cacheTime = 300;
        config.useDelayCache = true;
        kjh = new KJHttp(config);
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mListView.setStackFromBottom(true);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setDivider(new ColorDrawable(android.R.color.transparent));
        cache = kjh.getStringCache(EVERYDAY_HOST);
        if (!StringUtils.isEmpty(cache)) {
            List<EverydayMessage> datas = Parser.getEveryDayMsg(cache);
            if (adapter == null) {
                adapter = new WeChatAdapter(outsideAty, datas);
                mListView.setAdapter(adapter);
                mListView.scrollTo(0, mListView.getBottom());
            } else {
                adapter.refresh(datas);
            }
        }
        refresh();
    }

    private void refresh() {
        kjh.get(EVERYDAY_HOST, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug(TAG + "网络请求：" + t);
                if (t != null && !t.equals(cache)) {
                    List<EverydayMessage> datas = Parser.getEveryDayMsg(t);
                    if (adapter == null) {
                        adapter = new WeChatAdapter(outsideAty, datas);
                        mListView.setAdapter(adapter);
                    } else {
                        adapter.refresh(datas);
                    }
                }
            }
        });
    }
}
