package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.activity.CJActivity;
import org.kymjs.aframe.plugin.service.CJServiceUtils;
import org.kymjs.aframe.ui.BindView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * 作为插件Activity必须继承CJActivity
 */
public class ServiceActivity extends CJActivity {

    @BindView(id = R.id.aty_service_btn, click = true)
    private Button mBtnService;
    @BindView(id = R.id.aty_service_bind, click = true)
    private Button mBtnBind;

    private TestBindService serviceTwo;

    /**
     * 如果使用了注解，必须将setContentView方法写在本方法内
     */
    @Override
    public void setRootView() {
        super.setRootView();
        that.setContentView(R.layout.aty_service);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnBind.setText("bind启动Service");
        mBtnService.setText("start启动Service");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.aty_service_bind:
            // 这里的Intent必须是从CJServiceUtils中获取的
            Intent serviceIntent = CJServiceUtils.getPluginIntent(
                    that, MainActivity.pluginPath,
                    TestBindService.class);
            that.bindService(serviceIntent, serviceConnection,
                    BIND_AUTO_CREATE);
            break;
        case R.id.aty_service_btn:
            // 这里的Intent必须是从CJServiceUtils中获取的
            that.startService(CJServiceUtils.getPluginIntent(that,
                    MainActivity.pluginPath, TestService.class));
            break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        that.unbindService(serviceConnection);
    }

    // 绑定服务 链接
    private ServiceConnection serviceConnection = new ServiceConnection() {
        // 绑定服务
        public void onServiceConnected(ComponentName name,
                IBinder service) {
            // 用于获取服务返回的数据信息 -- 此处获取ServiceTwo的对象
            serviceTwo = ((TestBindService.ServiceHolder) service)
                    .create();
            serviceTwo.show();
        }

        // 解绑服务
        public void onServiceDisconnected(ComponentName name) {
            Log.i("debug", "--" + name);
        }
    };
}
