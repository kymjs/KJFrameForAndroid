package org.kymjs.example.activity;

import org.kymjs.aframe.KJActivityManager;
import org.kymjs.aframe.ui.activity.SlidTemplet;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.ResideMenuItem;
import org.kymjs.example.R;
import org.kymjs.example.fragment.ChoiceImageExample;
import org.kymjs.example.fragment.ExampleFragment;
import org.kymjs.example.fragment.ScaleImageExample;

import android.view.View;

/**
 * 侧滑缩放效果的布局
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class SlidExample extends SlidTemplet {
    private ResideMenuItem item1;
    private ResideMenuItem item2;
    private ResideMenuItem item3;
    private ResideMenuItem item4;
    private ResideMenuItem item5;

    private BaseFragment fragContent;

    @Override
    protected int setRootViewID() {
        return R.layout.aty_slid_example;
    }

    @Override
    protected void initSlidMenu() {
        item1 = new ResideMenuItem(this, R.drawable.ic_launcher, "第一项");
        item2 = new ResideMenuItem(this, R.drawable.ic_launcher, "第二项");
        item3 = new ResideMenuItem(this, R.drawable.ic_launcher, "图片缩放");
        item4 = new ResideMenuItem(this, R.drawable.ic_launcher, "多图选择");
        item5 = new ResideMenuItem(this, R.drawable.ic_launcher, "立即退出");
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
        item5.setOnClickListener(this);
        resideMenu.addMenuItem(item1);
        resideMenu.addMenuItem(item2);
        resideMenu.addMenuItem(item3);
        resideMenu.addMenuItem(item4);
        resideMenu.addMenuItem(item5);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        fragContent = new ExampleFragment();
        changeFragment(fragContent);
    }

    /**
     * 必须调用super()，否则界面触摸将被屏蔽
     */
    @Override
    protected void changeFragment(BaseFragment targetFragment) {
        super.changeFragment(targetFragment);
        changeFragment(R.id.content, targetFragment);
    }

    @Override
    public void onSlidMenuClick(View v) {
        if (v == item1) {
            changeFragment(new ExampleFragment());
        } else if (v == item2) {
            changeFragment(new ExampleFragment());
        } else if (v == item3) {
            changeFragment(new ScaleImageExample());
        } else if (v == item4) {
            changeFragment(new ChoiceImageExample());
        } else if (v == item5) {
            KJActivityManager.create().AppExit(SlidExample.this);
        }
        resideMenu.closeMenu();
    }
}
