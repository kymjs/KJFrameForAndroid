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
import org.kymjs.blog.domain.Tweet;
import org.kymjs.blog.ui.widget.CollapsibleTextView;
import org.kymjs.blog.utils.UIHelper;
import org.kymjs.emoji.helper.InputHelper;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.utils.StringUtils;
import org.kymjs.kjframe.widget.AdapterHolder;
import org.kymjs.kjframe.widget.KJAdapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;

/**
 * 动弹列表适配器
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class TweetAdapter extends KJAdapter<Tweet> {

    public TweetAdapter(AbsListView view, Collection<Tweet> mDatas,
            int itemLayoutId) {
        super(view, mDatas, itemLayoutId);
    }

    private final KJBitmap kjb = new KJBitmap();

    @Override
    public void convert(AdapterHolder helper, Tweet item, boolean isScrolling) {
        // 由于头像地址默认加了一段参数需要去掉
        String headUrl = item.getPortrait();
        int end = headUrl.indexOf('?');
        if (end > 0) {
            headUrl = headUrl.substring(0, end);
        }

        if (isScrolling) {
            kjb.displayCacheOrDefult(helper.getView(R.id.msg_item_img_head),
                    headUrl, R.drawable.default_head);
        } else {
            kjb.display(helper.getView(R.id.msg_item_img_head), headUrl, 135,
                    135, R.drawable.default_head);
        }
        helper.setText(R.id.msg_item_text_uname, item.getAuthor());
        CollapsibleTextView content = helper
                .getView(R.id.msg_item_text_content);
        content.setText(InputHelper.displayEmoji(content.getResources(),
                item.getBody(), "[", "]"));

        helper.setText(R.id.msg_item_text_time,
                StringUtils.friendlyTime(item.getPubDate()));

        View image = helper.getView(R.id.msg_item_img);
        if (StringUtils.isEmpty(item.getImgBig())) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            onPicClick(image, item.getImgBig());
            if (isScrolling) {
                kjb.displayCacheOrDefult(image, item.getImgBig(),
                        R.drawable.pic_bg);
            } else {
                kjb.displayWithLoadBitmap(image, item.getImgBig(),
                        R.drawable.pic_bg);
            }
        }
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
