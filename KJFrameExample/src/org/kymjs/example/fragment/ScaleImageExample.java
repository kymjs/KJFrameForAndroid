package org.kymjs.example.fragment;

import org.kymjs.aframe.ui.BindView;
import org.kymjs.aframe.ui.fragment.BaseFragment;
import org.kymjs.aframe.ui.widget.ScaleImageView;
import org.kymjs.example.R;
import org.kymjs.example.activity.SlidExample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 图片缩放控件展示
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class ScaleImageExample extends BaseFragment {
    @BindView(id = R.id.button1, click = true)
    private Button back;
    @BindView(id = R.id.button2, click = true)
    private Button rotate;
    @BindView(id = R.id.button3, click = true)
    private Button doubleClick;
    @BindView(id = R.id.imageview)
    private ScaleImageView imageview;

    @Override
    protected View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.example_scale_imageview,
                null);
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        back.setText("点击或侧滑返回,图片还可以双指拉伸");
        rotate.setText("可以旋转" + imageview.canRotate());
        doubleClick.setText("双击缩放开关" + imageview.canDoubleClick());
        ((SlidExample) getActivity()).resideMenu
                .addIgnoredView(imageview);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        if (v == back) {
            if (getActivity() instanceof SlidExample) {
                ((SlidExample) getActivity()).changeSlidMenu();
            }
        } else if (v == rotate) {
            // 旋转开关
            imageview.setCanRotate(!imageview.canRotate());
            rotate.setText("旋转开关" + imageview.canRotate());
        } else if (v == doubleClick) {
            // 双击开关
            imageview.setCanDoubleClick(!imageview.canDoubleClick());
            doubleClick.setText("双击或拉伸缩放"
                    + imageview.canDoubleClick());
        }
    }
}
