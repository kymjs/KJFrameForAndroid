package org.kymjs.aframe.ui.activity;

import android.view.View;

/**
 * KJFrameActivity接口协议，实现此接口可使用KJActivityManager堆栈
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-3-1
 * @lastChange 2014-5-30
 */
public interface I_KJActivity {
    /** 初始化方法 */
    void initialize();

    void setRootView();

    void widgetClick(View v);
}
