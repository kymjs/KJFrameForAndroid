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
import org.kymjs.blog.domain.CollectData;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import android.widget.AbsListView;

/**
 * 收藏列表适配器
 * 
 * @author kymjs (http://www.kymjs.com/)
 */
public class CollectAdapter extends KJAdapter<CollectData> {

    public CollectAdapter(AbsListView view, Collection<CollectData> mDatas,
            int itemLayoutId) {
        super(view, mDatas, itemLayoutId);
    }

    @Override
    public void convert(AdapterHolder helper, CollectData item,
            boolean isScrolling) {
        helper.setText(R.id.item_collect_title, item.getName());
        helper.setText(R.id.item_collect_url, item.getUrl());
    }
}
