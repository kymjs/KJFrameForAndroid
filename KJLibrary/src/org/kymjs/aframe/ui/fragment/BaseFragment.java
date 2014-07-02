package org.kymjs.aframe.ui.fragment;

import org.kymjs.aframe.KJLoger;

import android.os.Bundle;

/**
 * Application's base Fragment,you should inherit it for your Fragment
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-5-28
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
