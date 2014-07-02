package org.kymjs.aframe.utils.media;

import java.io.IOException;

import org.kymjs.aframe.KJConfig;
import org.kymjs.aframe.ui.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

/**
 * 播放器类 单例模式：封装了播放器的相关操作
 * 
 * @explain 每当一首音乐播放完成，系统会自动发出KJConfig.RECEIVER_MUSIC_CHANGE广播
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-6-24
 */
public class Player {
    /** 播放器状态 */
    public enum PlayerState {
        PLAYING_STOP, PLAYING_PAUSE, PLAYING_PLAY
    }

    private Context context;
    private MediaPlayer media;
    private static final Player instance = new Player();
    private PlayerState playing = PlayerState.PLAYING_STOP;

    // flag
    private boolean isPrepare;

    private Player() {}

    public static Player getPlayer() {
        return instance;
    }

    public PlayerState getPlaying() {
        return playing;
    }

    /**
     * 获取播放的音乐文件总时间长度
     */
    public int getDuration() {
        int durat = 0;
        if (media != null) {
            durat = media.getDuration();
        }
        return durat;
    }

    /**
     * 获取声音波形
     */
    public int getAmplitude() {
        return media.getAudioSessionId();
    }

    /**
     * 获取当前播放音乐时间点
     */
    public int getCurrentPosition() {
        int currentPosition = 0;
        if (media != null) {
            currentPosition = media.getCurrentPosition();
        }
        return currentPosition;
    }

    /**
     * 将音乐播放跳转到某一时间点,以毫秒为单位
     */
    public void seekTo(int msec) {
        if (media != null) {
            media.seekTo(msec);
        }
    }

    /**
     * 不再使用播放器后需要调用destroy()方法
     */
    public void destroy() {
        if (media != null) {
            media.release();
            playing = PlayerState.PLAYING_STOP;
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (playing != PlayerState.PLAYING_STOP) {
            media.reset();
            playing = PlayerState.PLAYING_STOP;
            context.sendBroadcast(new Intent(KJConfig.RECEIVER_MUSIC_CHANGE));
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (playing != PlayerState.PLAYING_PAUSE) {
            media.pause();
            playing = PlayerState.PLAYING_PAUSE;
            context.sendBroadcast(new Intent(KJConfig.RECEIVER_MUSIC_CHANGE));
        }
    }

    /**
     * 正在暂停，即将开始继续播放
     */
    public void replay() {
        if (playing != PlayerState.PLAYING_PLAY) {
            media.start();
            playing = PlayerState.PLAYING_PLAY;
            context.sendBroadcast(new Intent(KJConfig.RECEIVER_MUSIC_CHANGE));
        }
    }

    /**
     * 播放网络歌曲
     * 
     * @param context
     *            上下文对象
     * @param musicPath
     *            歌曲路径
     */
    public void playNetMusic(Context context, String musicPath) {
        if (media == null) {
            media = new MediaPlayer();
            media.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        if (isPrepare) {
            ViewInject.toast("声音正在加载，请稍后");
            return;
        }
        try {
            isPrepare = true; // 加载过程中做标记
            media.setDataSource(musicPath);
            media.prepare();
            isPrepare = false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        play(context, musicPath, media);
    }

    /**
     * 播放本地歌曲
     * 
     * @param context
     *            上下文对象
     * @param musicPath
     *            歌曲路径
     */
    public void playLocalMusic(Context context, String musicPath) {
        media = MediaPlayer.create(context, Uri.parse("file://" + musicPath));
        play(context, musicPath, media);
    }

    /**
     * 歌曲播放功能实现
     * 
     * @param musicPath
     *            歌曲路径
     * @param media
     *            不同播放模式传不同的MediaPlayer对象
     */
    private void play(Context context, String musicPath, MediaPlayer media) {
        // 如果有正在播放的歌曲，将它停止
        if (playing == PlayerState.PLAYING_PLAY) {
            media.reset();
        }
        try {
            media.start();
            this.context = context;
            media.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Player.this.context.sendBroadcast(new Intent(
                            KJConfig.RECEIVER_MUSIC_CHANGE));
                }
            });
            playing = PlayerState.PLAYING_PLAY;
            context.sendBroadcast(new Intent(KJConfig.RECEIVER_MUSIC_CHANGE));
        } catch (NullPointerException e) {
            ViewInject.toast("亲，找不到歌曲了，存储卡拔掉了吗？");
        }
    }
}
