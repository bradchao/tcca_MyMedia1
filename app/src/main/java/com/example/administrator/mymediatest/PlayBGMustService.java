package com.example.administrator.mymediatest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class PlayBGMustService extends Service {
    private MediaPlayer mediaPlayer;
    private final LocalBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        PlayBGMustService getService(){
            return PlayBGMustService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.brad);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
}
