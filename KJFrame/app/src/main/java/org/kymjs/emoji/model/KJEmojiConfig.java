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
package org.kymjs.emoji.model;


import org.kymjs.blog.R;

/**
 * 配置器
 * 
 * @author kymjs (http://www.kymjs.com)
 */
public class KJEmojiConfig {
    public static final String flag_Start = "[";
    public static final String flag_End = "]";

    public static final int COUNT_IN_PAGE = 20; // 每页显示多少个表情(要减去一个删除符号:例如这里是三行七列)
    public static final int COLUMNS = 7; // 每页显示多少列

    // Emoji表情中的删除按钮的图标
    public static final int DELETE_EMOJI_ID = R.drawable.btn_del;

    // Emoji表情所在的控件中文字大小：默认19sp(要让Emoji和文字一样高才好看)
    public static final int TextSize = 19;

    // Emoji键盘区域的高度，每行表情的高度是120，但是要多加10(上下的高度)
    public static final int SINGLE_TYPE_HEIGHT = 370; // 当只有一种表情的时候，显示三行的高度
    public static final int MORE_TYPE_HEIGHT = 490; // 当有多种表情的时候，显示四行的高度
}
