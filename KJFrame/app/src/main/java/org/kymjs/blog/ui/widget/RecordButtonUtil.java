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
import java.io.IOException;

import org.kymjs.blog.AppConfig;
import org.kymjs.blog.R;
import org.kymjs.kjframe.ui.KJActivityStack;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.StringUtils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.widget.TextView;

/**
 * 
 * {@link #RecordButton}需要的工具类
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class RecordButtonUtil {
    private final static String TAG = "AudioUtil";

    public static final String AUDOI_DIR = FileUtils.getSDCardPath()
            + File.separator + AppConfig.audioPath; // 录音音频保存根路径

    private String mAudioPath; // 要播放的声音的路径
    private boolean mIsRecording;// 是否正在录音
    private boolean mIsPlaying;// 是否正在播放

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private OnPlayListener listener;

    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * 设置要播放的声音的路径
     * 
     * @param path
     */
    public void setAudioPath(String path) {
        this.mAudioPath = path;
    }

    /**
     * 播放声音结束时调用
     * 
     * @param l
     */
    public void setOnPlayListener(OnPlayListener l) {
        this.listener = l;
    }

    // 初始化 录音器
    private void initRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mAudioPath);
        mIsRecording = true;
    }

    /**
     * 开始录音，并保存到文件中
     */
    public void recordAudio() {
        initRecorder();
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            ViewInject.toast("小屁孩不听你说话了,请返回重试");
        }
    }

    /**
     * 获取音量值，只是针对录音音量
     * 
     * @return
     */
    public int getVolumn() {
        int volumn = 0;
        // 录音
        if (mRecorder != null && mIsRecording) {
            volumn = mRecorder.getMaxAmplitude();
            if (volumn != 0)
                volumn = (int) (10 * Math.log(volumn) / Math.log(10)) / 5;
        }
        return volumn;
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mIsRecording = false;
        }
    }

    public void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mIsPlaying = false;
            if (listener != null) {
                listener.stopPlay();
            }
        }
    }

    public void startPlay(String audioPath, TextView timeView) {
        if (!mIsPlaying) {
            if (!StringUtils.isEmpty(audioPath)) {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(audioPath);
                    mPlayer.prepare();
                    if (timeView != null) {
                        int len = (mPlayer.getDuration() + 500) / 1000;
                        timeView.setText(len + "s");
                    }
                    mPlayer.start();
                    if (listener != null) {
                        listener.starPlay();
                    }
                    mIsPlaying = true;
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopPlay();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ViewInject.toast(KJActivityStack.create().topActivity()
                        .getString(R.string.record_sound_notfound));
            }
        } else {
            stopPlay();
        } // end playing
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        startPlay(mAudioPath, null);
    }

    public interface OnPlayListener {
        /**
         * 播放声音结束时调用
         */
        void stopPlay();

        /**
         * 播放声音开始时调用
         */
        void starPlay();
    }
}
