package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.service.CJService;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 作为插件Service必须继承CJService
 */
public class TestService extends CJService {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("debug", "start Service success!");
        Toast.makeText(that, "start服务启动成功", Toast.LENGTH_SHORT)
                .show();
        that.stopSelf();
        return 0;
    }
}
