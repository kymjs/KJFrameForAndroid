/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.blog.ui.fragment;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.NotebookData;
import org.kymjs.blog.ui.SimpleBackActivity;
import org.kymjs.blog.utils.KJAnimations;
import org.kymjs.kjframe.KJDB;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.StringUtils;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 便签编辑
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class NoteEditFragment extends TitleBarFragment implements
        OnTouchListener {

    @BindView(id = R.id.note_detail_edit)
    EditText mEtContent;
    @BindView(id = R.id.note_detail_tv_date)
    TextView mTvDate;
    @BindView(id = R.id.note_detail_titlebar)
    RelativeLayout mLayoutTitle;
    @BindView(id = R.id.note_detail_img_thumbtack)
    ImageView mImgThumbtack;

    @BindView(id = R.id.note_detail_img_button)
    ImageView mImgMenu;
    @BindView(id = R.id.note_detail_menu)
    RelativeLayout mLayoutMenu;

    @BindView(id = R.id.note_detail_img_green, click = true)
    ImageView mImgGreen;
    @BindView(id = R.id.note_detail_img_blue, click = true)
    ImageView mImgBlue;
    @BindView(id = R.id.note_detail_img_purple, click = true)
    ImageView mImgPurple;
    @BindView(id = R.id.note_detail_img_yellow, click = true)
    ImageView mImgYellow;
    @BindView(id = R.id.note_detail_img_red, click = true)
    ImageView mImgRed;

    private NotebookData editData;
    public static final String NOTE_KEY = "notebook_key";

    private KJDB kjdb;

    public static final int[] sBackGrounds = { 0xffe5fce8,// 绿色
            0xfffffdd7,// 黄色
            0xffffddde,// 红色
            0xffccf2fd,// 蓝色
            0xfff7f5f6,// 紫色
    };
    public static final int[] sTitleBackGrounds = { 0xffcef3d4,// 绿色
            0xffebe5a9,// 黄色
            0xffecc4c3,// 红色
            0xffa9d5e2,// 蓝色
            0xffddd7d9,// 紫色
    };

    public static final int[] sThumbtackImgs = { R.drawable.green,
            R.drawable.yellow, R.drawable.red, R.drawable.blue,
            R.drawable.purple };

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_note_detail,
                container, false);
        return rootView;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.sticies);
        actionBarRes.menuImageId = R.drawable.titlebar_done;
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.note_detail_img_green:
            editData.setColor(0);
            break;
        case R.id.note_detail_img_blue:
            editData.setColor(3);
            break;
        case R.id.note_detail_img_purple:
            editData.setColor(4);
            break;
        case R.id.note_detail_img_yellow:
            editData.setColor(1);
            break;
        case R.id.note_detail_img_red:
            editData.setColor(2);
            break;
        }
        mImgThumbtack.setImageResource(sThumbtackImgs[editData.getColor()]);
        mEtContent.setBackgroundColor(sBackGrounds[editData.getColor()]);
        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[editData.getColor()]);
        closeMenu();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            if (mLayoutMenu.getVisibility() == View.GONE) {
                openMenu();
            } else {
                closeMenu();
            }
        }
        return true;
    }

    @Override
    public void initData() {
        Bundle bundle = ((SimpleBackActivity) getActivity()).getBundleData();
        if (bundle != null) {
            editData = (NotebookData) bundle.getSerializable(NOTE_KEY);
        }

        kjdb = KJDB.create(outsideAty, true);
        if (editData == null) {
            editData = new NotebookData();
        }
        if (StringUtils.isEmpty(editData.getDate())) {
            editData.setDate(StringUtils.getDataTime("yyyy/MM/dd"));
        }
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mEtContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mEtContent.setSingleLine(false);
        mEtContent.setHorizontallyScrolling(false);
        mEtContent.setText(editData.getContent());
        mTvDate.setText(editData.getDate());

        mEtContent.setBackgroundColor(sBackGrounds[editData.getColor()]);
        mLayoutTitle.setBackgroundColor(sTitleBackGrounds[editData.getColor()]);
        mImgThumbtack.setImageResource(sThumbtackImgs[editData.getColor()]);

        mImgMenu.setOnTouchListener(this);
        mLayoutMenu.setOnTouchListener(this);
    }

    /**
     * 切换便签颜色的菜单
     */
    private void openMenu() {
        KJAnimations.openAnimation(mLayoutMenu, mImgMenu, 500);
    }

    /**
     * 切换便签颜色的菜单
     */
    private void closeMenu() {
        KJAnimations.closeAnimation(mLayoutMenu, mImgMenu, 500);
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        if (!StringUtils.isEmpty(mEtContent.getText().toString())) {
            save();
        }
        outsideAty.finish();
    }

    /**
     * 保存已编辑内容到数据库
     */
    private void save() {
        editData.setContent(mEtContent.getText().toString());
        if (editData.getId() != 0) {
            kjdb.update(editData);
        } else {
            kjdb.save(editData);
        }
    }
}
