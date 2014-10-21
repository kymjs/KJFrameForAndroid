package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.CJConfig;
import org.kymjs.aframe.plugin.activity.CJActivity;
import org.kymjs.aframe.plugin.activity.CJActivityUtils;
import org.kymjs.aframe.ui.BindView;

import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class MainActivity extends CJActivity {
    @BindView(id = R.id.button1, click = true)
    private Button button1;
    @BindView(id = R.id.button2, click = true)
    private Button button2;
    @BindView(id = R.id.button3, click = true)
    private Button button3;

    // 当做为APP单独运行时使用CJConfig.DEF_STR
    public static String pluginPath = CJConfig.DEF_STR;

    /**
     * 注意：1、setContentView必须放在setRootView()方法中调用<br>
     * 2、最好不要用onCreate方法，在KJFrameForAndroid中已经定义了完善的初始化回调方法
     */
    @Override
    public void setRootView() {
        super.setRootView();
        that.setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        super.initData();
        // 如果你的插件中使用了Activity跳转或启动Service，必须指定插件apk在用户SD卡中的位置
        pluginPath = Environment.getExternalStorageDirectory()
                + "/PluginExample.apk";
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            // 跳转界面的时候一定要使用CJActivityUtils跳转，
            // 或通过CJActivityUtils获取到一个intent，使用这个intent跳转
            CJActivityUtils.launchPlugin(that, pluginPath,
                    FragmentAty.class);
            break;
        case R.id.button2:
            CJActivityUtils.launchPlugin(that, pluginPath,
                    ServiceActivity.class);
            break;
        case R.id.button3:
            CJActivityUtils.launchPlugin(that, pluginPath,
                    ReceiveExample.class);
            break;
        }
    }
}
