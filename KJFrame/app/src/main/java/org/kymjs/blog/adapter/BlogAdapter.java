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

import java.util.List;

import org.kymjs.blog.R;
import org.kymjs.blog.domain.Blog;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;

/**
 * 主界面博客模块列表适配器
 * 
 * @author kymjs (https://www.kymjs.com/)
 * @since 2015-3
 */
public class BlogAdapter extends KJAdapter<Blog> {
    private final KJBitmap kjb = new KJBitmap();

    public BlogAdapter(AbsListView view, List<Blog> mDatas) {
        super(view, mDatas, R.layout.item_list_blog);
    }

    @Override
    public void convert(AdapterHolder helper, Blog item, boolean isScrolling) {
        helper.getView(R.id.item_blog_tip_recommend).setVisibility(
                item.getIsRecommend() == 0 ? View.GONE : View.VISIBLE);
        helper.getView(R.id.item_blog_tip_tody).setVisibility(
                item.getIsAuthor() == 0 ? View.GONE : View.VISIBLE);
        ImageView image = helper.getView(R.id.item_blog_img);
        String url = item.getImageUrl();
        if (StringUtils.isEmpty(url)) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            onPicClick(image, url);
            if (isScrolling) {
                kjb.displayCacheOrDefult(image, url, R.drawable.pic_bg);
            } else {
                kjb.display(image, url, 480, 420, R.drawable.pic_bg);
            }
        }
        helper.setText(R.id.item_blog_tv_title, item.getTitle());
        helper.setText(R.id.item_blog_tv_description, item.getDescription());
        helper.setText(R.id.item_blog_tv_author, "张涛");
        helper.setText(R.id.item_blog_tv_date,
                StringUtils.friendlyTime(item.getDate()));
    }

    private void onPicClick(View view, final String url) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.toGallery(v.getContext(), url);
            }
        });
    }

}
