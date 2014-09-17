package org.kymjs.example.fragment;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.ScaleImageView;
import org.kymjs.example.R;
import org.kymjs.example.activity.SlidExample;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 图片缩放控件展示
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class ScaleImageExample extends BaseFragment {
    @BindView(id = R.id.layout)
    private LinearLayout layout;
    @BindView(id = R.id.button1, click = true)
    private Button button;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.example_layout, null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        final ScaleImageView imageView = new ScaleImageView(
                getActivity());
        imageView.setBackgroundColor(0xff000000);
        imageView.setImageBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.bg));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        button.setText("点击或侧滑返回,图片还可以双指拉伸");
        final Button rotate = new Button(getActivity());
        rotate.setText("可以旋转" + imageView.canRotate());
        rotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setCanRotate(!imageView.canRotate());
                rotate.setText("旋转开关" + imageView.canRotate());
            }
        });
        final Button doubleClick = new Button(getActivity());
        doubleClick.setText("双击缩放开关" + imageView.canDoubleClick());
        doubleClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setCanDoubleClick(!imageView
                        .canDoubleClick());
                doubleClick.setText("双击或拉伸缩放"
                        + imageView.canDoubleClick());
            }
        });
        layout.addView(rotate);
        layout.addView(doubleClick);
        layout.addView(imageView);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        if (v == button) {
            if (getActivity() instanceof SlidExample) {
                ((SlidExample) getActivity()).changeSlidMenu();
            }
        }
    }
}
