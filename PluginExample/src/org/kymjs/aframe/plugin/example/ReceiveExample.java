package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.activity.CJActivity;
import org.kymjs.aframe.ui.BindView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ReceiveExample extends CJActivity {
    @BindView(id = R.id.send, click = true)
    private Button send;

    final String action = "org.kymjs.aframe.plugin.example.receiverexample";

    /**
     * 注意：1、setContentView必须放在setRootView()方法中调用<br>
     * 2、最好不要用onCreate方法，在KJFrameForAndroid中已经定义了完善的初始化回调方法
     */
    @Override
    public void setRootView() {
        super.setRootView();
        that.setContentView(R.layout.aty_receiver);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        send.setText("发送广播");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.send:
            that.sendBroadcast(new Intent(action));
            break;
        }
    }

    /**
     * 推荐把广播的注册与取消注册写在这两个方法内（框架会自动回调这两个方法）
     */
    @Override
    public void registerBroadcast() {
        super.registerBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        that.registerReceiver(receiver, filter);
    }

    /**
     * 推荐把广播的注册与取消注册写在这两个方法内（框架会自动回调这两个方法）
     */
    public void unRegisterBroadcast() {
        that.unregisterReceiver(receiver);
    };

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (action.equals(intent.getAction())) {
                Toast.makeText(that, "捕获到广播", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };
}
