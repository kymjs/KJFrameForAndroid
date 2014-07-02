package org.kymjs.aframe.ui.activity;

import org.kymjs.kjlibrary.R;

import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 应用启动的欢迎界面模板
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class BaseSplash extends BaseActivity {

    protected ImageView mImageView;

    @Override
    protected void setContent() {
        setAllowFullScreen(true);
        setHiddenActionBar(true);
        setScreenOrientation(ScreenOrientation.VERTICAL);
        mImageView = new ImageView(this);
        mImageView.setScaleType(ScaleType.FIT_XY);
        setContentView(mImageView);
        // mImageView.setImageResource(R.drawable.bg_start);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        // 监听动画过程
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                checkVersion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
                finish();
            }
        });
        mImageView.setAnimation(animation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    /**
     * 跳转到...
     */
    protected void redirectTo() {
        if (firstsInstall()) {
        }
    }

    /**
     * 判断首次使用
     */
    protected boolean firstsInstall() {
        return true;
    }

    /**
     * 检查更新
     */
    protected void checkVersion() {}
}
