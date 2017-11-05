package com.example.administrator.mymediatest;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private AudioManager audioManager;
    private SeekBar seekBar;
    private boolean isConnect;
    private ListView listView;
    private SimpleAdapter adapter;
    private String[] from = {"title","singer"};
    private int[] to = {R.id.title, R.id.singer};
    private LinkedList<HashMap<String,String>> data;


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
        listView = (ListView)findViewById(R.id.listview);
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



        getMusicList();

    }

    private void getMusicList(){
        data = new LinkedList<>();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        File musicPath =
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC);
        File[] musics = musicPath.listFiles();
        for (File music : musics){
            if (music.isDirectory()){
                File[] smusics = music.listFiles();
                for (File smusic : smusics){
                    if (!smusic.isFile()) continue;
                    mmr.setDataSource(smusic.toString());

                    String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

                    HashMap<String,String> musicInfo = new HashMap<>();
                    musicInfo.put("file", music.getAbsolutePath());
                    musicInfo.put(from[0], "sub:" + title);
                    musicInfo.put(from[1], singer);

                    data.add(musicInfo);
                }
            }else if (music.isFile()){
                mmr.setDataSource(music.toString());

                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);


                HashMap<String,String> musicInfo = new HashMap<>();
                musicInfo.put("file", music.getAbsolutePath());
                musicInfo.put(from[0], title);
                musicInfo.put(from[1], singer);

                data.add(musicInfo);
            }

        }
        adapter = new SimpleAdapter(this,data,R.layout.item, from, to);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playMusic(i);
            }
        });

    }

    private void playMusic(int i){
        mService.pausePlay();

        String file = data.get(i).get("file");
        Intent it = new Intent(this, PlayMusicService.class);
        it.putExtra("play", file);
        startService(it);

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
