package org.kymjs.example.fragment;

import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.http.KJParams;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HttpExample extends BaseFragment {
    @BindView(id = R.id.button1, click = true)
    Button btn;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        return inflater.inflate(R.layout.example_layout, null);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:

            break;
        }
    }

    private void httpData() {
        KJHttp kjh = new KJHttp();
        KJParams params = new KJParams();
        // kjh.urlPost("", params, hello);
    }
}
