package org.kymjs.example.fragment;

import java.io.File;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.http.FileCallBack;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class DownloadExample extends BaseFragment {

    @BindView(id = R.id.edit)
    private EditText mEt;
    @BindView(id = R.id.button, click = true)
    private Button mBtn;
    @BindView(id = R.id.progress)
    private ProgressBar mProgress;

    private boolean maxed = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        return inflater.inflate(R.layout.example_download, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mEt.setText("https://raw.githubusercontent.com/kymjs/KJFrameForAndroid/master/KJFrameExample/big_image.png");
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button:
            KJHttp kjh = new KJHttp();
            FileCallBack file = new FileCallBack() {
                @Override
                public void onSuccess(File f) {
                    KJLoger.debug("下载成功");
                }

                @Override
                public void onLoading(long count, long current) {
                    super.onLoading(count, current);
                    if (!maxed) {
                        mProgress.setMax((int) count);
                        maxed = true;
                    }
                    mProgress.setProgress((int) current);
                }
            };
            file.setProgress(true);
            kjh.urlDownload(mEt.getText().toString(), "/storage/sdcard0/3.png",
                    file);
            break;
        }
    }
}
