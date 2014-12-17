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

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * 插件Service的托管所，将负责管理插件Service中的全部事务<br>
 * 
 * <b>描述</b>对于APP来说，插件应用的所有Service都是CJProxy，
 * 只不过每个Service在启动时传递的CJConfig.KEY_EXTRA_CLASS不同。<br>
 * <b>创建时间</b> 2014-10-15 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class CJProxyService extends Service {

    private String mClass; // 插件Service的完整类名（必传）
    private String mDexPath = CJConfig.DEF_STR; // 插件所在绝对路径（可选）

    protected I_CJService mPluginService; // 插件Service对象

    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return mPluginService.onBind(intent);
    }

    private void init(Intent itFromApp) {
        mClass = itFromApp.getStringExtra(CJConfig.KEY_EXTRA_CLASS);
        mDexPath = itFromApp.getStringExtra(CJConfig.KEY_DEX_PATH);
        Object instance = null;
        try {
            Class<?> serviceClass;
            if (CJConfig.DEF_STR.equals(mDexPath)) {
                serviceClass = super.getClassLoader().loadClass(mClass);
            } else {
                serviceClass = this.getClassLoader().loadClass(mClass);
            }
            Constructor<?> serviceConstructor = serviceClass
                    .getConstructor(new Class[] {});
            instance = serviceConstructor.newInstance(new Object[] {});
        } catch (Exception e) {
        }
        setRemoteService(instance);
        mPluginService.setProxy(this, mDexPath);
    }

    /**
     * 保留一份插件Service对象
     */
    protected void setRemoteService(Object service) {
        if (service instanceof I_CJService) {
            mPluginService = (I_CJService) service;
        } else {
            throw new ClassCastException(
                    "plugin service must implements I_CJService");
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return CJClassLoader.getClassLoader(mDexPath, getApplicationContext(),
                super.getClassLoader());
    }

    @Override
    public void onCreate() {
    } // 不能调用屏蔽super

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init(intent);// 必须先初始化，才能Create
        mPluginService.onCreate();
        mPluginService.onStartCommand(intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mPluginService.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mPluginService.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        mPluginService.onLowMemory();
        super.onLowMemory();
    }

    @Override
    @SuppressLint("NewApi")
    public void onTrimMemory(int level) {
        mPluginService.onTrimMemory(level);
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPluginService.onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        mPluginService.onRebind(intent);
        super.onRebind(intent);
    }

    @Override
    @SuppressLint("NewApi")
    public void onTaskRemoved(Intent rootIntent) {
        mPluginService.onTaskRemoved(rootIntent);
        super.onTaskRemoved(rootIntent);
    }
}
