package org.kymjs.example.activity;

import org.kymjs.aframe.ui.activity.KJFragmentActivity;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;

public class ShowDemo extends KJFragmentActivity {

    public static BaseFragment content;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_showdemo);
        if (content != null) {
            changeFragment(content);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        setBackListener(false);
    }

    @Override
    public void changeFragment(BaseFragment targetFragment) {
        changeFragment(R.id.content, targetFragment);
    }
}
