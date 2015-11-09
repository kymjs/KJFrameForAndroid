package org.kymjs.kjframe.demo;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.KJActivityStack;
import org.kymjs.kjframe.utils.KJConfig;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends KJActivity {

    @BindView(id = R.id.textView1)
    private TextView mTvVersion;
    @BindView(id = R.id.button1, click = true)
    private Button mBtnUI;
    @BindView(id = R.id.button3, click = true)
    private Button mBtnBitmap;
    @BindView(id = R.id.button4, click = true)
    private Button mBtnHttp;
    @BindView(id = R.id.button5, click = true)
    private Button mBtnDB;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_main);
        if (!"org.kymjs.kjframe.demo".equals(getApplication().getPackageName())) {
            KJActivityStack.create().appExit(aty);
        }
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mTvVersion.setText("当前框架版本为:" + KJConfig.VERSION);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            showActivity(this, WidgetActivity.class);
            break;
        case R.id.button3:
            showActivity(this, BitmapActivity.class);
            break;
        case R.id.button4:
            showActivity(this, HttpActivity.class);
            break;
        case R.id.button5:
            showActivity(this, DBActivity.class);
            break;
        }
    }
}
