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

import java.io.File;

import org.kymjs.blog.AppConfig;
import org.kymjs.blog.R;
import org.kymjs.blog.ui.TitleBarActivity;
import org.kymjs.blog.ui.widget.KJScrollView;
import org.kymjs.blog.ui.widget.KJScrollView.OnViewTopPull;
import org.kymjs.blog.utils.Parser;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.utils.SystemTool;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 关于
 * 
 * @author kymjs (https://www.kymjs.com/)
 * 
 */
public class AboutFragment extends TitleBarFragment {

    @BindView(id = R.id.use_help, click = true)
    private RelativeLayout mRlHelp;
    @BindView(id = R.id.version, click = true)
    private RelativeLayout mRlVersion;
    @BindView(id = R.id.about_me, click = true)
    private RelativeLayout mRlAuthor;
    @BindView(id = R.id.root)
    private KJScrollView rootView;
    private KJHttp kjh;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View view = View.inflate(outsideAty, R.layout.frag_about, null);
        return view;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        actionBarRes.title = getString(R.string.about);
    }

    @Override
    protected void initData() {
        super.initData();
        HttpConfig config = new HttpConfig();
        config.cacheTime = 0;
        kjh = new KJHttp(config);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        rootView.setOnViewTopPullListener(new OnViewTopPull() {
            @Override
            public void onPull() {
                if (outsideAty instanceof TitleBarActivity) {
                    outsideAty.getCurtainView().expand();
                }
            }
        });
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.use_help:
            UIHelper.toBrowser(outsideAty, "http://www.kymjs.com");
            break;
        case R.id.version:
            update();
            break;
        case R.id.about_me:
            UIHelper.toBrowser(outsideAty, "http://blog.kymjs.com/about/");
            break;

        default:
            break;
        }
    }

    private void update() {
        kjh.get("http://www.kymjs.com/api/version", new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                KJLoger.debug("检测更新===" + t);
                checkVersion(t);
            }
        });
    }

    private void checkVersion(String json) {
        final String url = Parser.checkVersion(outsideAty, json);
        if (!StringUtils.isEmpty(url)) {
            if (SystemTool.isWiFi(outsideAty)) {
                download(url);
            } else {
                ViewInject.create().getExitDialog(outsideAty, "检测到新版本，是否更新",
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                download(url);
                            }
                        });
            }
        }
    }

    private void download(String url) {
        final File folder = FileUtils.getSaveFolder(AppConfig.saveFolder);
        File tempFile = new File(folder + "/kjblog.apk.tmp");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        ViewInject.toast("正在为你下载新版本");
        kjh.download(folder + "/kjblog.apk", url, new HttpCallBack() {
            /**
             * 下载过程
             */
            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
            }

            /**
             * 下载完成，开始安装
             */
            @Override
            public void onSuccess(byte[] t) {
                super.onSuccess(t);
                SystemTool.installApk(outsideAty, new File(folder
                        + "/kjblog.apk"));
            }
        });
    }
}
