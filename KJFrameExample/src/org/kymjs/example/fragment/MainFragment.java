package org.kymjs.example.fragment;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.KJViewPager;
import org.kymjs.aframe.ui.widget.KJViewPager.OnViewChangeListener;
import org.kymjs.example.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainFragment extends BaseFragment {

    @BindView(id = R.id.main_tv_title)
    private TextView mTvTitle;
    @BindView(id = R.id.main_pager)
    private KJViewPager mContentPager;

    @Override
    protected View inflaterView(LayoutInflater arg0, ViewGroup arg1,
            Bundle arg2) {
        return arg0.inflate(R.layout.frag_main, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mContentPager
                .setOnViewChangeListener(new OnViewChangeListener() {
                    @Override
                    public void OnViewChange(int page) {
                        ViewInject.toast("这是第" + page + "个界面");
                    }
                });
    }
}
