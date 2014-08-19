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
package org.kymjs.aframe.ui.widget;

import org.kymjs.aframe.utils.DensityUtils;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 设置菜单的每一个item<br>
 * 
 * <b>创建时间</b> 2014-5-31
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 */
public class ResideMenuItem extends LinearLayout {
    /** 菜单图标 */
    private ImageView mImgIcon = null;
    /** 菜单名 */
    private TextView mTvTitle = null;

    public ResideMenuItem(Context context) {
        this(context, 0, null);
    }

    public ResideMenuItem(Context context, String title) {
        this(context, 0, title);
    }

    public ResideMenuItem(Context context, int iconRes, int titleRes) {
        super(context);
        initLayout(iconRes, titleRes);
    }

    public ResideMenuItem(Context context, int iconRes, String title) {
        super(context);
        initLayout(iconRes, title);
    }

    private void initLayout(int iconRes, int titleRes) {
        if (titleRes != 0) {
            try { // 资源地址有误
                initLayout(iconRes, getResources().getString(titleRes));
            } catch (NotFoundException e) {
                initLayout(iconRes, null);
            }
        } else {
            initLayout(iconRes, null);
        }
    }

    /**
     * 创建菜单项
     */
    private void initLayout(int iconRes, String title) {
        this.setGravity(LinearLayout.VERTICAL);
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setPadding(0, DensityUtils.dip2px(getContext(), 30), 0, 0);
        if (iconRes != 0) {
            mImgIcon = createIcon();
            this.addView(mImgIcon);
            this.setIcon(iconRes);
        }
        if (title != null) {
            mTvTitle = createTitle();
            this.addView(mTvTitle);
            this.setTitle(title);
        }
    }

    /**
     * 创建图标控件
     */
    private ImageView createIcon() {
        ImageView icon = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = DensityUtils.dip2px(getContext(), 30);
        params.height = params.width;
        icon.setLayoutParams(params);
        icon.setScaleType(ScaleType.CENTER_CROP);
        return icon;
    }

    /**
     * 创建菜单名控件
     */
    private TextView createTitle() {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = DensityUtils.dip2px(getContext(), 10);
        tv.setLayoutParams(params);
        return tv;
    }

    /**
     * 设置菜单图标
     */
    public void setIcon(int icon) {
        if (mImgIcon != null)
            mImgIcon.setImageResource(icon);
    }

    /**
     * 设置菜单名
     */
    public void setTitle(String title) {
        if (mTvTitle != null)
            mTvTitle.setText(title);
    }

    /**
     * 设置菜单文字颜色
     */
    public void setTextColor(int color) {
        if (mTvTitle != null)
            mTvTitle.setTextColor(color);
    }

    /**
     * 设置菜单文字大小
     */
    public void setTextSize(float size) {
        if (mTvTitle != null)
            mTvTitle.setTextSize(size);
    }

    public void setOnClickListener(OnMenuClickListener l) {
        super.setOnClickListener(l);
    }

    public interface OnMenuClickListener extends OnClickListener {
        public abstract void onSlidMenuClick(View v);
    }
}