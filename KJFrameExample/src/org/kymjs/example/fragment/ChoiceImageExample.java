package org.kymjs.example.fragment;

import java.util.List;

import org.kymjs.aframe.ui.ViewInject;
import org.kymjs.aframe.ui.fragment.ChoiceImageTemplate;

import android.view.View;

/**
 * 多图选择界面展示
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class ChoiceImageExample extends ChoiceImageTemplate {

    /**
     * 用户选中图片点击确定后将会回调
     * 
     * @param v
     *            确定按钮
     * @param datas
     *            用户选择的图片地址集
     */
    @Override
    protected void OnClickCommitEvent(View v, List<String> datas) {
        if (!datas.isEmpty()) {
            ViewInject.toast("已捕获选择的图片,共有" + datas.size() + "张");
        } else {
            ViewInject.toast("没有选择图片");
        }

        // 同时你还可以操作的控件有父类中的两个控件：mListView（用于显示文件夹列表）
        // mBtnCancel、mBtnOk（用于显示取消与确定按钮）
        // mGridView（用于显示选择的文件夹下的图片列表）
        // 更多介绍请看API文档
    }
}
