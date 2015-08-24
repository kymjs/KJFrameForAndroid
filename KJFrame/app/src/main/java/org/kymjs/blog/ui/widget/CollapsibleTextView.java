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
package org.kymjs.blog.ui.widget;

import org.kymjs.kjframe.utils.DensityUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

/**
 * 
 * 可收缩展开的TextView
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class CollapsibleTextView extends LinearLayout {

    public enum CollapsibleState {
        NONE, // 默认
        SPREAD, // 展开
        SHRINKUP // 收缩
    };

    // constant
    private static final int MAX_LINE = 4;

    // widget
    private final TextView mTvContent;
    private final TextView mTvFlag;

    // data
    private CollapsibleState mState = CollapsibleState.NONE;
    private boolean flag = false; // 提高效率，若被多次调用，只执行一次
    private InnerRunnable thread; // 必须在线程中判断
    private String shrinkup = "收起";
    private String spread = "全文";

    public CollapsibleTextView(Context context) {
        this(context, null);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // from code to initialization layout
        this.setOrientation(VERTICAL);
        this.setPadding(0, -1, 0, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.topMargin = DensityUtils.dip2px(context, 4);
        mTvContent = new TextView(context);
        mTvContent.setLayoutParams(params);
        mTvContent.setTextColor(0xff000000);
        mTvContent.setTextSize(17);
        mTvFlag = new TextView(context);
        mTvFlag.setLayoutParams(params);
        mTvFlag.setGravity(Gravity.LEFT);
        mTvFlag.setFocusable(false);
        mTvFlag.setSingleLine();
        mTvFlag.setTextColor(0xff576b95);
        mTvFlag.setTextSize(17);
        mTvFlag.setVisibility(View.GONE);
        mTvFlag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                requestLayout();
            }
        });
        this.addView(mTvContent);
        this.addView(mTvFlag);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mTvContent.getLineCount() <= MAX_LINE) {
            flag = false;
        }
        if (!flag) {
            // 若本身就没达到最大行数，则显示全部，隐藏标识text
            if (mTvContent.getLineCount() <= MAX_LINE) {
                mTvContent.setMaxLines(Integer.MAX_VALUE);
                mState = CollapsibleState.NONE;
                mTvFlag.setVisibility(View.GONE);
            } else {
                flag = true;
                if (thread == null) {
                    thread = new InnerRunnable();
                }
                post(thread);
            }
        }
    }

    public final void setText(CharSequence charSequence) {
        mTvContent.setText(charSequence, BufferType.NORMAL);
        mState = CollapsibleState.SHRINKUP;
        // 要求父控件重新调用本控件的onMeasure onLayout方法重新测量
        requestLayout();
    }

    public void setTextSize(float size) {
        mTvContent.setTextSize(size);
    }

    public void setTextColor(int color) {
        mTvContent.setTextColor(color);
    }

    public void setFlagText(String on, String off) {
        shrinkup = off;
        spread = on;
    }

    /**
     * TextView行数必须在线程里面判断（view显示以后才能判断）
     */
    class InnerRunnable implements Runnable {
        @Override
        public void run() {
            if (mState == CollapsibleState.SHRINKUP) { // 当前状态是收缩，显示全文按钮
                mTvContent.setMaxLines(MAX_LINE);
                mTvFlag.setVisibility(View.VISIBLE);
                mTvFlag.setText(spread);
                mState = CollapsibleState.SPREAD;
            } else if (mState == CollapsibleState.SPREAD) { // 当前状态是展开，显示收缩按钮
                mTvContent.setMaxLines(Integer.MAX_VALUE);
                mTvFlag.setVisibility(View.VISIBLE);
                mTvFlag.setText(shrinkup);
                mState = CollapsibleState.SHRINKUP;
            }
        }
    }
}
