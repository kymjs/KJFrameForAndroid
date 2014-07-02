package org.kymjs.aframe.ui.activity;

import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.ResideMenu;
import org.kymjs.aframe.ui.widget.ResideMenuItem.OnMenuClickListener;

import android.view.MotionEvent;
import android.view.View;

/**
 * 具有侧滑缩放效果侧滑界面模板
 * 
 * @warn 在changeFragment()中必须调用super.changeFragment(targetFragment);
 * @author kymjs(kymjs123@gmail.com)
 */
public abstract class SlidTemplet extends KJFragmentActivity implements
        OnMenuClickListener {
    protected ResideMenu resideMenu;
    
    /** 设置Activity布局 */
    protected abstract int setRootViewID();
    
    /** 初始化侧滑菜单界面控件 */
    protected abstract void initSlidMenu();
    
    @Override
    protected void setContent() {
        View root = View.inflate(this, setRootViewID(), null);
        setContentView(root);
        resideMenu = new ResideMenu(this);
        resideMenu.attachToActivity(this);
        resideMenu.addIgnoredView(root);
    }
    
    @Override
    protected void initWidget() {
        initSlidMenu();
        super.initWidget();
    }
    
    /**
     * 改变Menu状态
     */
    public void changeSlidMenu() {
        if (resideMenu.isOpened()) {
            resideMenu.closeMenu();
        } else {
            resideMenu.openMenu();
        }
    }
    
    @Override
    protected void changeFragment(BaseFragment targetFragment) {
        // 清空不拦截触摸事件的控件（界面已经被替换）
        resideMenu.clearIgnoredViewList();
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.onInterceptTouchEvent(ev)
                || super.dispatchTouchEvent(ev);
    }
    
    @Override
    public void onClick(View v) {
        super.onClick(v);
        onSlidMenuClick(v);
    }
}
