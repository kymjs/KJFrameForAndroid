package org.kymjs.example.fragment;

import java.io.File;

import org.kymjs.aframe.http.FileCallBack;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
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

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.example_download, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        // 一张5M图片的下载地址
        mEt.setText("https://raw.githubusercontent.com/kymjs/KJFrameForAndroid/master/KJFrameExample/big_image2.jpg");
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button:
            KJHttp kjh = new KJHttp();
            FileCallBack file = new FileCallBack() {
                /**
                 * 下载成功时回调
                 */
                @Override
                public void onSuccess(File f) {
                    ViewInject.toast("下载成功");
                }

                /**
                 * 下载过程中循环回调，必须设置file.setProgress(true);
                 */
                @Override
                public void onLoading(long count, long current) {
                    super.onLoading(count, current);
                    mProgress.setMax((int) count);
                    mProgress.setProgress((int) current);
                }

                /**
                 * 下载失败回调
                 */
                @Override
                public void onFailure(Throwable t, int errorNo,
                        String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    ViewInject.toast("失败原因： " + strMsg);
                    mProgress.setMax(100000);
                    mProgress.setProgress(0);
                }
            };
            // 若要显示进度，必须设置为true
            file.setProgress(true);
            kjh.urlDownload(mEt.getText().toString(),
                    "/storage/sdcard0/3.png", file);
            break;
        }
    }
}
