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
package org.kymjs.blog.adapter;

import java.util.ArrayList;
import java.util.List;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.EverydayMessage;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 每日推送界面列表适配器
 * 
 * @author kymjs (https://www.kymjs.com)
 * @since 2015-3
 */
public class WeChatAdapter extends BaseAdapter {
    private final Context cxt;
    private List<EverydayMessage> datas;
    private final KJBitmap kjb = new KJBitmap();

    public WeChatAdapter(Context cxt, List<EverydayMessage> datas) {
        this.cxt = cxt;
        if (datas == null) {
            datas = new ArrayList<EverydayMessage>(0);
        }
        this.datas = datas;
    }

    public void refresh(List<EverydayMessage> datas) {
        if (datas == null) {
            datas = new ArrayList<EverydayMessage>(0);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

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

    static class ViewHolder {
        ImageView img;
        TextView title;
        RelativeLayout layoutHead;
        TextView singleTitle;
        ImageView singleImg;
        TextView singleDescription;
        RelativeLayout singleLayout;
        LinearLayout root;
        TextView tiem;
    }

    /**
     * 变量命名：多图文消息中分title和item，title指代第一项那个大的列表项，而item表示小的列表项<br>
     * 单图文消息中只有标题，图片，摘要
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;
        EverydayMessage data = datas.get(position);
        int itemCount = 0; // 标题下面的栏目共有多少个
        if (v == null) {
            v = View.inflate(cxt, R.layout.item_list_wechat, null);
            holder = new ViewHolder();
            holder.img = (ImageView) v.findViewById(R.id.item_wechat_img_head);
            holder.title = (TextView) v.findViewById(R.id.item_wechat_tv_head);
            holder.layoutHead = (RelativeLayout) v
                    .findViewById(R.id.item_wechat_layout_head);
            holder.singleTitle = (TextView) v
                    .findViewById(R.id.item_wechat_tv_single);
            holder.singleDescription = (TextView) v
                    .findViewById(R.id.item_wechat_tv_single_content);
            holder.singleImg = (ImageView) v
                    .findViewById(R.id.item_wechat_img_single);
            holder.singleLayout = (RelativeLayout) v
                    .findViewById(R.id.item_wechat_layout_single);
            holder.root = (LinearLayout) v;
            holder.tiem = (TextView) v.findViewById(R.id.item_wechat_time);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
            itemCount = holder.root.getChildCount() - 3;
        }

        if (data.isHasItem()) {
            // 多图文消息
            holder.singleLayout.setVisibility(View.GONE);
            holder.layoutHead.setVisibility(View.VISIBLE);
            holder.title.setText(data.getTitle());
            kjb.display(holder.img, data.getImgUrl());
            initMsgItem(holder.root, itemCount, data);
            holder.layoutHead
                    .setOnClickListener(getItemMessageClickListener(data
                            .getUrl()));
        } else {
            // 单图文消息
            holder.singleLayout.setVisibility(View.VISIBLE);
            holder.layoutHead.setVisibility(View.GONE);
            holder.singleDescription.setText(data.getDescription());
            holder.singleTitle.setText(data.getTitle());
            kjb.display(holder.singleImg, data.getImgUrl());
            holder.singleLayout
                    .setOnClickListener(getItemMessageClickListener(data
                            .getUrl()));
        }
        holder.tiem.setText(StringUtils.friendlyTime(data.getTime()));
        return v;
    }

    /**
     * 初始化多图文消息中下半部分
     */
    private void initMsgItem(LinearLayout root, int itemCount,
            EverydayMessage data) {
        // 已有的layout直接复用修改数据
        for (int i = 0; i < itemCount; i++) {
            RelativeLayout itemView = (RelativeLayout) root.getChildAt(i + 3);
            initItem(data, i, itemView);
        }
        if (data.getUrlList().size() == itemCount) { // 正好容纳
            return;
        } else if (data.getUrlList().size() > itemCount) { // 当需要额外添加item
            for (int i = itemCount; i < data.getUrlList().size(); i++) {
                RelativeLayout itemView = (RelativeLayout) View.inflate(cxt,
                        R.layout.item_wechat_list, null);
                initItem(data, i, itemView);
                root.addView(itemView);
            }
        } else { // 需要移除item
            for (int i = itemCount; i < data.getUrlList().size(); i++) {
                root.removeViewAt(i);
            }
        }
    }

    /**
     * 初始化多图文消息的item
     * 
     * @param data
     * @param i
     * @param itemView
     */
    private void initItem(EverydayMessage data, int i, RelativeLayout itemView) {
        ImageView itemImg = (ImageView) itemView.getChildAt(0);
        TextView itemText = (TextView) itemView.getChildAt(1);
        kjb.display(itemImg, data.getImageUrlList().get(i));
        itemText.setText(data.getTitleList().get(i));
        itemView.setOnClickListener(getItemMessageClickListener(data
                .getUrlList().get(i)));
    }

    /**
     * 当点击一个标题时，跳转到浏览器显示参数地址
     * 
     * @param url
     *            要显示的url
     * @return 点击事件监听器
     */
    private OnClickListener getItemMessageClickListener(final String url) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toBrowser(cxt, url);
            }
        };
    }
}
