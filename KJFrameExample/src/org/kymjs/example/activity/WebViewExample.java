package org.kymjs.example.activity;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.activity.SimpleBrowserTemplet;
import org.kymjs.example.R;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class WebViewExample extends SimpleBrowserTemplet {

    @BindView(id = R.id.edittext)
    private EditText mEtText;
    @BindView(id = R.id.button, click = true)
    private Button mBtn;
    @BindView(id = R.id.bottombar_content1, click = true)
    private RadioButton mRbtn1;
    @BindView(id = R.id.bottombar_content2, click = true)
    private RadioButton mRbtn2;
    @BindView(id = R.id.bottombar_content3, click = true)
    private RadioButton mRbtn3;
    @BindView(id = R.id.bottombar_content4, click = true)
    private RadioButton mRbtn4;

    /**
     * 如果不需要自定义界面，可以不用重写本方法。 <br>
     * 这里由于我添加了刷新、前进、后退等按钮、所以布局需要自定义
     */
    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.aty_webview);
    }

    @Override
    protected WebView initWebVie(WebView webview) {
        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl("http://heka.tn10000.com");
        mEtText.setText(webview.getUrl());
        return webview;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRbtn1.setText("返回");
        mRbtn2.setText("前进");
        mRbtn3.setText("刷新");
        mRbtn4.setText("菜单");
        mBtn.setText("搜索");
    }

    /**
     * 载入链接之前会被调用（此时我们可以显示一些进度条之类的）
     */
    @Override
    protected void onUrlLoading(WebView view, String url) {
        super.onUrlLoading(view, url);
        // 偷个懒，就改一下文字（图片）
        mRbtn3.setText("停止");
    }

    /**
     * 链接载入完成会被调用（此时我们可以把显示的进度条什么的隐藏掉）
     */
    @Override
    protected void onUrlFinished(WebView view, String url) {
        super.onUrlFinished(view, url);
        mRbtn3.setText("刷新");
    }

    /**
     * 获取到当前网页的title
     */
    @Override
    protected void getWebTitle(WebView view, String title) {
        super.getWebTitle(view, title);
        mEtText.setText(title);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.bottombar_content1:
            back();
            break;
        case R.id.bottombar_content2:
            next();
            break;
        case R.id.bottombar_content3:
            refresh();
            break;
        case R.id.bottombar_content4:
            break;
        case R.id.button:
            mWebView.loadUrl("http://" + mEtText.getText());
            break;
        }
    }

    private void back() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
        mEtText.setText(mWebView.getUrl());
    }

    private void next() {
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
        mEtText.setText(mWebView.getUrl());
    }

    private void refresh() {
        mWebView.reload();
        // 停止刷新
        // mWebView.stopLoading();
    }
}
