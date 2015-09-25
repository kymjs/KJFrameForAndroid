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
package org.kymjs.blog.ui.fragment;

import org.kymjs.blog.R;
import org.kymjs.blog.ui.widget.RecordButton;
import org.kymjs.blog.ui.widget.RecordButton.OnFinishedRecordListener;
import org.kymjs.blog.ui.widget.RecordButtonUtil.OnPlayListener;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.utils.DensityUtils;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * 语音动弹发布界面
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class TweetRecordFragment extends TitleBarFragment {

    @BindView(id = R.id.tweet_btn_record)
    RecordButton mBtnRecort;
    @BindView(id = R.id.tweet_time_record)
    TextView mTvTime;
    @BindView(id = R.id.tweet_text_record)
    TextView mTvInputLen;
    @BindView(id = R.id.tweet_edit_record)
    EditText mEtSpeech;
    @BindView(id = R.id.tweet_img_volume)
    ImageView mImgVolume;
    @BindView(id = R.id.tweet_img_add, click = true)
    ImageView mImgAdd;
    @BindView(id = R.id.tweet_layout_record, click = true)
    RelativeLayout mLayout;

    public static int MAX_LEN = 160;

    private final KJBitmap kjb = new KJBitmap();
    private AnimationDrawable drawable; // 录音播放时的动画背景

    // private final String strSpeech = "#语音动弹#";
    private String filePath;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View rootView = inflater.inflate(R.layout.frag_tweet_pub_record,
                container, false);
        return rootView;
    }

    @Override
    protected void setActionBarRes(ActionBarRes actionBarRes) {
        super.setActionBarRes(actionBarRes);
        actionBarRes.title = getString(R.string.str_tweet_pub_record);
        actionBarRes.backImageId = R.drawable.titlebar_back;
        actionBarRes.menuImageId = R.drawable.titlebar_add;
    }

    @Override
    public void onMenuClick() {
        super.onMenuClick();
        Intent intent = new Intent();
        intent.putExtra(TweetFragment.CONTENT_KEY, mEtSpeech.getText()
                .toString());
        if (!StringUtils.isEmpty(filePath)) {
            intent.putExtra(TweetFragment.IMAGEPATH_KEY, filePath);
            outsideAty.setResult(TweetFragment.REQUEST_CODE_IMAGE, intent);
        } else {
            intent.putExtra(TweetFragment.AUDIOPATH_KEY,
                    mBtnRecort.getCurrentAudioPath());
            outsideAty.setResult(TweetFragment.REQUEST_CODE_RECORD, intent);
        }
        outsideAty.finish();
    }

    @Override
    public void onBackClick() {
        super.onBackClick();
        outsideAty.finish();
    }

    @Override
    protected void initWidget(View view) {
        super.initWidget(view);
        RelativeLayout.LayoutParams params = (LayoutParams) mBtnRecort
                .getLayoutParams();
        params.width = DensityUtils.getScreenW(getActivity());
        params.height = (int) (DensityUtils.getScreenH(getActivity()) * 0.4);
        mBtnRecort.setLayoutParams(params);

        mBtnRecort.setOnFinishedRecordListener(new OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int recordTime) {
                mLayout.setVisibility(View.VISIBLE);
                if (recordTime < 10) {
                    mTvTime.setText("0" + recordTime + "\"");
                } else {
                    mTvTime.setText(recordTime + "\"");
                }
                mImgAdd.setVisibility(View.GONE);
                filePath = null;
            }

            @Override
            public void onCancleRecord() {
                mLayout.setVisibility(View.GONE);
            }
        });

        drawable = (AnimationDrawable) mImgVolume.getBackground();
        mBtnRecort.getAudioUtil().setOnPlayListener(new OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mImgVolume.setBackgroundDrawable(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                mImgVolume.setBackgroundDrawable(drawable);
                drawable.start();
            }
        });

        mEtSpeech.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > MAX_LEN) {
                    mTvInputLen.setText("已达到最大长度");
                } else {
                    mTvInputLen.setText("你还可以输入" + (MAX_LEN - s.length())
                            + "个字");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_LEN) {
                    mEtSpeech.setText(s.subSequence(0, MAX_LEN));
                    CharSequence text = mEtSpeech.getText();
                    if (text instanceof Spannable) {
                        Selection.setSelection((Spannable) text, MAX_LEN);
                    }
                }
            }
        });
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.tweet_layout_record:
            mBtnRecort.playRecord();
            break;
        case R.id.tweet_img_add:
            Intent intent;
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                        111);
            } else {
                intent = new Intent(Intent.ACTION_PICK,
                        Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                        111);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                filePath = FileUtils.uri2File(outsideAty, uri)
                        .getAbsolutePath();
                kjb.display(mImgAdd, filePath, 240, 240);
            }
        }
    }
}
