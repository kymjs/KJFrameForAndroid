/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.aframe.ui.activity;

import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.ResideMenu;
import org.kymjs.aframe.ui.widget.ResideMenuItem;
import org.kymjs.aframe.ui.widget.ResideMenuItem.OnMenuClickListener;

import android.view.MotionEvent;
import android.view.View;

/**
 * 具有侧滑缩放效果侧滑界面模板<br>
 * 
 * <b>警告</b> 在changeFragment()中必须调用super.changeFragment(targetFragment);
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public abstract class SlidTemplet extends KJFragmentActivity
        implements OnMenuClickListener {
    public ResideMenu resideMenu;

    /** 设置Activity布局 */
    protected abstract int setRootViewID();

    @Override
    public void setRootView() {
        View root = View.inflate(this, setRootViewID(), null);
        setContentView(root);
        resideMenu = new ResideMenu(this);
        resideMenu.attachToActivity(this);
        resideMenu.addIgnoredView(root);
    }

    /**
     * 过时方法：初始化侧滑菜单界面控件<br>
     * 你应该使用initSlidMenus(ResideMenuItem...)来初始化每一个菜单项
     */
    @Deprecated
    protected void initSlidMenu() {}

    @Override
    protected void initWidget() {
        initSlidMenu();
        super.initWidget();
    }

    /** 初始化侧滑菜单界面控件 */
    protected void initSlidMenus(ResideMenuItem... items) {
        if (items != null) {
            for (ResideMenuItem item : items) {
                item.setOnClickListener(this);
                resideMenu.addMenuItem(item);
            }
        }
    }

    /**
     * 改变Menu状态，若是关闭则改为开启，若是开启则改为关闭
     */
    public void changeSlidMenu() {
        if (resideMenu.isOpened()) {
            resideMenu.closeMenu();
        } else {
            resideMenu.openMenu();
        }
    }

    @Override
    public void changeFragment(BaseFragment targetFragment) {
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
