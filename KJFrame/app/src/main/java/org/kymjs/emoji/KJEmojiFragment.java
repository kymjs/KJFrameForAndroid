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
package org.kymjs.emoji;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;

import org.kymjs.blog.R;
import org.kymjs.emoji.adapter.EmojiPagerAdapter;
import org.kymjs.emoji.helper.InputHelper;
import org.kymjs.emoji.helper.SoftKeyboardStateHelper;
import org.kymjs.emoji.helper.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import org.kymjs.emoji.model.KJEmojiConfig;

/**
 * 核心展示类
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class KJEmojiFragment extends Fragment implements
        SoftKeyboardStateListener {
    private LinearLayout mRootView;

    private View mEmojiTitle;
    private LinearLayout mEmojiContent;
    private RadioGroup mEmojiBottom;
    private View[] mEmojiTabs;

    private EditText mEt;
    private CheckBox mCBox;
    private ViewPager mEmojiPager;

    private EmojiPagerAdapter adapter;
    private OnSendClickListener listener;
    public static int EMOJI_TAB_CONTENT;

    private SoftKeyboardStateHelper mKeyboardHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = (LinearLayout) inflater.inflate(R.layout.frag_emoji_main,
                container, false);
        initWidget(mRootView);
        return mRootView;
    }

    private void initWidget(View rootView) {
        // title
        mEmojiTitle = rootView.findViewById(R.id.emoji_title);
        mEt = (EditText) mEmojiTitle.findViewById(R.id.emoji_titile_input);
        mCBox = (CheckBox) mEmojiTitle.findViewById(R.id.emoji_title_menu);
        mCBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked) {
                    showEmojiKeyBoard();
                    hideSoftKeyboard();
                } else {
                    hideEmojiKeyBoard();
                    showSoftKeyboard();
                }
            }
        });
        // bottom
        mEmojiBottom = (RadioGroup) rootView.findViewById(R.id.emoji_bottom);
        EMOJI_TAB_CONTENT = mEmojiBottom.getChildCount() - 1; // 减一是因为有一个删除按钮
        mEmojiTabs = new View[EMOJI_TAB_CONTENT];
        if (EMOJI_TAB_CONTENT <= 1) { // 只有一个分类的时候就不显示了
            mEmojiBottom.setVisibility(View.GONE);
        }
        for (int i = 0; i < EMOJI_TAB_CONTENT; i++) {
            mEmojiTabs[i] = mEmojiBottom.getChildAt(i);
            mEmojiTabs[i].setOnClickListener(getBottomBarClickListener(i));
        }
        mEmojiBottom.findViewById(R.id.emoji_bottom_del).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputHelper.backspace(mEt);
                    }
                });

        // content必须放在bottom下面初始化
        mEmojiContent = (LinearLayout) rootView
                .findViewById(R.id.emoji_content);
        LayoutParams params = (LayoutParams) mEmojiContent.getLayoutParams();
        params.height = EMOJI_TAB_CONTENT > 1 ? KJEmojiConfig.MORE_TYPE_HEIGHT
                : KJEmojiConfig.SINGLE_TYPE_HEIGHT;
        mEmojiContent.setLayoutParams(params);
        mEmojiPager = (ViewPager) mEmojiContent.findViewById(R.id.emoji_pager);
        adapter = new EmojiPagerAdapter(getFragmentManager());
        mEmojiPager.setAdapter(adapter);

        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow()
                .getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
        if (getActivity() instanceof OnSendClickListener) {
            listener = (OnSendClickListener) getActivity();
            mEmojiTitle.findViewById(R.id.emoji_title_send).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClickSendButton(mEt.getText());
                        }
                    });
        }

    }

    /**
     * 底部栏点击事件监听器
     * 
     * @param index
     * @return
     */
    private OnClickListener getBottomBarClickListener(final int index) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiPager.setCurrentItem(index);
            }
        };
    }

    /******************************* preference *************************************/

    public void hideAllKeyBoard() {
        hideEmojiKeyBoard();
        hideSoftKeyboard();
    }

    /**
     * 隐藏Emoji并显示软键盘
     */
    public void hideEmojiKeyBoard() {
        mEmojiBottom.setVisibility(View.GONE);
        mEmojiContent.setVisibility(View.GONE);
        mCBox.setChecked(false);
    }

    /**
     * 显示Emoji并隐藏软键盘
     */
    public void showEmojiKeyBoard() {
        mEmojiContent.setVisibility(View.VISIBLE);
        if (EMOJI_TAB_CONTENT > 1) {
            mEmojiBottom.setVisibility(View.VISIBLE);
        }
        mCBox.setChecked(true);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEt.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        mEt.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(mEt,
                InputMethodManager.SHOW_FORCED);
    }

    public View getEmojiTitle() {
        return mEmojiTitle;
    }

    public Editable getTextString() {
        return mEt.getText();
    }

    public boolean isShowEmojiKeyBoard() {
        return mCBox.isChecked();
    }

    /**
     * 当软键盘显示时回调
     */
    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        mEmojiBottom.setVisibility(View.GONE);
        mEmojiContent.setVisibility(View.GONE);
        mCBox.setChecked(false);
    }

    @Override
    public void onSoftKeyboardClosed() {}

    @Override
    public void onStop() {
        super.onStop();
        hideSoftKeyboard();
    }
}
