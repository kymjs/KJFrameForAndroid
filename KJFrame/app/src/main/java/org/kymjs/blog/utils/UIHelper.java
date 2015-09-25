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
package org.kymjs.blog.utils;

import java.util.List;

import org.kymjs.blog.domain.SimpleBackPage;
import org.kymjs.blog.domain.User;
import org.kymjs.blog.ui.Browser;
import org.kymjs.blog.ui.ImageActivity;
import org.kymjs.blog.ui.MyBlogBrowser;
import org.kymjs.blog.ui.SimpleBackActivity;
import org.kymjs.blog.ui.fragment.OSCBlogDetailFragment;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.utils.StringUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

public class UIHelper {
    private static User user = null;

    /** 全局web样式 */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>";
    public final static String WEB_STYLE = linkCss
            + "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
            + "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
            + "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;overflow: auto;} "
            + "a.tag {font-size:15px;text-decoration:none;background-color:#cfc;color:#060;border-bottom:1px solid #B1D3EB;border-right:1px solid #B1D3EB;color:#3E6D8E;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;position:relative}</style>";

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";

    public static void toHome(Context cxt) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
        intent.addCategory(Intent.CATEGORY_HOME);
        cxt.startActivity(intent);
    }

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(15);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        webView.setWebViewClient(UIHelper.getWebViewClient());
    }

    public static String setHtmlCotentSupportImagePreview(String body) {
        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        // if ( ) {
        // 过滤掉 img标签的width,height属性
        body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
        // 添加点击图片放大支持
        // 添加点击图片放大支持
        body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                "$1$2\" onClick=\"showImagePreview('$2')\"");
        // } else {
        // // 过滤掉 img标签
        // body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        // }
        return body;
    }

    /**
     * 添加网页的点击图片展示支持
     */
    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    @JavascriptInterface
    public static void addWebImageShow(final Context cxt, WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new OnWebViewImageListener() {

            @Override
            @JavascriptInterface
            public void showImagePreview(String bigImageUrl) {
                if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {
                    UIHelper.showImagePreview(cxt, new String[] { bigImageUrl });
                }
            }
        }, "mWebViewImageListener");
    }

    /**
     * 获取webviewClient对象
     */
    public static WebViewClient getWebViewClient() {
        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 载入webview
                return true;
            }
        };
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, String[] imageUrls) {
        // ImagePreviewActivity.showImagePrivew(context, 0, imageUrls);
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, int index,
            String[] imageUrls) {
        // ImagePreviewActivity.showImagePrivew(context, index, imageUrls);
    }

    /**
     * 监听webview上的图片
     */
    public interface OnWebViewImageListener {
        /**
         * 点击webview上的图片，传入该缩略图的大图Url
         */
        void showImagePreview(String bigImageUrl);
    }

    public static void toBrowser(Context cxt, String url) {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        if (url.indexOf("oschina") > 0) {
            Bundle bundle = new Bundle();
            bundle.putString(OSCBlogDetailFragment.DATA_URL_KEY, url);
            SimpleBackActivity.postShowWith(cxt,
                    SimpleBackPage.OSC_BLOG_DETAIL, bundle);
        } else if (url.indexOf("blog.kymjs.com") > 0) {
            Intent intent = new Intent(cxt, MyBlogBrowser.class);
            intent.putExtra(MyBlogBrowser.BROWSER_KEY, url);
            intent.putExtra(MyBlogBrowser.BROWSER_TITLE_KEY, "博客详情");
            cxt.startActivity(intent);
        } else if (url.indexOf("www.kymjs.com") > 0) {
            Intent intent = new Intent(cxt, MyBlogBrowser.class);
            intent.putExtra(MyBlogBrowser.BROWSER_TITLE_KEY, "开源实验室");
            intent.putExtra(MyBlogBrowser.BROWSER_KEY, url);
            cxt.startActivity(intent);
        } else {
            Intent intent = new Intent(cxt, Browser.class);
            intent.putExtra(MyBlogBrowser.BROWSER_KEY, url);
            cxt.startActivity(intent);
        }
    }

    public static void toGallery(Context cxt, String url) {
        if (!StringUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.putExtra(ImageActivity.URL_KEY, url);
            intent.setClass(cxt, ImageActivity.class);
            cxt.startActivity(intent);
        }
    }

    public static void saveUser(Context cxt, User u) {
        KJDB kjdb = KJDB.create(cxt);
        kjdb.deleteByWhere(User.class, "");
        user = u;
        kjdb.save(u);
    }

    public static User getUser(Context cxt) {
        if (user != null) {
            return user;
        }
        KJDB kjdb = KJDB.create(cxt);
        List<User> datas = kjdb.findAll(User.class);

        if (datas != null && datas.size() > 0) {
            user = datas.get(0);
        } else {
            user = new User();
            user.setUid(2332925);
            user.setPortrait("http://www.kymjs.com/image/default_head.png");
            user.setName("爱看博客用户");
            user.setPwd("");
            user.setAccount("");
            user.setCookie("oscid=8N57Os9FG%2F%2B%2FFIA9vyogCJYPf0yMQGHmZhyzKMyuza2hL%2BW4xL7DPVVS%2B1BREZZzJGVMZrm4jNnkRHJmiDzNhjZIjp4pKbDtS4hUVFfAysLMq%2Fy5vIojQA%3D%3D;JSESSIONID=9B7tJ9RSZ4YYbdRhvg2xcTQ7skNJBwK3tMzdttnZwJpqmtx1d6hn!-25520330;");
            kjdb.save(user);
        }
        return user;
    }
}
