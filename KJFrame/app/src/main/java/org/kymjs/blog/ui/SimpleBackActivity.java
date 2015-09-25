/*
 * Copyright (c) 2015, 张涛.
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
package org.kymjs.blog.ui;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.SimpleBackPage;
import org.kymjs.blog.ui.fragment.TitleBarFragment;
import org.kymjs.kjframe.ui.KJFragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 应用二级界面
 * 
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 * 
 */
public class SimpleBackActivity extends TitleBarActivity {
    public static String TAG = SimpleBackActivity.class.getSimpleName();
    public static String CONTENT_KEY = "sba_content_key";
    public static String DATA_KEY = "sba_datat_key";

    private TitleBarFragment currentFragment;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_simple_back);
        int value = getIntent().getIntExtra(CONTENT_KEY, -1);
        if (value != -1) {
            try {
                currentFragment = (TitleBarFragment) SimpleBackPage
                        .getPageByValue(value).newInstance();
                changeFragment(currentFragment);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

    public Bundle getBundleData() {
        return getIntent().getBundleExtra(DATA_KEY);
    }

    public void changeFragment(KJFragment targetFragment) {
        super.changeFragment(R.id.main_content, targetFragment);
    }

    @Override
    protected void onBackClick() {
        super.onBackClick();
        if (currentFragment != null) {
            currentFragment.onBackClick();
        }
    }

    @Override
    protected void onMenuClick() {
        super.onMenuClick();
        if (currentFragment != null) {
            currentFragment.onMenuClick();
        }
    }

    /**
     * 跳转到SimpleBackActivity时，只能使用该方法跳转
     * 
     * @param cxt
     * @param page
     * @param data
     */
    public static void postShowWith(Context cxt, SimpleBackPage page,
            Bundle data) {
        Intent intent = new Intent(cxt, SimpleBackActivity.class);
        intent.putExtra(CONTENT_KEY, page.getValue());
        intent.putExtra(DATA_KEY, data);
        cxt.startActivity(intent);
    }

    /**
     * 跳转到SimpleBackActivity时，只能使用该方法跳转
     * 
     * @param cxt
     * @param page
     */
    public static void postShowWith(Context cxt, SimpleBackPage page) {
        postShowWith(cxt, page, null);
    }

    /**
     * 跳转到SimpleBackActivity时，只能使用该方法跳转
     * 
     * @param cxt
     *            从哪个Activity跳转
     * @param code
     *            启动码
     * @param page
     *            要显示的Fragment
     * @param data
     *            传递的Bundle数据
     */
    public static void postShowForResult(Fragment fragment, int code,
            SimpleBackPage page, Bundle data) {
        Intent intent = new Intent(fragment.getActivity(),
                SimpleBackActivity.class);
        intent.putExtra(CONTENT_KEY, page.getValue());
        intent.putExtra(DATA_KEY, data);
        fragment.startActivityForResult(intent, code);
    }

    /**
     * 跳转到SimpleBackActivity时，只能使用该方法跳转
     * 
     * @param cxt
     *            从哪个Activity跳转
     * @param code
     *            启动码
     * @param page
     *            要显示的Fragment
     */
    public static void postShowForResult(Fragment fragment, int code,
            SimpleBackPage page) {
        postShowForResult(fragment, code, page, null);
    }
}
