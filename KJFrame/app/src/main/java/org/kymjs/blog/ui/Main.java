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

import org.kymjs.blog.R;
import org.kymjs.blog.ui.fragment.BlogFragment;
import org.kymjs.blog.ui.fragment.FindFragment;
import org.kymjs.blog.ui.fragment.MineFragment;
import org.kymjs.blog.ui.fragment.TitleBarFragment;
import org.kymjs.blog.utils.KJAnimations;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.KJActivityStack;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RadioButton;

/**
 * 应用主界面
 * 
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 */
public class Main extends TitleBarActivity {

    @BindView(id = R.id.bottombar_content1, click = true)
    private RadioButton mRbtnContent1;
    @BindView(id = R.id.bottombar_content2, click = true)
    private RadioButton mRbtnContent2;
    @BindView(id = R.id.bottombar_content3, click = true)
    private RadioButton mRbtnContent3;

    private TitleBarFragment contentFragment1;
    private TitleBarFragment contentFragment2;
    private TitleBarFragment contentFragment3;
    private TitleBarFragment currentFragment;

    private float titleBarHeight;
    private boolean isOnKeyBacking;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_main);
    }

    @Override
    public void initData() {
        super.initData();
        contentFragment1 = new BlogFragment();
        contentFragment2 = new FindFragment();
        contentFragment3 = new MineFragment();
        titleBarHeight = getResources().getDimension(R.dimen.titlebar_height);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        changeFragment(contentFragment1);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.bottombar_content1:
            changeFragment(contentFragment1);
            break;
        case R.id.bottombar_content2:
            changeFragment(contentFragment2);
            break;
        case R.id.bottombar_content3:
            changeFragment(contentFragment3);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        currentFragment.onBackClick();
    }

    @Override
    protected void onMenuClick() {
        super.onMenuClick();
        currentFragment.onMenuClick();
    }

    public void changeFragment(TitleBarFragment targetFragment) {
        currentFragment = targetFragment;
        super.changeFragment(R.id.main_content, targetFragment);
    }

    /********************** 再按一下退出 *****************************/

    /**
     * 取消退出
     */
    private void cancleExit() {
        Animation anim = KJAnimations.getTranslateAnimation(0, 0,
                titleBarHeight, 0, 300);
        mTvTitle.startAnimation(anim);
        Animation anim2 = KJAnimations.getTranslateAnimation(0, 0,
                titleBarHeight, 300, 0);
        anim2.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mTvDoubleClickTip.setVisibility(View.GONE);
            }
        });
        mTvDoubleClickTip.startAnimation(anim2);
    }

    /**
     * 显示退出提示
     */
    private void showExitTip() {
        mTvDoubleClickTip.setVisibility(View.VISIBLE);
        Animation anim = KJAnimations.getTranslateAnimation(0, 0, 0,
                titleBarHeight, 300);
        mTvTitle.startAnimation(anim);
        Animation anim2 = KJAnimations.getTranslateAnimation(0, 0,
                titleBarHeight, 0, 300);
        mTvDoubleClickTip.startAnimation(anim2);
    }

    private final Runnable onBackTimeRunnable = new Runnable() {
        @Override
        public void run() {
            isOnKeyBacking = false;
            cancleExit();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isOnKeyBacking) {
                mMainLoopHandler.removeCallbacks(onBackTimeRunnable);
                isOnKeyBacking = false;
                // UIHelper.toHome(aty);
                KJActivityStack.create().appExit(aty);
            } else {
                isOnKeyBacking = true;
                showExitTip();
                mMainLoopHandler.postDelayed(onBackTimeRunnable, 2000);
            }
            return true;
            // } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    };
}
