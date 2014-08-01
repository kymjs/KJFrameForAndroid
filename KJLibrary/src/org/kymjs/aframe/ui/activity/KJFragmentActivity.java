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

import org.kymjs.aframe.ui.KJActivityManager;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.kjlibrary.R;

import android.app.FragmentTransaction;
import android.view.KeyEvent;

/**
 * Application BaseActivity plus. For ease of use, your Activity should overload
 * changeFragment(Fragment frag).
 * 
 * @explain if you want include the Fragment,you should extends it for your
 *          Activity
 * @explain else you should extends KJFrameActivity for your Activity
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-5-14
 */
public abstract class KJFragmentActivity extends BaseActivity {
    private boolean openBackListener = false;

    public KJFragmentActivity() {
        openBackListener = getBackListener();
        setBackListener(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (openBackListener && keyCode == KeyEvent.KEYCODE_BACK
                && getFragmentManager().getBackStackEntryCount() == 0
                && KJActivityManager.create().getCount() < 2) {
            ViewInject.create().getExitDialog(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    /** 改变界面的fragment */
    protected void changeFragment(int resView, boolean addStack,
            BaseFragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(resView, targetFragment, targetFragment.getClass()
                .getName());
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left);
        if (addStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 你应该在这里调用changeFragment(R.id.content, addStack, targetFragment);
     * 
     * @param addStack
     *            是否加入返回栈（加入返回栈后，用户按下返回键可以返回调用本方法之前的界面）
     * @param targetFragment
     *            要改变的Activity
     */
    public abstract void changeFragment(boolean addStack,
            BaseFragment targetFragment);
}
