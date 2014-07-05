package org.kymjs.example.fragment;

import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.ChoiceImageTemplate;

import android.view.View;

/**
 * 多图选择界面展示
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class ChoiceImageExample extends ChoiceImageTemplate {

    @Override
    protected void OnClickCommitEvent(View v) {
        if (!checkFile.isEmpty()) {
            ViewInject.toast("已捕获选择的图片,共有" + checkFile.size() + "张");
        } else {
            ViewInject.toast("没有选择图片");
        }
    }
}
