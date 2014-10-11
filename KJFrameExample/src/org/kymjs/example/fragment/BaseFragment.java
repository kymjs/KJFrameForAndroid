package org.kymjs.example.fragment;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;

import org.kymjs.example.AppContext;
import org.kymjs.example.R;

import android.app.Activity;
import android.view.View;

/**
 * 本界面是广告加载的基类，如果您不喜欢可以删掉，希望您不这么做<br>
 * 作为个人开发者，我真心的希望能得到您的支持，谢谢。
 * 
 * 
 * @author kymjs
 */
public abstract class BaseFragment extends
        org.kymjs.aframe.ui.fragment.BaseFragment {

    Activity aty;

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        aty = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppContext.count++ % 3 == 1) {
            show();
        }
    }

    private void show() {
        AdManager.getInstance(aty).init(getString(R.string.id),
                getString(R.string.search), true);
        SpotManager manager = SpotManager.getInstance(aty);
        manager.loadSpotAds();
        manager.setShowInterval(20);
        manager.setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
        manager.showSpotAds(aty, new SpotDialogListener() {
            @Override
            public void onShowSuccess() {}

            @Override
            public void onShowFailed() {}

            @Override
            public void onSpotClosed() {}
        });
    }
}
