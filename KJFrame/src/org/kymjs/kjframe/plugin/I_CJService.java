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

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * CJFrameService接口协议，插件Service必须实现此接口<br>
 * Service实现此接口意味着将插件的Service生命周期交給CJFrame托管， 而不再是SDK托管<br>
 * 
 * <b>创建时间</b> 2014-10-15 <br>
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public interface I_CJService {
    IBinder onBind(Intent intent);

    void onCreate();

    int onStartCommand(Intent intent, int flags, int startId);

    void onDestroy();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    boolean onUnbind(Intent intent);

    void onRebind(Intent intent);

    void onTaskRemoved(Intent rootIntent);

    /**
     * 设置托管Service，并将that指针指向那个托管的Service
     * 
     * @param proxyService
     * @param dexPath
     */
    void setProxy(Service proxyService, String dexPath);
}
