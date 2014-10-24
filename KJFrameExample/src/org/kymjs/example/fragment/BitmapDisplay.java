package org.kymjs.example.fragment;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.bitmap.BitmapCallBack;
import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.bitmap.KJBitmapConfig;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;
import org.kymjs.example.activity.SlidExample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 网络图片加载Demo（在侧滑界面下则展示侧滑效果）
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class BitmapDisplay extends BaseFragment {
    @BindView(id = R.id.button1, click = true)
    private Button button;
    @BindView(id = R.id.button2, click = true)
    private Button button2;
    @BindView(id = R.id.button3, click = true)
    private Button button3;
    @BindView(id = R.id.button4, click = true)
    private Button button4;
    @BindView(id = R.id.imageview, click = true)
    private ImageView imageView;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.example_layout, null);
        return view;
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        button2.setVisibility(View.VISIBLE);
        button2.setText("点击加载网络图片");
        button3.setVisibility(View.VISIBLE);
        button3.setText("图片加载过程中显示环形等待条");
        button4.setVisibility(View.VISIBLE);
        button4.setText("config的使用");
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            if (getActivity() instanceof SlidExample) {
                ((SlidExample) getActivity()).changeSlidMenu();
            }
            break;
        case R.id.button2:
            KJBitmap kjb2 = KJBitmap.create();
            // // 载入本地图片
            // kjb.display(imageView, "/storage/sdcard0/1.jpg");
            // 载入网络图片
            kjb2.display(
                    imageView,
                    "http://imgsrc.baidu.com/forum/w%3D580/sign=b16bfbd859b5c9ea62f303ebe53bb622/588e5ece36d3d53933f0c2103887e950372ab0fd.jpg",
                    50, 50);
            break;
        case R.id.button3:
            KJBitmap kjb3 = KJBitmap.create();
            kjb3.display(
                    imageView,
                    "http://e.hiphotos.baidu.com/image/pic/item/9358d109b3de9c829d7a94986f81800a19d8438d.jpg",
                    true); // 开启环形等待条
            ViewInject.toast("图片较大，加载中");
            break;
        case R.id.button4:
            KJBitmapConfig config = new KJBitmapConfig();
            config.loadingBitmap = BitmapCreate.bitmapFromResource(
                    getResources(), R.drawable.ic_launcher, 40, 40);
            config.callBack = new BitmapCallBack() {
                @Override
                public void imgLoading(View view) {
                    super.imgLoading(view);
                    KJLoger.debug("载入中");
                    ViewInject.toast("载入中");
                }

                @Override
                public void imgLoadSuccess(View view) {
                    super.imgLoadSuccess(view);
                    KJLoger.debug("载入成功");
                    ViewInject.toast("载入成功");
                }

                @Override
                public void imgLoadFailure(String url, String msg) {
                    super.imgLoadFailure(url, msg);
                    KJLoger.debug("载入失败");
                    ViewInject.toast("载入失败");
                    imageView
                            .setImageResource(R.drawable.ic_launcher);
                }
            };
            KJBitmap kjb4 = KJBitmap.create(config);
            kjb4.display(v, "http://123.123.123.123");
            break;
        }
    }
}
