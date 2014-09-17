package org.kymjs.example.fragment;

import org.kymjs.aframe.bitmap.utils.BitmapOperateUtil;
import org.kymjs.aframe.ui.BindView;
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

    @BindView(id = R.id.img_button1, click = true)
    Button button;
    @BindView(id = R.id.img_button2, click = true)
    Button button2;
    @BindView(id = R.id.img_button3, click = true)
    Button button3;
    @BindView(id = R.id.imageview)
    ImageView image;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.imageview, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        button.setText("黑白图片");
        button2.setText("模糊图片");
        button3.setText("负片效果");
        image.setImageResource(R.drawable.bg);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        Bitmap src = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg);
        switch (v.getId()) {
        case R.id.img_button1:
            // 黑白效果
            src = BitmapOperateUtil.convertToBlackWhite(src);
            image.setImageBitmap(src);
            break;
        case R.id.img_button2:
            // 虚化效果
            BitmapOperateUtil.SetMistyBitmap(image, src);
            break;
        case R.id.img_button3:
            // 负片效果
            src = BitmapOperateUtil.tone(src, 10);
            image.setImageBitmap(src);
            break;
        }
    }
}
