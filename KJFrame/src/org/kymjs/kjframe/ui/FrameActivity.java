/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
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
package org.kymjs.kjframe.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Activity's framework,the developer shouldn't extends it<br>
 * 
 * <b>创建时间</b> 2014-3-1 <br>
 * <b>最后修改时间</b> 2014-10-17<br>
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.8
 */
public abstract class FrameActivity extends FragmentActivity implements
        OnClickListener, I_BroadcastReg, I_KJActivity, I_SkipActivity {

    public static final int WHICH_MSG = 0X37210;

    /**
     * 一个私有回调类，线程中初始化数据完成后的回调
     */
    private interface ThreadDataCallBack {
        void onSuccess();
    }

    private static ThreadDataCallBack callback;

    // 当线程中初始化的数据初始化完成后，调用回调方法
    private static Handler threadHandle = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == WHICH_MSG) {
                callback.onSuccess();
            }
        };
    };

    /**
     * 如果调用了initDataFromThread()，则当数据初始化完成后将回调该方法。
     */
    protected void threadDataInited() {}

    /**
     * 在线程中初始化数据，注意不能在这里执行UI操作
     */
    @Override
    public void initDataFromThread() {
        callback = new ThreadDataCallBack() {
            @Override
            public void onSuccess() {
                threadDataInited();
            }
        };
    }

    @Override
    public void initData() {}

    @Override
    public void initWidget() {}

    // 仅仅是为了代码整洁点
    private void initializer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initDataFromThread();
                threadHandle.sendEmptyMessage(WHICH_MSG);
            }
        }).start();
        initData();
        initWidget();
    }

    /** listened widget's click method */
    @Override
    public void widgetClick(View v) {}

    @Override
    public void onClick(View v) {
        widgetClick(v);
    }

    @Override
    public void registerBroadcast() {}

    @Override
    public void unRegisterBroadcast() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRootView(); // 必须放在annotate之前调用
        AnnotateUtil.initBindView(this);
        initializer();
        registerBroadcast();
    }

    @Override
    protected void onDestroy() {
        unRegisterBroadcast();
        super.onDestroy();
    }

    /**
     * 用Fragment替换视图
     * 
     * @param resView
     *            将要被替换掉的视图
     * @param targetFragment
     *            用来替换的Fragment
     */
    public void changeFragment(int resView, KJFragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(resView, targetFragment, targetFragment.getClass()
                .getName());
        transaction.commit();
    }

    /**
     * 用Fragment替换视图
     * 
     * @param resView
     *            将要被替换掉的视图
     * @param targetFragment
     *            用来替换的Fragment
     */
    public void changeFragment(int resView, SupportFragment targetFragment) {
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(resView, targetFragment, targetFragment.getClass()
                .getName());
        transaction.commit();
    }
}
