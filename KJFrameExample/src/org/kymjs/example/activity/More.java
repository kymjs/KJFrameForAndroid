package org.kymjs.example.activity;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.activity.BaseActivity;
import org.kymjs.example.R;
import org.kymjs.example.fragment.DBExample;
import org.kymjs.example.fragment.DownloadExample;
import org.kymjs.example.fragment.HorizontalListDemo;
import org.kymjs.example.fragment.HttpExample;
import org.kymjs.example.fragment.MainFragment;
import org.kymjs.example.fragment.PluginSkinExample;

import android.view.View;
import android.widget.Button;

public class More extends BaseActivity {
    @BindView(id = R.id.more1, click = true)
    private Button btn1;
    @BindView(id = R.id.more2, click = true)
    private Button btn2;
    @BindView(id = R.id.more3, click = true)
    private Button btn3;
    @BindView(id = R.id.more4, click = true)
    private Button btn4;
    @BindView(id = R.id.more5, click = true)
    private Button btn5;
    @BindView(id = R.id.more6, click = true)
    private Button btn6;
    @BindView(id = R.id.more7, click = true)
    private Button btn7;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_more_list);
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.more1:
            showActivity(aty, WebViewExample.class);
            break;
        case R.id.more2:
            ShowDemo.content = new DBExample();
            showActivity(aty, ShowDemo.class);
            break;
        case R.id.more3:
            ShowDemo.content = new HorizontalListDemo();
            showActivity(aty, ShowDemo.class);
            break;
        case R.id.more4:
            ShowDemo.content = new DownloadExample();
            showActivity(aty, ShowDemo.class);
            break;
        case R.id.more5:
            ShowDemo.content = new MainFragment();
            showActivity(aty, ShowDemo.class);
            break;
        case R.id.more6:
            ShowDemo.content = new HttpExample();
            showActivity(aty, ShowDemo.class);
            break;
        case R.id.more7:
            ShowDemo.content = new PluginSkinExample();
            showActivity(aty, ShowDemo.class);
            break;
        }
    }
}
