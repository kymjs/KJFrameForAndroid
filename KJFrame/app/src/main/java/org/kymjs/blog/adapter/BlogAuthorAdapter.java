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
import org.kymjs.blog.domain.BlogAuthor;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import android.view.View;
import android.widget.AbsListView;

/**
 * 大神列表
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class BlogAuthorAdapter extends KJAdapter<BlogAuthor> {

    private final KJBitmap kjb = new KJBitmap();

    public BlogAuthorAdapter(AbsListView view, List<BlogAuthor> mDatas,
            int itemLayoutId) {
        super(view, mDatas, itemLayoutId);
    }

    @Override
    public void convert(AdapterHolder helper, BlogAuthor item,
            boolean isScrolling) {
        helper.setText(R.id.item_blogauthor_tv_name, item.getDescription());
        helper.setText(R.id.item_blogauthor_tv_desc, item.getName());

        View view = helper.getView(R.id.item_blogauthor_head);
        if (isScrolling) {
            kjb.displayCacheOrDefult(view, item.getHead(),
                    R.drawable.default_head);
        } else {
            kjb.display(view, item.getHead(), 150, 150);
        }
    }
}
