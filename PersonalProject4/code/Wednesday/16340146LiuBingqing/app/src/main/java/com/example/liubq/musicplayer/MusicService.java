package com.example.liubq.musicplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import java.io.File;
import java.io.IOException;

public class MusicService extends Service {
    public static MediaPlayer mediaPlayer;
    public static String mediaPath;

    public final MyBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService() { return MusicService.this; }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            // 播放or暂停按钮，service
            if (code == 101) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
            // 停止按钮,service
            else if (code == 102) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (code == 103) {
                System.exit(0);
            }
            else if (code == 104) {
                data.setDataPosition(0);
                int process = data.readInt();
                mediaPlayer.seekTo(process);
            }
            // 外部加载音乐
            else if (code == 105) {
                data.setDataPosition(0);
                String path = data.readString();
                if (path!=null) {
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(path);
                        mediaPath = path;
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (code==106){
                mediaPlayer.pause();
            }
            return super.onTransact(code,data,reply, flags);
        }
    }

    public MusicService() {
        try {
            if(mediaPlayer == null)
            {
                mediaPlayer = new MediaPlayer();
                mediaPath = Environment.getExternalStorageDirectory().getPath()+"/山高水长.mp3";
                mediaPlayer.setDataSource(mediaPath);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);  // 设置循环播放
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }

    public String getPath(){
        return mediaPath;
    }
}
