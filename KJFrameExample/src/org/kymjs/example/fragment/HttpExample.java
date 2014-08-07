package org.kymjs.example.fragment;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.http.KJHttp;
import org.kymjs.aframe.http.KJParams;
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
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        return inflater.inflate(R.layout.example_layout, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        btn1.setText("POST请求示例");
        btn2.setVisibility(View.VISIBLE);
        btn2.setText("GET请求示例");
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

    private void httpGetData() {
        KJHttp kjh = new KJHttp();
        kjh.urlGet("这里填网址！！！", new StringCallBack() {
            @Override
            public void onSuccess(String json) {
                ViewInject.longToast(json);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ViewInject.longToast(strMsg);
            }
        });
    }

    private void httpData() {
        KJHttp kjh = new KJHttp();
        KJParams params = new KJParams();
        params.put("uid", "13212347894");
        params.put("password", "147852369");
        params.put("login_type", "0");
        kjh.urlPost("http://mr.tn10000.com/index.php/umessage/login", params,
                new StringCallBack() {
                    @Override
                    public void onSuccess(String json) {
                        ViewInject.longToast(json);
                        KJLoger.debug(getClass().getName() + "网络信息：" + json);
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
