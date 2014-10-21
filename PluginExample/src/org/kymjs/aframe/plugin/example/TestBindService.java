package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.service.CJService;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 作为插件Service必须继承CJService
 */
public class TestBindService extends CJService {

    public class ServiceHolder extends Binder {
        public TestBindService create() {
            return TestBindService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return new ServiceHolder();
    }

    public void show() {
        Log.i("debug", "bind Service success!");
        Toast.makeText(that, "bind服务启动成功", Toast.LENGTH_SHORT).show();
    }
}
