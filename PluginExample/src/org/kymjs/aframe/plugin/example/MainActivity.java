package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.activity.CJActivity;
import org.kymjs.aframe.ui.BindView;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends CJActivity {

    @BindView(id = R.id.button1, click = true)
    private Button button1;

    @Override
    public void setRootView() {
        super.setRootView();
        that.setContentView(R.layout.activity_main);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            Toast.makeText(that, "点击了插件按钮", Toast.LENGTH_SHORT)
                    .show();
            break;
        }
    }
}
