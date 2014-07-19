package org.kymjs.example.fragment;

import org.kymjs.aframe.bitmap.utils.BitmapMistyUtil;
import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.example.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 图片滤化功能展示
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class BitmapMistyExample extends BaseFragment {

    @BindView(id = R.id.button1, click = true)
    Button button;
    @BindView(id = R.id.imageview)
    ImageView image;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        return inflater.inflate(R.layout.imageview, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        button.setText("一键虚化");
        image.setImageResource(R.drawable.bg);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        ViewInject.toast("模糊吗，叫你不要撸太多，偏不听");
        Bitmap src = BitmapFactory
                .decodeResource(getResources(), R.drawable.bg);
        src = BitmapMistyUtil.SetMistyBitmap(image, src, true);
    }
}
