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
package org.kymjs.aframe.ui.fragment;

import org.kymjs.aframe.KJLoger;

import android.os.Bundle;

/**
 * Application's base Fragment,you should inherit it for your Fragment<br>
 * 
 * <b>创建时间</b> 2014-5-28
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public abstract class BaseFragment extends KJFrameFragment {

    /***************************************************************************
     * 
     * print Fragment callback methods
     * 
     ***************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KJLoger.state(this.getClass().getName(), "---------onCreateView ");
    }

    @Override
    public void onResume() {
        KJLoger.state(this.getClass().getName(), "---------onResume ");
        super.onResume();
    }

    @Override
    public void onPause() {
        KJLoger.state(this.getClass().getName(), "---------onPause ");
        super.onPause();
    }

    @Override
    public void onStop() {
        KJLoger.state(this.getClass().getName(), "---------onStop ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        KJLoger.state(this.getClass().getName(), "---------onDestroy ");
        super.onDestroyView();
    }
}
