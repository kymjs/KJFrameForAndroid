/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.ui.fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.utils.DensityUtils;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 多张图片选择效果的Fragment模板<br>
 * 
 * <b>说明</b> 开发者必须实现OnClickCommitEvent(View v)方法，此方法将在用户点击确定按钮时回调，
 * 此时用户选择的图片将存储在List.String类型的checkFile中。 <br>
 * <b>说明</b> 可供开发者定制的组件有：mListView（用于显示文件夹列表） mBtnCancel、mBtnOk（用于显示取消与确定按钮）
 * mGridView（用于显示选择的文件夹下的图片列表）<br>
 * 
 * <b>创建时间</b> 2014-6-23
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public abstract class ChoiceImageTemplate extends BaseFragment {

    // widget
    private RelativeLayout mRootView;
    private RelativeLayout mFileLayout;
    /** 顶部的取消和确定按钮 */
    protected Button mBtnCancel, mBtnOk;
    protected ListView mListView;
    /** 默认显示三列，开发者可自己定制 */
    protected GridView mGridView;
    // data
    private List<FolderBean> datas = null;
    /** 用户选中的图片的地址集合（结果集） */
    protected List<String> checkFile = null;

    KJBitmap kjb = KJBitmap.create();

    @Override
    protected final View inflaterView(LayoutInflater inflater,
            ViewGroup container, Bundle bundle) {
        // 创建view
        mRootView = new RelativeLayout(getActivity());
        mListView = new ListView(getActivity());
        mFileLayout = new RelativeLayout(getActivity());
        mBtnCancel = new Button(getActivity());
        mBtnOk = new Button(getActivity());
        mGridView = new GridView(getActivity());
        // 设置取消按钮的属性
        RelativeLayout.LayoutParams cancelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancelParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        cancelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        cancelParams.leftMargin = 20;
        cancelParams.topMargin = 10;
        mBtnCancel.setLayoutParams(cancelParams);
        mFileLayout.addView(mBtnCancel);
        // 设置确定按钮的属性
        RelativeLayout.LayoutParams okParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        okParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        okParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        okParams.rightMargin = 20;
        okParams.topMargin = 10;
        mBtnOk.setLayoutParams(okParams);
        mBtnOk.setId(0x37213721);
        mFileLayout.addView(mBtnOk);
        // 设置gridView的属性
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        gridParams.addRule(RelativeLayout.BELOW, 0x37213721);
        gridParams.topMargin = 5;
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mGridView.setLayoutParams(gridParams);
        mGridView.setGravity(Gravity.CENTER);
        mFileLayout.addView(mGridView);
        mFileLayout.setVisibility(View.GONE);
        // 加入跟布局
        mRootView.addView(mListView);
        mRootView.addView(mFileLayout);
        mRootView.setBackgroundColor(0xffffffff);
        return mRootView;
    }

    @Override
    protected void initData() {
        super.initData();
        datas = ChoiceImageUtil.LocalImgFileList(getActivity());
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mListView.setAdapter(new FolderListAdapter());
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                mListView.setVisibility(View.GONE);
                mFileLayout.setVisibility(View.VISIBLE);
                mGridView.setAdapter(new FileGridAdapter(datas.get(position)
                        .getFilePath()));
            }
        });
        mGridView.setNumColumns(3);
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGridView.setVerticalSpacing(15);
        mGridView.setHorizontalSpacing(15);
        mBtnOk.setText("确定");
        mBtnCancel.setText("取消");
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        if (v == mBtnCancel) {
            mFileLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else if (v == mBtnOk) {
            if (checkFile != null) {
                OnClickCommitEvent(v);
            }
        }
    }

    /**
     * 用户选中图片点击确定后将会回调
     * 
     * @param v
     */
    abstract protected void OnClickCommitEvent(View v);

    /*********************** ListView（文件夹）适配器部分 ***********************/

    private static class ListViewHolder {
        ImageView img;
        TextView tv_folder;
        TextView tv_count;
    }

    private class FolderListAdapter extends BaseAdapter {
        // widget
        private LinearLayout itemView = null;
        private ImageView itemImg;
        private TextView itemTvFolder;
        private TextView itemTvCount;

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder holder = null;
            if (convertView == null) {
                initItemLayout();
                convertView = itemView;
                holder = new ListViewHolder();
                holder.img = itemImg;
                holder.tv_count = itemTvCount;
                holder.tv_folder = itemTvFolder;
                convertView.setTag(holder);
            } else {
                holder = (ListViewHolder) convertView.getTag();
            }
            kjb.display(holder.img, datas.get(position).getFilePath().get(0),
                    80, 80);
            holder.tv_count.setText("共有"
                    + datas.get(position).getFilePath().size() + "张图片");
            holder.tv_folder.setText(datas.get(position).getFolderName());
            return convertView;
        }

        /**
         * 创建并初始化一个item布局
         */
        private void initItemLayout() {
            itemView = new LinearLayout(getActivity());
            itemView.setOrientation(LinearLayout.HORIZONTAL);
            itemView.setPadding(10, 10, 10, 10);
            itemImg = new ImageView(getActivity());
            itemImg.setScaleType(ScaleType.CENTER_CROP);
            LinearLayout textLayout = new LinearLayout(getActivity());
            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setPadding(5, 5, 5, 5);
            itemTvFolder = new TextView(getActivity());
            itemTvFolder.setTextSize(18F);
            itemTvCount = new TextView(getActivity());
            itemTvCount.setTextColor(0xff999999);
            textLayout.addView(itemTvFolder);
            textLayout.addView(itemTvCount);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    80, 80);
            params.width = params.height = 80;
            itemImg.setLayoutParams(params);
            itemView.addView(itemImg);
            itemView.addView(textLayout);
        }
    }

    /*********************** GridView（图片文件）适配器部分 ***********************/
    private static class GridViewHolder {
        ImageView img;
        CheckBox cBox;
    }

    private class FileGridAdapter extends BaseAdapter {
        private List<String> fileDatas = null;

        // widget
        private RelativeLayout itemView = null;
        private ImageView itemImg;
        private CheckBox itemCbox;

        public FileGridAdapter(List<String> datas) {
            checkFile = new LinkedList<String>();
            fileDatas = datas;
            if (fileDatas == null) {
                fileDatas = new ArrayList<String>();
            }
        }

        @Override
        public int getCount() {
            return fileDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return fileDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent) {
            final GridViewHolder holder;
            if (convertView == null) {
                initItemLayout();
                convertView = itemView;
                holder = new GridViewHolder();
                holder.img = itemImg;
                holder.cBox = itemCbox;
                convertView.setTag(holder);
            } else {
                holder = (GridViewHolder) convertView.getTag();
            }
            kjb.display(holder.img, fileDatas.get(position), 80, 80);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cBox.setChecked(!holder.cBox.isChecked());
                    if (holder.cBox.isChecked()) {
                        checkFile.add(fileDatas.get(position));
                    } else {
                        checkFile.remove(fileDatas.get(position));
                    }
                }
            });
            return convertView;
        }

        private void initItemLayout() {
            itemView = new RelativeLayout(getActivity());
            itemImg = new ImageView(getActivity());
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            imgParams.width = imgParams.height = (DensityUtils
                    .getScreenW(getActivity()) - 80) / 3;
            imgParams.rightMargin = imgParams.leftMargin = 5;
            imgParams.bottomMargin = imgParams.topMargin = 5;
            imgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            itemImg.setLayoutParams(imgParams);
            itemImg.setScaleType(ScaleType.CENTER_CROP);
            itemCbox = new CheckBox(getActivity());
            itemCbox.setFocusable(false);
            itemCbox.setClickable(false);
            itemCbox.setChecked(false);
            RelativeLayout.LayoutParams cboxParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            cboxParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            cboxParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            cboxParams.rightMargin = 3;
            itemCbox.setLayoutParams(cboxParams);
            itemView.addView(itemImg);
            itemView.addView(itemCbox);
        }
    }
}
