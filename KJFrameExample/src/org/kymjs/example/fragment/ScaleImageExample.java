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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class ScaleImageExample extends BaseFragment {
    @BindView(id = R.id.layout)
    private LinearLayout layout;
    @BindView(id = R.id.button1, click = true)
    private Button button;
    
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        return inflater.inflate(R.layout.example_layout, null);
    }
    
    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        ScaleImageView imageView = new ScaleImageView(getActivity(),
                BitmapFactory.decodeResource(getResources(), R.drawable.bg));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        button.setText("查看菜单");
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
