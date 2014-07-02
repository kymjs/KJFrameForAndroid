package org.kymjs.aframe.ui.activity;

import org.kymjs.aframe.KJLoger;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Application BaseActivity,you should inherit it for your Activity
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-5-28
 */
public abstract class BaseActivity extends KJFrameActivity {
    public static enum ActivityState {
        RESUME, STOP, PAUSE, DESTORY
    }

    public static enum ScreenOrientation {
        HORIZONTAL, VERTICAL, AUTO
    }

    // Activity状态
    public ActivityState activityState = ActivityState.DESTORY;
    // 是否允许全屏
    private boolean mAllowFullScreen = false;
    // 是否隐藏ActionBar
    private boolean mHiddenActionBar = true;
    // 屏幕方向
    private ScreenOrientation orientation = ScreenOrientation.VERTICAL;

    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    public void setHiddenActionBar(boolean hiddenActionBar) {
        this.mAllowFullScreen = hiddenActionBar;
    }

    public void setScreenOrientation(ScreenOrientation orientation) {
        this.orientation = orientation;
    }

    /***************************************************************************
     * 
     * print Activity callback methods
     * 
     ***************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KJLoger.state(this.getClass().getName(), "---------onCreat ");
        switch (orientation) {
        case HORIZONTAL:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            break;
        case VERTICAL:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            break;
        case AUTO:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            break;
        }

        if (mHiddenActionBar) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (mAllowFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        KJLoger.state(this.getClass().getName(), "---------onStart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityState = ActivityState.RESUME;
        KJLoger.state(this.getClass().getName(), "---------onResume ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityState = ActivityState.PAUSE;
        KJLoger.state(this.getClass().getName(), "---------onPause ");
    }

    @Override
    protected void onStop() {
        super.onResume();
        activityState = ActivityState.STOP;
        KJLoger.state(this.getClass().getName(), "---------onStop ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        KJLoger.state(this.getClass().getName(), "---------onRestart ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityState = ActivityState.DESTORY;
        KJLoger.state(this.getClass().getName(), "---------onDestroy ");
    }
}
