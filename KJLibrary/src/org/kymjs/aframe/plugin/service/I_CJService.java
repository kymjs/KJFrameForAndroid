package org.kymjs.aframe.plugin.service;

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
