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

import android.app.FragmentTransaction;
import android.view.KeyEvent;

/**
 * Application BaseActivity plus. For ease of use, your Activity should overload
 * changeFragment(Fragment frag).<br>
 * 
 * <b>说明</b> if you want include the Fragment,you should extends it for your
 * Activity <br>
 * <b>说明</b> else you should extends KJFrameActivity for your Activity<br>
 * 
 * <b>创建时间</b> 2014-5-14
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
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
    protected void changeFragment(int resView,
            BaseFragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(resView, targetFragment, targetFragment
                .getClass().getName());
        transaction.commit();
    }

    /**
     * 你应该在这里调用changeFragment(R.id.content, addStack, targetFragment);
     * 
     * @param targetFragment
     *            要改变的Activity
     */
    public abstract void changeFragment(BaseFragment targetFragment);
}
