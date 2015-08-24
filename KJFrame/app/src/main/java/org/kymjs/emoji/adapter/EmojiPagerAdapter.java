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

import org.kymjs.emoji.EmojiPageFragment;
import org.kymjs.emoji.KJEmojiFragment;
import org.kymjs.emoji.model.DisplayRules;
import org.kymjs.emoji.model.KJEmojiConfig;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 表情页适配器（FragmentPagerAdapter的好处是fragment常驻内存，对于要求效率而页卡很少的表情控件最合适）
 * 
 * @author kymjs (http://www.kymjs.com)
 */
public class EmojiPagerAdapter extends FragmentPagerAdapter {

    public EmojiPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public EmojiPageFragment getItem(int index) {
        if (KJEmojiFragment.EMOJI_TAB_CONTENT > 1) {
            return new EmojiPageFragment(index, index);
        } else {
            return new EmojiPageFragment(index, 0);
        }
    }

    /**
     * 显示模式：如果只有一种Emoji表情，则像QQ表情一样左右滑动分页显示<br>
     * 如果有多种Emoji表情，每页显示一种，Emoji筛选时上下滑动筛选。
     */
    @Override
    public int getCount() {
        if (KJEmojiFragment.EMOJI_TAB_CONTENT > 1) {
            return KJEmojiFragment.EMOJI_TAB_CONTENT;
        } else {
            // 采用进一法取小数
            return (DisplayRules.getAllByType(0).size() - 1 + KJEmojiConfig.COUNT_IN_PAGE)
                    / KJEmojiConfig.COUNT_IN_PAGE;
        }
    }
}
