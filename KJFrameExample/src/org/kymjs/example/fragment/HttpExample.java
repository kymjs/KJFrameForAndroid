package org.kymjs.example.fragment;

import java.io.File;
import java.io.FileNotFoundException;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.http.KJFileParams;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.http.KJStringParams;
import org.kymjs.aframe.http.StringCallBack;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HttpExample extends BaseFragment {
    @BindView(id = R.id.button1, click = true)
    Button btn1;
    @BindView(id = R.id.button2, click = true)
    Button btn2;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.example_layout, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        btn1.setText("POST请求JSON示例");
        btn2.setVisibility(View.VISIBLE);
        btn2.setText("GET请求JSON示例,更多示例请看代码");
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            httpData();
            break;
        case R.id.button2:
            httpGetData();
            break;
        }
    }

    /**
     * get请求json
     */
    private void httpGetData() {
        KJHttp kjh = new KJHttp();
        kjh.urlGet("这里填网址！！！", new StringCallBack() {
            @Override
            public void onSuccess(String json) {
                ViewInject.longToast(json);
            }

            @Override
            public void onFailure(Throwable t, int errorNo,
                    String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ViewInject.longToast(strMsg);
            }
        });
    }

    /**
     * post上传文件
     */
    private void httpFile() {
        KJHttp kjh = new KJHttp();
        KJFileParams params = new KJFileParams();
        params.put("user_id", "33");
        try {
            params.put(new File("/storage/sdcard0/1.jpg"));
        } catch (FileNotFoundException e) {
            ViewInject.toast("图片没有找到");
        }
        kjh.urlPost(
                "http://l.tn10000.com/index.php/umessage/update_message",
                params, new StringCallBack() {
                    @Override
                    public void onSuccess(String json) {
                        ViewInject.longToast(json);
                        KJLoger.debug(getClass().getName() + "网络信息："
                                + json);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                            String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ViewInject.longToast(strMsg);
                    }
                });
    }

    /**
     * post请求json
     */
    private void httpData() {
        KJHttp kjh = new KJHttp();
        KJStringParams params = new KJStringParams();
        params.put("user_id", "33");
        params.put("birthday", "2008-8-1");
        kjh.post(
                "http://l.tn10000.com/index.php/umessage/update_message",
                params, new StringCallBack() {
                    @Override
                    public void onSuccess(String json) {
                        ViewInject.longToast(json);
                        KJLoger.debug(getClass().getName() + "网络信息："
                                + json);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                            String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ViewInject.longToast(strMsg);
                    }
                });
    }
}
