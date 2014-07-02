package org.kymjs.example.fragment;

import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.choiceimg.ChoiceImageTemplate;

import android.view.View;

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
