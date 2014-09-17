package org.kymjs.example.activity;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.activity.KJFragmentActivity;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;
import org.kymjs.example.fragment.DBExample;
import org.kymjs.example.fragment.DownloadExample;
import org.kymjs.example.fragment.HorizontalListDemo;

import android.app.ActionBar;
import android.view.View;
import android.widget.RadioButton;

public class TabExampleTwo extends KJFragmentActivity {

    @BindView(id = R.id.bottombar_content1, click = true)
    public RadioButton mRbtn1;
    @BindView(id = R.id.bottombar_content2, click = true)
    private RadioButton mRbtn2;
    @BindView(id = R.id.bottombar_content3, click = true)
    private RadioButton mRbtn3;
    @BindView(id = R.id.bottombar_content4, click = true)
    private RadioButton mRbtn4;

    BaseFragment content2 = new DBExample(); // 第二个界面
    BaseFragment content3 = new HorizontalListDemo(); // 第三个界面
    BaseFragment content4 = new DownloadExample(); // 第四个界面

    public ActionBar actionBar;

    public TabExampleTwo() {
        setHiddenActionBar(false);
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_tab_example);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        actionBar = getActionBar();
        mRbtn1.setText("没东西");
        mRbtn2.setText("数据库");
        mRbtn3.setText("控件展示");
        mRbtn4.setText("下载");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.bottombar_content1:
            break;
        case R.id.bottombar_content2:
            actionBar.setTitle("数据库基本操作");
            changeFragment(content2);
            break;
        case R.id.bottombar_content3:
            actionBar.setTitle("横向ListView与圆形ImageView");
            changeFragment(content3);
            break;
        case R.id.bottombar_content4:
            actionBar.setTitle("下载");
            changeFragment(content4);
            break;
        }
    }

    @Override
    public void changeFragment(BaseFragment targetFragment) {
        changeFragment(R.id.content, targetFragment);
    }
}
