package org.kymjs.example.activity;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.activity.KJFragmentActivity;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;
import org.kymjs.example.fragment.ChoiceImageExample;
import org.kymjs.example.fragment.ExampleFragment;
import org.kymjs.example.fragment.ScaleImageExample;

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

    BaseFragment content1 = new ExampleFragment(); // 第一个界面
    BaseFragment content2 = new ChoiceImageExample(); // 第二个界面
    BaseFragment content3 = new ScaleImageExample(); // 第三个界面
    BaseFragment content4 = new ExampleFragment(); // 第四个界面

    @Override
    protected void setContent() {
        setContentView(R.layout.aty_tab_example);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.bottombar_content1:
            startActivity(new Intent(this, SlidExample.class));
            break;
        case R.id.bottombar_content2:
            changeFragment(content2);
            ViewInject.toast("2");
            break;
        case R.id.bottombar_content3:
            changeFragment(content3);
            ViewInject.toast("3");
            break;
        case R.id.bottombar_content4:
            changeFragment(content4);
            ViewInject.toast("4");
            break;
        }
    }

    @Override
    protected void changeFragment(BaseFragment targetFragment) {
        changeFragment(R.id.content, targetFragment);
    }
}
