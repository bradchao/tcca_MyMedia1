package com.example.administrator.mymediatest;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private AudioManager audioManager;
    private SeekBar seekBar;
    private boolean isConnect;

    private PlayBGMustService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayBGMustService.LocalBinder binder =
                    (PlayBGMustService.LocalBinder)iBinder;
            mService = binder.getService();
            isConnect = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnect = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    0);
        }else{
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
        }else{
            finish();
        }
    }

    private void init(){
        seekBar = (SeekBar)findViewById(R.id.seekbar);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(vol);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void test0(View view){
        audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD,0);
    }

    public void test1(View view){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,0);
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }
    public void test2(View view){
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,0);
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent it = new Intent(this, PlayBGMustService.class);
        bindService(it, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isConnect){
            unbindService(mConnection);
        }

    }

    public void test3(View view) {
        Intent it = new Intent(this, Page2Activity.class);
        startActivity(it);
    }
}
