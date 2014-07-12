package org.kymjs.aframe.ui.activity;

import org.kymjs.aframe.KJActivityManager;
import org.kymjs.aframe.ui.AnnotateUtil;
import org.kymjs.aframe.ui.I_BroadcastReg;
import org.kymjs.aframe.ui.ViewInject;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Activity's framework,the developer shouldn't extends it
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.5
 * @created 2014-3-1
 * @lastChange 2014-5-30
 */
public abstract class KJFrameActivity extends Activity implements
        OnClickListener, I_BroadcastReg {

    protected abstract void setContent();

    /** initialization widget */
    protected void initWidget() {}

    /** initialization data */
    protected void initData() {}

    /** listened widget's click method */
    protected void widgetClick(View v) {}

    @Override
    public void onClick(View v) {
        widgetClick(v);
    }

    @Override
    public void registerBroadcast() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KJActivityManager.create().addActivity(this);
        setContent();
        AnnotateUtil.initBindView(this);
        initData();
        initWidget();
        registerBroadcast();
    }

    @Override
    public void unRegisterBroadcast() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
        KJActivityManager.create().finishActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && KJActivityManager.create().getCount() < 2) {
            ViewInject.create().getExitDialog(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
