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
import java.util.Collections;
import java.util.List;

import org.kymjs.blog.AppContext;
import org.kymjs.blog.R;
import org.kymjs.blog.domain.NotebookData;
import org.kymjs.blog.ui.fragment.NoteEditFragment;
import org.kymjs.blog.ui.widget.KJDragGridView.DragGridBaseAdapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 便签列表适配器
 * 
 * @author kymjs (https://www.kymjs.com/)
 * 
 */
public class NotebookAdapter extends BaseAdapter implements DragGridBaseAdapter {
    private List<NotebookData> datas;
    private final Activity aty;
    private int currentHidePosition = -1;
    private final int width;
    private final int height;
    private boolean dataChange = false;

    public NotebookAdapter(Activity aty, List<NotebookData> datas) {
        super();
        if (datas == null) {
            datas = new ArrayList<NotebookData>(1);
        }
        this.datas = datas;
        this.aty = aty;
        width = AppContext.screenW / 2;
        height = (int) aty.getResources().getDimension(R.dimen.space_35);
    }

    public void refurbishData(List<NotebookData> datas) {
        if (datas == null) {
            datas = new ArrayList<NotebookData>(1);
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

    public List<NotebookData> getDatas() {
        return datas;
    }

    /**
     * 数据是否发生了改变
     * 
     * @return
     */
    public boolean getDataChange() {
        return dataChange;
    }

    static class ViewHolder {
        TextView date;
        ImageView state;
        ImageView thumbtack;
        View titleBar;
        TextView content;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        NotebookData data = datas.get(position);

        ViewHolder holder = null;
        if (v == null) {
            holder = new ViewHolder();
            v = View.inflate(aty, R.layout.item_notebook, null);
            holder.titleBar = v.findViewById(R.id.item_note_titlebar);
            holder.date = (TextView) v.findViewById(R.id.item_note_tv_date);
            holder.state = (ImageView) v.findViewById(R.id.item_note_img_state);
            holder.thumbtack = (ImageView) v
                    .findViewById(R.id.item_note_img_thumbtack);
            holder.content = (TextView) v.findViewById(R.id.item_note_content);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        RelativeLayout.LayoutParams params = (LayoutParams) holder.content
                .getLayoutParams();
        params.width = width;
        params.height = (params.width - height);
        holder.content.setLayoutParams(params);

        holder.titleBar
                .setBackgroundColor(NoteEditFragment.sTitleBackGrounds[data
                        .getColor()]);
        holder.date.setText(data.getDate());
        if (data.getId() > 0) {
            holder.state.setVisibility(View.GONE);
        } else {
            holder.state.setVisibility(View.VISIBLE);
        }
        holder.thumbtack.setImageResource(NoteEditFragment.sThumbtackImgs[data
                .getColor()]);
        holder.content.setText(Html.fromHtml(data.getContent()).toString());
        holder.content.setBackgroundColor(NoteEditFragment.sBackGrounds[data
                .getColor()]);
        if (position == currentHidePosition) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        dataChange = true;
        if (oldPosition >= datas.size() || oldPosition < 0) {
            return;
        }
        NotebookData temp = datas.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(datas, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(datas, i, i - 1);
            }
        }
        datas.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.currentHidePosition = hidePosition;
        notifyDataSetChanged();
    }
}
