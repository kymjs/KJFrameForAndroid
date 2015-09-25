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

import org.kymjs.blog.AppConfig;
import org.kymjs.blog.AppContext;
import org.kymjs.blog.R;
import org.kymjs.blog.utils.KJAnimations;
import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.widget.RoundImageView;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 应用欢迎界面(动态适配模式)
 * 
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 */
public class Splash extends KJActivity {
    public static final String TAG = "splash";

    @BindView(id = R.id.splash_layout_root)
    private RelativeLayout mRlRoot;
    @BindView(id = R.id.splash_box)
    private RelativeLayout mRlBox;
    @BindView(id = R.id.splash_tv_content)
    private TextView mTvContent;
    @BindView(id = R.id.splash_img_head)
    private RoundImageView mImgHead;
    @BindView(id = R.id.splash_btn_go, click = true)
    private Button mBtnGo;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_splash);
    }

    @Override
    public void initData() {
        super.initData();
        String cacheTime = PreferenceHelper.readString(aty, TAG,
                AppConfig.CACHE_TIME_KEY, "");
        if (!StringUtils.getDataTime("yyyymmdd").equalsIgnoreCase(cacheTime)) {
            PreferenceHelper.clean(aty, TAG);
        }
    }

    @Override
    public void initWidget() {
        super.initWidget();
        screenAdaptation();
        KJAnimations.openLoginAnim(mRlBox);
        mImgHead.setAnimation(KJAnimations.getRotateAnimation(360, 0, 600));
        setUserInterface();
    }

    /**
     * 屏幕适配
     */
    private void screenAdaptation() {
        RelativeLayout.LayoutParams boxParams = (LayoutParams) mRlBox
                .getLayoutParams();
        boxParams.width = (int) (AppContext.screenW * 0.8);
        boxParams.height = (int) (AppContext.screenH * 0.6);
        mRlBox.setLayoutParams(boxParams);

        RelativeLayout.LayoutParams goParams = (LayoutParams) mBtnGo
                .getLayoutParams();
        goParams.width = (int) (AppContext.screenW * 0.7);
        goParams.height = (int) getResources().getDimension(
                R.dimen.splash_btn_go_height);
        mBtnGo.setLayoutParams(goParams);

        RelativeLayout.LayoutParams headParams = (RelativeLayout.LayoutParams) mImgHead
                .getLayoutParams();
        headParams.topMargin = (int) ((AppContext.screenH * 0.16) / 2);
        mImgHead.setLayoutParams(headParams);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.splash_btn_go:
            startActivity(new Intent(aty, Main.class));
            finish();
            break;
        default:
            break;
        }
    }

    /**
     * 动态设置用户界面
     */
    private void setUserInterface() {
        String sdCardPath = FileUtils.getSavePath(AppConfig.saveFolder);
        String headImgPath = sdCardPath
                + PreferenceHelper.readString(aty, TAG,
                        AppConfig.SPLASH_HEAD_IMG_KEY, "");
        String rootBgPath = sdCardPath
                + PreferenceHelper.readString(aty, TAG,
                        AppConfig.SPLASH_BACKGROUND_KEY, "");
        String boxBgPath = sdCardPath
                + PreferenceHelper.readString(aty, TAG,
                        AppConfig.SPLASH_BOX_KEY, "");
        String contentStr = PreferenceHelper.readString(aty, TAG,
                AppConfig.SPLASH_CONTENT_KEY,
                getString(R.string.splash_content));

        KJBitmap kjb = new KJBitmap();
        kjb.display(mRlRoot, rootBgPath);
        kjb.display(mImgHead, headImgPath);
        kjb.display(mRlBox, boxBgPath);
        mTvContent.setText(contentStr);
    }
}
