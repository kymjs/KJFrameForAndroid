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

import java.util.Collection;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.OSCBlog;
import org.kymjs.blog.utils.TimeUtils;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * OSC博客适配器
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class OSCBlogAdapter extends KJAdapter<OSCBlog> {

    public OSCBlogAdapter(AbsListView view, Collection<OSCBlog> mDatas,
            int itemLayoutId) {
        super(view, mDatas, itemLayoutId);
    }

    static class ViewHolder {
        ImageView img_tody;
        ImageView img_recommend;
        TextView tv_title;
        TextView tv_description;
        TextView tv_author;
        TextView tv_time;
    }

    @Override
    public void convert(AdapterHolder helper, OSCBlog item, boolean isScrolling) {
        View img_tody = helper.getView(R.id.item_blog_tip_tody);
        View img_recommend = helper.getView(R.id.item_blog_tip_recommend);
        if (TimeUtils.dateIsTody(item.getPubDate())) {
            img_tody.setVisibility(View.VISIBLE);
            img_recommend.setVisibility(View.GONE);
        } else {
            img_tody.setVisibility(View.GONE);
            img_recommend.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.item_blog_tv_author, item.getAuthorname());
        helper.setText(R.id.item_blog_tv_date,
                StringUtils.friendlyTime(item.getPubDate()));

        String content = item.getTitle();
        int boundary = content.indexOf("——");
        if (boundary > 0) {
            helper.setText(R.id.item_blog_tv_title,
                    content.substring(boundary + 2));
            helper.setText(R.id.item_blog_tv_description,
                    content.substring(0, boundary));
        } else {
            helper.setText(R.id.item_blog_tv_title, content);
            helper.setText(R.id.item_blog_tv_description, item.getAuthorname()
                    + "的博客");
        }

    }
}
