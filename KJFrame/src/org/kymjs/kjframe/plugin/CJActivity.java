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
package org.kymjs.kjframe.plugin;

import org.kymjs.kjframe.ui.I_BroadcastReg;
import org.kymjs.kjframe.ui.I_KJActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

/**
 * 插件Activity的基类，若要使用CJFrame，必须继承本基类<br>
 * 
 * <b>注意</b> 在CJActivity以及子类中，绝对不可以使用this调用，应该使用that调用<br>
 * <b>创建时间</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public abstract class CJActivity extends Activity implements OnClickListener,
        I_CJActivity, I_KJActivity, I_BroadcastReg {

    public static final int WHICH_MSG = 0X37212;
    /**
     * that指针指向的是当前插件的Context（由于是插件化开发，this指针绝对不能使用）
     */
    public Activity that; // 替代this指针
    protected String mDexPath = CJConfig.DEF_STR;
    protected int mFrom = CJConfig.FROM_PLUGIN;

    /**
     * 设置托管Activity，并将that指针指向那个托管的Activity
     */
    @Override
    public void setProxy(Activity proxyActivity, String dexPath) {
        that = proxyActivity;
        mDexPath = dexPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFrom = savedInstanceState.getInt(CJConfig.FROM,
                    CJConfig.FROM_PLUGIN);
        }
        // 如果不是来自托管类，则将that指针指向自身
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onCreate(savedInstanceState);
            that = this;
            this.setRootView();
        }
        initializer();
        registerBroadcast();
    }

    @Override
    public void setContentView(View view) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.setContentView(view);
        } else {
            that.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.setContentView(view, params);
        } else {
            that.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.setContentView(layoutResID);
        } else {
            that.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.addContentView(view, params);
        } else {
            that.addContentView(view, params);
        }
    }

    @Override
    public View findViewById(int id) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.findViewById(id);
        } else {
            return that.findViewById(id);
        }
    }

    @Override
    public Intent getIntent() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getIntent();
        } else {
            return that.getIntent();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getClassLoader();
        } else {
            return that.getClassLoader();
        }
    }

    @Override
    public Resources getResources() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getResources();
        } else {
            return that.getResources();
        }
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getLayoutInflater();
        } else {
            return that.getLayoutInflater();
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getSharedPreferences(name, mode);
        } else {
            return that.getSharedPreferences(name, mode);
        }
    }

    @Override
    public Context getApplicationContext() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getApplicationContext();
        } else {
            return that.getApplicationContext();
        }
    }

    @Override
    public WindowManager getWindowManager() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getWindowManager();
        } else {
            return that.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getWindow();
        } else {
            return that.getWindow();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.getSystemService(name);
        } else {
            return that.getSystemService(name);
        }
    }

    @Override
    public void finish() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.finish();
        } else {
            that.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onStart();
        }
    }

    @Override
    public void onRestart() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onRestart();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void onResume() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onPause();
        }
    }

    @Override
    public void onStop() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onDestroy();
        }
        unRegisterBroadcast();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.onKeyUp(keyCode, event);
        } else {
            return false;
        }
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onWindowAttributesChanged(params);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    /*********************** extend KJFrame ******************************/
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
     * initialization data. And this method run in background thread, so you
     * shouldn't change ui<br>
     * on initializated, will call threadDataInited();
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

    /**
     * 如果调用了initDataFromThread()，则当数据初始化完成后将回调该方法。
     */
    protected void threadDataInited() {}

    /** initialization data */
    @Override
    public void initData() {}

    /** initialization widget */
    @Override
    public void initWidget() {}

    /** 初始化方法 */
    public void initializer() {
        initData();
        initWidget();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在线程中执行初始化数据
                initDataFromThread();
                // 初始化完成发送一条message
                threadHandle.sendEmptyMessage(WHICH_MSG);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        widgetClick(v);
    }

    /** 点击事件回调方法 */
    @Override
    public void widgetClick(View v) {}

    /** 设置root界面 */
    @Override
    public void setRootView() {}

    @Override
    public void registerBroadcast() {}

    @Override
    public void unRegisterBroadcast() {}
}