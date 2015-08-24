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
package org.kymjs.emoji.adapter;

import java.util.ArrayList;
import java.util.List;

import org.kymjs.emoji.model.Emojicon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 表情适配器
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class EmojiGridAdapter extends BaseAdapter {

    private List<Emojicon> datas;
    private final Context cxt;

    public EmojiGridAdapter(Context cxt, List<Emojicon> datas) {
        this.cxt = cxt;
        if (datas == null) {
            datas = new ArrayList<Emojicon>(0);
        }
        this.datas = datas;
    }

    public void refresh(List<Emojicon> datas) {
        if (datas == null) {
            datas = new ArrayList<Emojicon>(0);
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
        return 0;
    }

    private static class ViewHolder {
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = new ImageView(cxt);
            LayoutParams params = new LayoutParams(120, 120);
            convertView.setLayoutParams(params);
            convertView.setPadding(10, 10, 10, 10);
            holder.image = (ImageView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageResource(datas.get(position).getResId());
        return convertView;
    }
}
