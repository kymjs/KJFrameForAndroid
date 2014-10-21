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
package org.kymjs.aframe.ui;

import org.kymjs.aframe.ui.activity.I_KJActivity;
import org.kymjs.aframe.ui.activity.I_SkipActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * update log:
 * @1.5 abstract protocol: I_KJActivity
 * @1.6 add method initThreadData()
 * @1.7 add abstract protocol:I_SkipActivity
 * @1.8 add thread data callback
 */

/**
 * Activity's framework,the developer shouldn't extends it<br>
 * 
 * <b>创建时间</b> 2014-3-1 <br>
 * <b>最后修改时间</b> 2014-10-17<br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.8
 */
public abstract class KJFrameActivity extends Activity implements
        OnClickListener, I_BroadcastReg, I_KJActivity, I_SkipActivity {

    private interface ThreadDataCallBack {
        void onSuccess();
    }

    // 当线程中初始化的数据初始化完成后，调用回调方法
    private static Handler threadHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x37213722) {
                callback.onSuccess();
            }
        };
    };

    private static ThreadDataCallBack callback;

    /**
     * initialization data. And this method run in background thread, so you
     * shouldn't change ui<br>
     * on initializated, will call threadDataInited();
     */
    protected void initDataFromThread() {
        callback = new ThreadDataCallBack() {
            @Override
            public void onSuccess() {
                threadDataInited();
            }
        };
    }

    /**
     * 如果调用了initDataFromThread()，则当数据初始化完成后将回调该方法。
     */
    protected void threadDataInited() {}

    /** initialization data */
    protected void initData() {}

    /** initialization widget */
    protected void initWidget() {}

    /** initialization */
    @Override
    public void initialize() {
        initData();
        initWidget();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在线程中执行初始化数据
                initDataFromThread();
                // 初始化完成发送一条message
                threadHandle.sendEmptyMessage(0x37213722);
            }
        }).start();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRootView(); // 必须放在annotate之前调用
        AnnotateUtil.initBindView(this); // 必须放在initialization之前调用
        initialize();
        registerBroadcast();
    }

    @Override
    public void unRegisterBroadcast() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }
}
