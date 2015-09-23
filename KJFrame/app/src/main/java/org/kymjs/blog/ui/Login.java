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
package org.kymjs.blog.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.LoginData;
import org.kymjs.blog.domain.User;
import org.kymjs.blog.utils.KJAnimations;
import org.kymjs.blog.utils.Parser;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.widget.RoundImageView;

/**
 * 新用户登陆界面
 *
 * @author kymjs (http://www.kymjs.com/)
 * @点击登陆 传递账号、密码、IMEI；
 * @第三方登陆 传递ID, token，IMEI，若返回201，表示未登陆过，跳转到注册界面
 * @注册 跳转到注册页面
 */
public class Login extends KJActivity {

    // wdiget
    @BindView(id = R.id.login_img_logo)
    private ImageView mImgLogo;
    @BindView(id = R.id.login_layout_input)
    private RelativeLayout mLayoutInput;
    @BindView(id = R.id.login_et_uid)
    private EditText mEtUid;
    @BindView(id = R.id.login_img_delete, click = true)
    private ImageView mImgDel;
    @BindView(id = R.id.login_et_pwd)
    private EditText mEtPwd;
    @BindView(id = R.id.login_btn_login, click = true)
    private Button mBtnLogin;
    @BindView(id = R.id.login_img_head)
    private RoundImageView mImgHead;

    // 实在没有精力做第三方了
    // @BindView(id = R.id.login_img_baidu)
    // private ImageView mImgBaiDu;
    // @BindView(id = R.id.login_img_qq)
    // private ImageView mImgQQ;
    // @BindView(id = R.id.login_img_sina)
    // private ImageView mImgSina;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_login);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        initEtUser();
        screenAdapter();
        // 设置动画
        KJAnimations.openLoginAnim(mLayoutInput);
        mImgHead.setAnimation(KJAnimations.getRotateAnimation(360, 0, 500));
        mImgHead.setBorderOutsideColor(0xffffffff);
        mImgHead.setBorderThickness(2);
    }

    /**
     * 初始化用户名输入框
     */
    private void initEtUser() {
        if (StringUtils.isEmpty(mEtUid.getText())) {
            mImgDel.setVisibility(View.GONE);
        } else {
            mImgDel.setVisibility(View.VISIBLE);
        }
        mEtUid.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 屏幕适配
     */
    private void screenAdapter() {
        // 输入框适配
        RelativeLayout.LayoutParams inputParams = (RelativeLayout.LayoutParams) mLayoutInput
                .getLayoutParams();
        inputParams.width = (int) (DensityUtils.getScreenW(aty) * 0.8);
        mLayoutInput.setLayoutParams(inputParams);
        // 高度计算
        mLayoutInput.measure(0, 0);
        int layoutH = mLayoutInput.getMeasuredHeight();
        mImgHead.measure(0, 0);
        int headH = (int) getResources().getDimension(
                R.dimen.splash_head_height);
        // 头像适配
        RelativeLayout.LayoutParams headParams = (RelativeLayout.LayoutParams) mImgHead
                .getLayoutParams();
        headParams.topMargin = (DensityUtils.getScreenH(aty) - layoutH - headH) / 2;
        mImgHead.setLayoutParams(headParams);
        // logo适配
        RelativeLayout.LayoutParams logoParams = (RelativeLayout.LayoutParams) mImgLogo
                .getLayoutParams();
        logoParams.width = (int) (DensityUtils.getScreenW(aty) * 0.4);
        logoParams.height = (int) (logoParams.width / 2.3);
        logoParams.topMargin = (DensityUtils.getScreenH(aty) - layoutH - headH - logoParams
                .height) / 4;
        mImgLogo.setLayoutParams(logoParams);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
            case R.id.login_btn_login:
                doLogin();
                break;
            case R.id.login_img_delete:
                mEtUid.setText(null);
                break;
        }
    }

    /**
     * 输入合法性检测
     */
    private boolean inputCheck() {
        if (StringUtils.isEmpty(mEtUid.getText().toString())) {
            ViewInject.toast(getString(R.string.account_not_empty));
            return false;
        }
        if (StringUtils.isEmpty(mEtPwd.getText().toString())) {
            ViewInject.toast(getString(R.string.password_not_empty));
            return false;
        }
        return true;
    }

    private void doLogin() {
        if (!inputCheck()) {
            return;
        }
        HttpConfig config = new HttpConfig();
        config.cacheTime = 0;
        KJHttp kjh = new KJHttp(config);
        HttpParams params = new HttpParams();
        params.put("username", mEtUid.getText().toString());
        params.put("pwd", mEtPwd.getText().toString());
        kjh.post("http://www.oschina.net/action/api/login_validate", params,
                new HttpCallBack() {
                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        ViewInject.toast("网络不好" + strMsg);
                    }

                    @Override
                    public void onSuccess(
                            java.util.Map<String, String> headers, byte[] t) {
                        String cookie = headers.get("Set-Cookie");
                        if (t != null) {
                            String str = new String(t);
                            KJLoger.debug("登陆网络请求：" + new String(t));
                            LoginData data = Parser.xmlToBean(LoginData.class,
                                    str);
                            try {
                                if (1 == data.getResult().getErrorCode()) {
                                    User user = data.getUser();
                                    user.setCookie(cookie);
                                    user.setAccount(mEtUid.getText().toString());
                                    user.setPwd(mEtPwd.getText().toString());
                                    UIHelper.saveUser(aty, user);
                                    finish();
                                } else {
                                    mEtPwd.setText(null);
                                    mEtUid.setText(null);
                                }
                                ViewInject.toast(data.getResult()
                                        .getErrorMessage());
                                // 太多判断了，写的蛋疼，还不如一个NullPointerException
                            } catch (NullPointerException e) {
                                ViewInject.toast("登陆失败");
                                mEtPwd.setText(null);
                                mEtUid.setText(null);
                            }
                        }
                    }

                    ;
                });
    }
}
