package org.kymjs.kjframe.demo;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.demo.widget.KJListViewDemo;
import org.kymjs.kjframe.demo.widget.KJSlidingMenuDemo;
import org.kymjs.kjframe.demo.widget.ScaleImageDemo;
import org.kymjs.kjframe.ui.BindView;

import android.view.View;
import android.widget.Button;

public class WidgetActivity extends KJActivity {
    @BindView(id = R.id.button1, click = true)
    private Button mBtn1;
    @BindView(id = R.id.button2, click = true)
    private Button mBtn2;
    @BindView(id = R.id.button3, click = true)
    private Button mBtn3;
    @BindView(id = R.id.button4, click = true)
    private Button mBtn4;
    @BindView(id = R.id.button5, click = true)
    private Button mBtn5;

    @Override
    public void setRootView() {
        setContentView(R.layout.widget);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBtn1.setText("侧滑SlidingMenu");
        mBtn2.setText("KJListDemo和RoundImageView");
        mBtn3.setText("KJScrollView不支持上下拉刷新");
        mBtn4.setText("KJViewPager请访问：https://github.com/kymjs/KJController");
        mBtn5.setText("缩放旋转ImageView");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            showActivity(aty, KJSlidingMenuDemo.class);
            break;
        case R.id.button2:
            showActivity(aty, KJListViewDemo.class);
            break;
        case R.id.button3:
            break;
        case R.id.button4:
            break;
        case R.id.button5:
            showActivity(aty, ScaleImageDemo.class);
            break;
        }
    }
}
