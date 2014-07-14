/*
 * Copyright (c) 2012-2013, kymjs 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.utils.media;

import java.io.IOException;

import org.kymjs.aframe.utils.FileUtils;

import android.media.MediaRecorder;

/**
 * Update Log
 * @1.0 代理-单例模式封装录音器MediaRecorder
 * @1.1 状态模式封装录音操作，使外界只考虑录音功能调用
 */

/**
 * 录音器类 单例模式：封装了录音器的相关操作
 * 
 * @explain 每当成功录制一段音频后应当记得调用setRecordFileName(String)重新设置文件名，否则将会覆盖上一次录制的音频。
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-6-24
 */
public class Record {
    private static Record instance = null;
    private MediaRecorder recorder;
    private boolean recordding = false;
    private String recordFileName = "kjLibraryRecord.amr";

    private Record() {
        recorder = new MediaRecorder();
    }

    public static Record create() {
        if (instance == null) {
            instance = new Record();
        }
        return instance;
    }

    /**
     * 初始化录音器
     */
    private void init() {
        // 设置录音的声音来源
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置声音编码的格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置音频采样率
        recorder.setAudioSamplingRate(8000);
        // 设置声音的存储位置
        recorder.setOutputFile(FileUtils.getSaveFile(recordFileName)
                .getAbsolutePath());
    }

    /**
     * 开始录音
     */
    public void start() {
        init();
        try {
            recorder.prepare();
            recorder.start();
            recordding = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void stop() {
        if (recordding) {
            recorder.stop();
            recordding = false;
        }
    }

    /**
     * 不再录音时应当调用destroy()重置录音器
     */
    public void destroy() {
        stop();
        recorder.release();
        recordding = false;
    }

    /**
     * 获取录音大小
     */
    public double getAmplitude() {
        if (recorder != null) {
            return (recorder.getMaxAmplitude());
        } else
            return 0;
    }

    /**
     * 是否正在录音
     */
    public boolean isRecordding() {
        return recordding;
    }

    /**
     * 设置录音文件的文件名
     */
    public void setRecordFileName(String fileName) {
        this.recordFileName = fileName;
    }
}
