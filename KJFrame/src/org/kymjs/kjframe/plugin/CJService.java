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

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * 插件Service的基类，若要使用CJFrame，必须继承本基类<br>
 * 
 * <b>注意</b> 在CJService以及子类中，绝对不可以使用this调用，应该使用that调用<br>
 * <b>创建时间</b> 2014-10-15 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public abstract class CJService extends Service implements I_CJService {

    protected String mDexPath = CJConfig.DEF_STR;
    protected int mFrom = CJConfig.FROM_PROXY_APP;

    /**
     * that指针指向的是当前插件的Context（由于是插件化开发，this指针绝对不能使用）
     */
    protected Service that; // 替代this指针

    @Override
    public void setProxy(Service proxyService, String dexPath) {
        this.that = proxyService;
        this.mDexPath = dexPath;
        if (CJConfig.DEF_STR.equals(dexPath)) {
            mFrom = CJConfig.FROM_PROXY_APP;
        } else {
            mFrom = CJConfig.FROM_PLUGIN;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return null;
        } else {
            return that.onBind(intent);
        }
    }

    @Override
    public void onCreate() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onCreate();
        } else {
            that.onCreate();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.onStartCommand(intent, flags, startId);
        } else {
            return that.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onDestroy();
        } else {
            that.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onConfigurationChanged(newConfig);
        } else {
            that.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onLowMemory();
        } else {
            that.onLowMemory();
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onTrimMemory(int level) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onTrimMemory(level);
        } else {
            that.onTrimMemory(level);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            return super.onUnbind(intent);
        } else {
            return that.onUnbind(intent);
        }
    }

    @Override
    public void onRebind(Intent intent) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onRebind(intent);
        } else {
            that.onRebind(intent);
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onTaskRemoved(Intent rootIntent) {
        if (mFrom == CJConfig.FROM_PLUGIN) {
            super.onTaskRemoved(rootIntent);
        } else {
            that.onTaskRemoved(rootIntent);
        }
    }
}
