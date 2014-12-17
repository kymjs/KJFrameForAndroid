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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.I_KJActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

/**
 * 插件Activity的托管所，运行于APP程序中，将负责管理插件Activity中的全部事务<br>
 * 
 * <b>描述：</b>对于APP来说，插件应用的所有Activity都是CJProxy，
 * 只不过每个Activity在启动时传递的CJConfig.KEY_EXTRA_CLASS不同。<br>
 * <b>创建时间：</b> 2014-10-11 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJProxyActivity extends KJActivity {

    private int mAtyIndex; // 插件Activity在插件Manifest.xml中的序列（可选）
    private String mClass; // 插件Activity的完整类名（可选）
    private String mDexPath; // 插件所在绝对路径（必传）

    private Theme mTheme; // 托管插件的样式
    private Resources mResources; // 托管插件的资源
    private AssetManager mAssetManager; // 托管插件的assets

    protected I_CJActivity mPluginAty; // 插件Activity对象
    protected I_KJActivity mPluginKJAty; // 插件Activity对象的另一种表现形式

    /** not use */
    @Override
    public void setRootView() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent fromAppIntent = getIntent();
        mClass = fromAppIntent.getStringExtra(CJConfig.KEY_EXTRA_CLASS);
        mAtyIndex = fromAppIntent.getIntExtra(CJConfig.KEY_ATY_INDEX, 0);
        mDexPath = fromAppIntent.getStringExtra(CJConfig.KEY_DEX_PATH);
        initResources();
        if (mClass == null) {
            launchPluginActivity();
        } else {
            // 若已经指定要启动的插件Activity完整类名，则直接调用
            launchPluginActivity(mClass);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * 通过反射，获取到插件的资源访问器
     */
    protected void initResources() {
        // 如果是独立运行插件程序，mDexPath会有一个默认值
        if (CJConfig.DEF_STR.equals(mDexPath)) {
            defResources();
        } else {
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod(
                        "addAssetPath", String.class);
                addAssetPath.invoke(assetManager, mDexPath);
                mAssetManager = assetManager;
                Resources superRes = super.getResources();
                mResources = new Resources(mAssetManager,
                        superRes.getDisplayMetrics(),
                        superRes.getConfiguration());
                mTheme = mResources.newTheme();
                mTheme.setTo(super.getTheme());
            } catch (Exception e) {
                // 必须保证资源系统的正常
                defResources();
            }
        }
    }

    /**
     * 默认的Res
     */
    private void defResources() {
        mResources = this.getResources();
        mAssetManager = this.getAssets();
        mTheme = this.getTheme();
    }

    /**
     * 启动插件的Activity
     */
    protected void launchPluginActivity() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = CJTool.getAppInfo(this, mDexPath);
        } catch (NameNotFoundException e) {
            try {
                throw new NameNotFoundException(mDexPath + " not found");
            } catch (Exception e1) {
            }
        }
        if ((packageInfo.activities != null)
                && (packageInfo.activities.length > 0)) {
            mClass = packageInfo.activities[mAtyIndex].name;
            launchPluginActivity(mClass);
        }
    }

    /**
     * 启动指定的Activity
     * 
     * @param className
     *            要启动的Activity完整类名
     */
    protected void launchPluginActivity(final String className) {
        Class<?> atyClass;
        Constructor<?> atyConstructor = null;
        Object instance = null;
        try {
            if (CJConfig.DEF_STR.equals(mDexPath)) {
                atyClass = super.getClassLoader().loadClass(className);
            } else {
                atyClass = this.getClassLoader().loadClass(className);
            }
            atyConstructor = atyClass.getConstructor(new Class[] {});
            instance = atyConstructor.newInstance(new Object[] {});
        } catch (Exception e) {
            e.getStackTrace();
        }
        setRemoteActivity(instance);
        mPluginAty.setProxy(this, mDexPath);
        initAnnotate(instance); // 初始化插件界面的RootView和注解
        Bundle bundle = new Bundle();
        bundle.putInt(CJConfig.FROM, CJConfig.FROM_PROXY_APP);
        mPluginAty.onCreate(bundle);
    }

    /**
     * 初始化插件界面的RootView和注解
     * 
     * @param currentClass
     *            插件Activity对象
     */
    private void initAnnotate(Object currentClass) {
        if (mPluginKJAty != null) {
            mPluginKJAty.setRootView();
            View rootView = this.getWindow().getDecorView();
            // 通过反射获取到全部属性，反射的字段可能是一个类（静态）字段或实例字段
            Field[] fields = currentClass.getClass().getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    // 返回BindView类型的注解内容
                    BindView bindView = field.getAnnotation(BindView.class);
                    if (bindView != null) {
                        int viewId = bindView.id();
                        boolean clickLis = bindView.click();
                        try {
                            field.setAccessible(true);
                            if (clickLis) {
                                rootView.findViewById(viewId)
                                        .setOnClickListener(
                                                (OnClickListener) currentClass);
                            }
                            // 将currentClass的field赋值为sourceView.findViewById(viewId)
                            field.set(currentClass,
                                    rootView.findViewById(viewId));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置当前要显示的插件Activity
     */
    protected void setRemoteActivity(Object activity) {
        if (activity instanceof I_KJActivity) {
            mPluginKJAty = (I_KJActivity) activity;
        }
        if (activity instanceof I_CJActivity) {
            mPluginAty = (I_CJActivity) activity;
        } else {
            throw new ClassCastException(
                    "plugin activity must implements I_CJActivity");
        }
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }

    @Override
    public ClassLoader getClassLoader() {
        return CJClassLoader.getClassLoader(mDexPath, getApplicationContext(),
                super.getClassLoader());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPluginAty.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        mPluginAty.onStart();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        mPluginAty.onRestart();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        mPluginAty.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mPluginAty.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mPluginAty.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mPluginAty.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPluginAty.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mPluginAty.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mPluginAty.onNewIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        mPluginAty.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return mPluginAty.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        return mPluginAty.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowAttributesChanged(LayoutParams params) {
        mPluginAty.onWindowAttributesChanged(params);
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mPluginAty.onWindowFocusChanged(hasFocus);
        super.onWindowFocusChanged(hasFocus);
    }
}