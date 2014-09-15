package org.kymjs.example.fragment;

import org.kymjs.aframe.bitmap.KJBitmap;
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
            KJBitmap kjb = KJBitmap.create();
            // // 载入本地图片
            // kjb.display(imageView, "/storage/sdcard0/1.jpg");
            // 载入网络图片
            kjb.display(
                    imageView,
                    "http://imgsrc.baidu.com/forum/w%3D580/sign=b16bfbd859b5c9ea62f303ebe53bb622/588e5ece36d3d53933f0c2103887e950372ab0fd.jpg",
                    50, 50);
            break;
        case R.id.button3:
            KJBitmap kjbitmap = KJBitmap.create();
            kjbitmap.display(
                    imageView,
                    "https://raw.githubusercontent.com/kymjs/KJFrameForAndroid/master/KJFrameExample/big_image2.jpg",
                    true); // 开启环形等待条
            ViewInject.toast("图片较大，加载中");
            break;
        }
    }
}
