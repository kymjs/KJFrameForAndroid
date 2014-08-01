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
package org.kymjs.aframe.ui.activity;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 简单浏览器模板
 * 
 * @explain 开发者必须首先实现initWebVie(WebView mWebView)方法，将webview返回
 * @explain 若要显示网页，可手动调用mWebView.loadUrl(url);
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-6-23
 */
@SuppressLint("SetJavaScriptEnabled")
public abstract class SimpleBrowserTemplet extends BaseActivity {

    /** 浏览器的webview，你可以在子类中使用 */
    protected WebView mWebView;

    @Override
    public void setRootView() {}

    abstract protected WebView initWebVie(WebView mWebView);

    @Override
    protected void initWidget() {
        super.initWidget();
        mWebView = initWebVie(mWebView);
        initWebView();
    }

    /**
     * 初始化浏览器设置信息
     */
    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
    }

    /**
     * 返回事件屏蔽
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
