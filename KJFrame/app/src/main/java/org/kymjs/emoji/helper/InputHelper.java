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
package org.kymjs.emoji.helper;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.widget.EditText;

import org.kymjs.blog.R;
import org.kymjs.emoji.model.DisplayRules;
import org.kymjs.emoji.model.Emojicon;
import org.kymjs.emoji.model.KJEmojiConfig;

/**
 * 输入相关的工具类
 * 
 * @author kymjs (http://www.kymjs.com)
 */
public class InputHelper {
    public static void backspace(EditText editText) {
        if (editText == null) {
            return;
        }
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
                0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    /**
     * 对emoji控件输入emoji图标
     * 
     * @param editText
     *            待显示输入的控件
     * @param emojicon
     *            待输入的图标
     */
    public static void input(EditText editText, Emojicon emojicon) {
        input(editText, emojicon, KJEmojiConfig.flag_Start,
                KJEmojiConfig.flag_End);
    }

    public static void input(EditText editText, Emojicon emojicon,
            String flagStart, String flagEnd) {
        if (editText == null || emojicon == null) {
            return;
        }
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        Spannable emojiString = displayEmoji(editText.getResources(),
                emojicon.getEmojiStr(), flagStart, flagEnd);
        // 没有多选时，直接在当前光标处添加
        if (start < 0) {
            editText.append(emojiString);
        } else {
            // 将已选中的部分替换为表情(当长按文字时会多选刷中很多文字)
            editText.getText().replace(Math.min(start, end),
                    Math.max(start, end), emojiString, 0, emojiString.length());
        }
    }

    /**
     * 获取name对应的资源
     * 
     * @param name
     * @return
     */
    public static int getEmojiResId(String name) {
        Integer res = DisplayRules.getMapAll().get(name);
        if (res != null) {
            return res.intValue();
        } else {
            return -1;
        }
    }

    /**
     * 
     */
    public static Spannable displayEmoji(Resources res, CharSequence s,
            String flagStart, String flagEnd) {
        String str = s.toString();
        Spannable spannable = null;
        if (s instanceof Spannable) {
            spannable = (Spannable) s;
        } else {
            // 构建文字span
            spannable = new SpannableString(str);
        }
        for (int i = 0; i < str.length(); i++) {
            int index1 = str.indexOf(flagStart, i);
            int length1 = str.indexOf(flagEnd, index1 + 1);
            int bound = (int) res.getDimension(R.dimen.space_19);
            try {
                String emojiStr = str.substring(index1,
                        length1 + flagEnd.length());
                int resId = getEmojiResId(emojiStr);
                if (resId > 0) {
                    // 构建图片span
                    Drawable drawable = res.getDrawable(resId);

                    drawable.setBounds(0, 20, bound, bound + 20);
                    ImageSpan span = new ImageSpan(drawable,
                            ImageSpan.ALIGN_BASELINE);
                    spannable.setSpan(span, index1, length1 + flagEnd.length(),
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
            }
        }
        return spannable;
    }
}
