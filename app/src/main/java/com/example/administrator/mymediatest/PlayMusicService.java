package com.example.administrator.mymediatest;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.IOException;

public class PlayMusicService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String file = intent.getStringExtra("play");
        boolean isPause = intent.getBooleanExtra("pause", false);
        if (isPause){
            pauseMusic();
        }else {
            playMusic(file);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void pauseMusic(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    private void playMusic(String file){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();


        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            Log.i("brad", e.toString());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
