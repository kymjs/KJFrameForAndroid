package org.kymjs.aframe.plugin.example;

import org.kymjs.aframe.plugin.activity.CJActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;

public class FragmentAty extends CJActivity {

    /**
     * 注意：1、setContentView必须放在setRootView()方法中调用<br>
     * 2、能不用onCreate就不要用onCreate方法，在KJFrameForAndroid中已经定义了完善的初始化回调方法
     */
    @Override
    public void setRootView() {
        super.setRootView();
        that.setContentView(R.layout.aty_fragment);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        changeFragment(R.id.content, new TestFragment());
    }

    /**
     * 用来初始化数据
     */
    @Override
    protected void initData() {
        super.initData();
    }

    /**
     * 本代码块将在线程中运行（具体请查看API：BaseActivity）
     */
    @Override
    protected void initDataFromThread() {
        super.initDataFromThread();
    }

    protected void changeFragment(int resView, Fragment targetFragment) {
        FragmentTransaction transaction = that.getFragmentManager()
                .beginTransaction();
        transaction.replace(resView, targetFragment, targetFragment
                .getClass().getName());
        transaction.commit();
    }
}
