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

import java.io.File;
import java.lang.ref.WeakReference;

import org.kymjs.blog.R;
import org.kymjs.blog.ui.widget.RecordButtonUtil.OnPlayListener;
import org.kymjs.blog.utils.KJAnimations;
import org.kymjs.kjframe.ui.ViewInject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 录音专用Button，可弹出自定义的录音dialog。需要配合{@link #RecordButtonUtil}使用
 * 
 * @author kymjs(kymjs123@gmail.com)
 * 
 */
public class RecordButton extends RelativeLayout {
    private static final int MIN_INTERVAL_TIME = 900; // 录音最短时间(毫秒)
    private static final int MAX_INTERVAL_TIME = 60; // 录音最长时间（秒）
    private static final int HANDLE_FLAG = 33333721;// 用作massage的标示

    private ImageView mImgPlay;
    private ImageView mImgListen;
    private ImageView mImgDelete;
    private int mLeftButtonX = 0; // 左边界值（mImgListen右值）
    private int mRightButtonX = 0; // 右边界值（mImgDelete左值）

    private View bottomFlag;
    private View topFlag;
    private TextView mTvRecordTime;
    private ImageView mImgVolume;

    private long mStartTime;// 录音起始时间
    private String mAudioFile = null;
    private boolean mIsCancel = false; // 手指抬起或划出时判断是否主动取消录音
    private boolean mTouchInPlayButton = false; // 手指是按在录音按钮上而不是空白区域
    private OnFinishedRecordListener mFinishedListerer;

    private RecordButtonUtil mAudioUtil;
    private ObtainDecibelThread mThread;
    private Handler mVolumeHandler; // 用于更新录音音量大小的图片

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mVolumeHandler = new ShowVolumeHandler(this);
        mAudioUtil = new RecordButtonUtil();
        initSavePath();
        LayoutInflater.from(getContext()).inflate(R.layout.record_view, this);
        mImgDelete = (ImageView) findViewById(R.id.recordview_delete);
        mImgListen = (ImageView) findViewById(R.id.recordview_listen);
        mImgPlay = (ImageView) findViewById(R.id.recordview_start);
        bottomFlag = findViewById(R.id.recordview_text);
        topFlag = findViewById(R.id.recordview_layout);
        mTvRecordTime = (TextView) findViewById(R.id.recordview_text_time);
        mImgVolume = (ImageView) findViewById(R.id.recordview_img_volume);
        initPlayButtonEvent();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAudioFile == null) {
            return false;
        }
        if (!mTouchInPlayButton) {
            return false;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            initBorderLine();
            break;
        case MotionEvent.ACTION_MOVE:
            if (event.getY() < 0) {
                viewToInit();
                break;
            }
            if (event.getX() > mRightButtonX) {
                mIsCancel = true;
                scaleView(mImgDelete, 1.5f);
            } else if (event.getX() < mLeftButtonX) {
                scaleView(mImgListen, 1.5f);
            } else {
                mIsCancel = false;
                viewToInit();
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mIsCancel || event.getY() < -50) {
                cancelRecord();
            } else if (event.getX() < mLeftButtonX) {// 试听
                playRecord();
                finishRecord();
            } else if (event.getX() > mRightButtonX) {// 删除
                cancelRecord();
            } else {
                finishRecord();
            }
            viewToInit();
            bottomFlag.setVisibility(View.VISIBLE);
            topFlag.setVisibility(View.GONE);
            mIsCancel = false;
            mTouchInPlayButton = false;
            break;
        }
        return true;
    }

    /****************************** ui method ******************************/

    private void initPlayButtonEvent() {
        mImgDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRecord();
            }
        });

        mImgListen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
            }
        });

        mImgPlay.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mImgPlay.startAnimation(KJAnimations.clickAnimation(0.8f,
                            400));
                    initRecorder();
                    bottomFlag.setVisibility(View.GONE);
                    mTvRecordTime.setText("0\"");
                    topFlag.setVisibility(View.VISIBLE);
                    mTouchInPlayButton = true;
                }
                return false;
            }
        });
    }

    /**
     * 需要等控件显示了以后才能调用
     */
    private void initBorderLine() {
        int[] xy = new int[2];
        mImgListen.getLocationInWindow(xy);
        mLeftButtonX = xy[0] + mImgListen.getWidth();
        mImgDelete.getLocationInWindow(xy);
        mRightButtonX = xy[0];
    }

    private void viewToInit() {
        scaleView(mImgDelete, 1f);
        scaleView(mImgListen, 1f);
    }

    @SuppressLint("NewApi")
    private void scaleView(View view, float scaleXY) {
        if (android.os.Build.VERSION.SDK_INT > 10) {
            view.setScaleX(scaleXY);
            view.setScaleY(scaleXY);
        }
    }

    private void changeVolume(int volume) {
        switch (volume) {
        case 0:
        case 1:
        case 2:
            mImgVolume.setImageResource(R.drawable.audio0);
            break;
        case 3:
            mImgVolume.setImageResource(R.drawable.audio1);
            break;
        case 4:
            mImgVolume.setImageResource(R.drawable.audio2);
            break;
        case 5:
            mImgVolume.setImageResource(R.drawable.audio3);
            break;
        }
    }

    /****************************** ui method end ******************************/

    /**
     * 调用该方法设置录音文件存储点
     */
    private void initSavePath() {
        mAudioFile = RecordButtonUtil.AUDOI_DIR;
        File file = new File(mAudioFile);
        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioFile += File.separator + System.currentTimeMillis() + ".amr";
    }

    /**
     * 初始化 dialog和录音器
     */
    private void initRecorder() {
        mStartTime = System.currentTimeMillis();
        startRecording();
    }

    /**
     * 获取当前已经录音的秒数
     */
    private int getRecordTime() {
        return (int) ((System.currentTimeMillis() - mStartTime) / 1000);
    }

    /**
     * 录音完成（达到最长时间或用户决定录音完成）
     */
    private void finishRecord() {
        stopRecording();
        long intervalTime = System.currentTimeMillis() - mStartTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            ViewInject.toast("声音太短啦，听不清");
            File file = new File(mAudioFile);
            file.delete();
            if (mFinishedListerer != null) {
                mFinishedListerer.onCancleRecord();
            }
            return;
        } else {
            if (mFinishedListerer != null) {
                mFinishedListerer.onFinishedRecord(mAudioFile, getRecordTime());
            }
        }
    }

    // 用户手动取消录音
    private void cancelRecord() {
        stopRecording();
        File file = new File(mAudioFile);
        file.delete();
        if (mFinishedListerer != null) {
            mFinishedListerer.onCancleRecord();
        }
    }

    // 开始录音
    private void startRecording() {
        mAudioUtil.setAudioPath(mAudioFile);
        mAudioUtil.recordAudio();
        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    // 停止录音
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        if (mAudioUtil != null) {
            mAudioUtil.stopRecord();
        }
    }

    /******************************* public method ****************************************/

    public RecordButtonUtil getAudioUtil() {
        return mAudioUtil;
    }

    public void playRecord() {
        mAudioUtil.startPlay();
    }

    /**
     * 获取最近一次录音的文件路径
     * 
     * @return
     */
    public String getCurrentAudioPath() {
        return mAudioFile;
    }

    /**
     * 设置要播放的声音的路径
     * 
     * @param path
     */
    public void setAudioPath(String path) {
        mAudioUtil.setAudioPath(path);
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        mAudioUtil.startPlay();
    }

    /**
     * 删除当前文件
     */
    public void delete() {
        File file = new File(mAudioFile);
        file.delete();
    }

    /**
     * 结束录音的监听器
     * 
     * @param listener
     */
    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        mFinishedListerer = listener;
    }

    /**
     * 播放结束监听器
     * 
     * @param l
     */
    public void setOnPlayListener(OnPlayListener l) {
        mAudioUtil.setOnPlayListener(l);
    }

    /******************************* inner class ****************************************/

    private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = mVolumeHandler.obtainMessage();
                msg.what = HANDLE_FLAG;
                msg.arg1 = getRecordTime();
                if (mAudioUtil != null && running) {
                    // 如果用户仍在录音
                    msg.arg2 = mAudioUtil.getVolumn();
                } else {
                    exit();
                }
                mVolumeHandler.sendMessage(msg);
            }
        }
    }

    static class ShowVolumeHandler extends Handler {
        private final WeakReference<RecordButton> mOuterInstance;

        public ShowVolumeHandler(RecordButton outer) {
            mOuterInstance = new WeakReference<RecordButton>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            RecordButton outerButton = mOuterInstance.get();
            if (msg.what == HANDLE_FLAG) {
                if (msg.arg1 > MAX_INTERVAL_TIME) {
                    outerButton.finishRecord();
                } else {
                    outerButton.changeVolume(msg.arg2);
                    outerButton.mTvRecordTime.setText(msg.arg1 + "\"");
                }
            }
        }
    }

    public interface OnFinishedRecordListener {
        /** 用户手动取消 */
        public void onCancleRecord();

        /** 录音完成 */
        public void onFinishedRecord(String audioPath, int recordTime);
    }
}