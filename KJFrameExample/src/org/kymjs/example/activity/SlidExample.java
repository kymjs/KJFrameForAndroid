package org.kymjs.example.activity;

import org.kymjs.aframe.ui.KJActivityManager;
import org.kymjs.aframe.ui.activity.SlidTemplet;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.ResideMenuItem;
import org.kymjs.example.R;
import org.kymjs.example.fragment.BitmapDisplay;
import org.kymjs.example.fragment.BitmapMistyExample;
import org.kymjs.example.fragment.ChoiceImageExample;
import org.kymjs.example.fragment.ScaleImageExample;

import android.app.ActionBar;
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

    public ActionBar actionBar;

    private BaseFragment fragContent;

    public SlidExample() {
        setHiddenActionBar(false);
    }

    @Override
    protected int setRootViewID() {
        actionBar = getActionBar();
        return R.layout.aty_slid_example;
    }

    @Override
    protected void initSlidMenu() {
        item1 = new ResideMenuItem(this, R.drawable.ic_launcher, "加载网络图");
        item2 = new ResideMenuItem(this, R.drawable.ic_launcher, "模糊图片");
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
        actionBar.setTitle("侧滑效果演示");
        fragContent = new BitmapDisplay();
        changeFragment(false, fragContent);
    }

    /**
     * 必须调用super()，否则界面触摸将被屏蔽
     */
    @Override
    public void changeFragment(boolean addStack, BaseFragment targetFragment) {
        super.changeFragment(addStack, targetFragment);
        changeFragment(R.id.content, addStack, targetFragment);
    }

    @Override
    public void onSlidMenuClick(View v) {
        if (v == item1) {
            actionBar.setTitle("网络图片加载");
            changeFragment(false, new BitmapDisplay());
        } else if (v == item2) {
            actionBar.setTitle("图片模糊效果");
            changeFragment(false, new BitmapMistyExample());
        } else if (v == item3) {
            actionBar.setTitle("图片缩放效果");
            changeFragment(false, new ScaleImageExample());
        } else if (v == item4) {
            actionBar.setTitle("多图选择效果");
            changeFragment(false, new ChoiceImageExample());
        } else if (v == item5) {
            KJActivityManager.create().AppExit(SlidExample.this);
        }
        resideMenu.closeMenu();
    }
}
