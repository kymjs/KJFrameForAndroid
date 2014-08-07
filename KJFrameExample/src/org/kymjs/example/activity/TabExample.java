package org.kymjs.example.activity;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.activity.KJFragmentActivity;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;
import org.kymjs.example.fragment.BitmapMistyExample;
import org.kymjs.example.fragment.ChoiceImageExample;
import org.kymjs.example.fragment.HttpExample;
import org.kymjs.example.fragment.ListBitmapExample;

import android.app.ActionBar;
import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;

/**
 * 底部Tab导航效果的布局，使用RadioButton和Fragment组合
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class TabExample extends KJFragmentActivity {

    @BindView(id = R.id.bottombar_content1, click = true)
    public RadioButton mRbtn1;
    @BindView(id = R.id.bottombar_content2, click = true)
    private RadioButton mRbtn2;
    @BindView(id = R.id.bottombar_content3, click = true)
    private RadioButton mRbtn3;
    @BindView(id = R.id.bottombar_content4, click = true)
    private RadioButton mRbtn4;

    BaseFragment content1 = new ChoiceImageExample(); // 第一个界面
    BaseFragment content2 = new HttpExample(); // 第二个界面
    BaseFragment content3 = new ListBitmapExample(); // 第三个界面
    BaseFragment content4 = new BitmapMistyExample(); // 第四个界面

    public ActionBar actionBar;

    public TabExample() {
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
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.bottombar_content1:
            startActivity(new Intent(this, SlidExample.class));
            ViewInject.toast("侧滑试试");
            break;
        case R.id.bottombar_content2:
            actionBar.setTitle("网络请求");
            changeFragment(false, content2);
            break;
        case R.id.bottombar_content3:
            actionBar.setTitle("listview网络图片加载");
            changeFragment(false, content3);
            break;
        case R.id.bottombar_content4:
            actionBar.setTitle("图片模糊效果");
            changeFragment(false, content4);
            break;
        }
    }

    @Override
    public void changeFragment(boolean addBackStack, BaseFragment targetFragment) {
        changeFragment(R.id.content, addBackStack, targetFragment);
    }
}
