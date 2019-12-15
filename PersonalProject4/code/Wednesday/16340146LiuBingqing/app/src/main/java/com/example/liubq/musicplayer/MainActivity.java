package com.example.liubq.musicplayer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private MusicService ms = new MusicService();
    private boolean isplaying = false;
    private boolean isProcessChange;
    private float degree = 0;
    private ObjectAnimator animator;

    private CircleImageView cover;
    private TextView name;
    private TextView singer;
    private ImageButton file;
    private ImageButton play;
    private ImageButton stop;
    private ImageButton back;
    private SeekBar seekBar;
    private TextView totalTime;
    private TextView playingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cover = (CircleImageView) findViewById(R.id.cover);
        name = (TextView) findViewById(R.id.name);
        singer = (TextView) findViewById(R.id.singer);
        file = (ImageButton) findViewById(R.id.file);
        play = (ImageButton) findViewById(R.id.play);
        stop = (ImageButton) findViewById(R.id.stop);
        back = (ImageButton) findViewById(R.id.back);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        totalTime = (TextView) findViewById(R.id.totalTime);
        playingTime = (TextView) findViewById(R.id.playingTime);

        setSongDetail(ms.getPath());

        animator = ObjectAnimator.ofFloat(cover,"rotation",0,360);
        animator.setDuration(15000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(Animation.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setCurrentPlayTime(ms.mediaPlayer.getCurrentPosition());
        if(MusicService.mediaPlayer.isPlaying()){
            play.setImageResource(R.mipmap.pause);
            animator.start();
        }

        bindMusicService();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
           @Override
           public void run() {
               Observable.create(new Observable.OnSubscribe<Integer>() {
                   @Override
                   public void call(Subscriber<? super Integer> subscriber) {
                       subscriber.onNext(ms.mediaPlayer.getCurrentPosition());
                       subscriber.onCompleted();
                   }
               })
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Observer<Integer>() {
                   @Override
                   public void onNext(Integer s) {
                       playingTime.setText(time.format(s));
                       seekBar.setProgress(s);
                       seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                           @Override
                           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                               if (fromUser) {
                                   ms.mediaPlayer.seekTo(seekBar.getProgress());
                               }
                           }

                           @Override
                           public void onStartTrackingTouch(SeekBar seekBar) {

                           }

                           @Override
                           public void onStopTrackingTouch(SeekBar seekBar) {

                           }
                       });
                   }

                   @Override
                   public void onCompleted() {

                   }

                   @Override
                   public void onError(Throwable e) {

                   }
               });
           }
       }, 0, 100);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ms.binder.onTransact(101, Parcel.obtain(), Parcel.obtain(), 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                // 开始
                if (!isplaying) {
                    if(!animator.isStarted()){
                        animator.start();
                    }else{
                        animator.resume();
                    }
                    play.setImageResource(R.mipmap.pause);
                    isplaying = true;
                }
                // 暂停
                else {
                    animator.pause();
                    play.setImageResource(R.mipmap.play);
                    isplaying = false;
                }

                //handler.post(runnable);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.end();
                isplaying = false;
                playingTime.setText("00:00");
                seekBar.setProgress(0);
                play.setImageResource(R.mipmap.play);
                try {
                    ms.binder.onTransact(102, Parcel.obtain(), Parcel.obtain(), 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.end();
                //handler.removeCallbacks(runnable);
                unbindService(sc);
                sc = null;
                try {
                    MainActivity.this.finish();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!fromUser) return;
                isProcessChange = true;
                Parcel parcel=Parcel.obtain();
                parcel.writeInt(progress);

                playingTime.setText(time.format(progress));
                try {
                    ms.binder.onTransact(104,parcel,Parcel.obtain(),0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isProcessChange = false;
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setImageResource(R.mipmap.play);

                if (animator != null){
                    degree=(Float) animator.getAnimatedValue();
                    animator.cancel();
                }
                isplaying = false;
                try {
                    ms.binder.onTransact(106,Parcel.obtain(),Parcel.obtain(),0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ms = ((MusicService.MyBinder) service).getService();
            playingTime.setText(time.format(ms.mediaPlayer.getCurrentPosition()));
            totalTime.setText(time.format(ms.mediaPlayer.getDuration()));
            seekBar.setMax(ms.mediaPlayer.getDuration());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ms = null;
        }
    };

    private void bindMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, sc, this.BIND_AUTO_CREATE);
    }


//    public Handler handler = new Handler();
//    public Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            playingTime.setText(time.format(ms.mediaPlayer.getCurrentPosition()));
//            seekBar.setProgress(ms.mediaPlayer.getCurrentPosition());
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (fromUser) {
//                        ms.mediaPlayer.seekTo(seekBar.getProgress());
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
//            handler.postDelayed(runnable, 100);
//        }
//    };


    @Override
    protected void onResume() {
        if (ms.mediaPlayer.isPlaying()) {
            play.setImageResource(R.mipmap.pause);
            isplaying = true;
        }
        seekBar.setProgress(ms.mediaPlayer.getCurrentPosition());
        seekBar.setMax(ms.mediaPlayer.getDuration());
        //handler.post(runnable);

        super.onResume();
    }

    // 读取外部音乐
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri selectAudioUri = data.getData();
            String[] filePathColumn = {MediaStore.Audio.Media.DATA};
            Cursor cursor = getContentResolver().query(selectAudioUri,filePathColumn,null,null,null);
            cursor.moveToLast();
            String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();

            if(path!=null){
                Parcel parcel=Parcel.obtain();
                parcel.writeString(path);
                try {
                    ms.binder.onTransact(105,parcel,Parcel.obtain(),0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                animator.end();
                isplaying = false;
                playingTime.setText("00:00");
                seekBar.setProgress(0);
                play.setImageResource(R.mipmap.play);

                setSongDetail(path);
            }
        }
    }

    private void setSongDetail(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String song_name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String singer_name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        byte[] data = mmr.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        cover.setImageBitmap(bitmap);

        name.setText(song_name);
        singer.setText(singer_name);

        totalTime.setText(time.format(ms.mediaPlayer.getDuration()));

        mmr.release();
    }

}


