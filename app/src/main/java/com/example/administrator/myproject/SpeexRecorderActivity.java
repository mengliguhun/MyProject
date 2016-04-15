package com.example.administrator.myproject;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.audio.manager.SpeexPlayer;
import com.audio.manager.SpeexRecorder;
import com.example.administrator.myproject.utils.SDUtil;

public class SpeexRecorderActivity extends BaseActivity implements OnClickListener{
    private Toolbar toolbar;
    private Button mRecorder,mPlay;
    public static final int STOPPED = 0;
    public static final int RECORDING = 1;
    public static final int PLAYING = 2;

    SpeexRecorder recorderInstance = null;
    int status = STOPPED;
    String fileName = null;
    SpeexPlayer splayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speexrecorder);

        initViews();
        bindViews();

    }

    @Override
    public void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Speex录音播放");
        setSupportActionBar(toolbar);

        mRecorder = (Button) findViewById(R.id.recorder);
        mPlay = (Button) findViewById(R.id.play);

    }

    @Override
    public void bindViews() {
        mRecorder.setOnClickListener(this);
        mPlay.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recorder:
                if (status == STOPPED) {
                    this.setTitle("开始录音了！");
                    fileName = SDUtil.getFileName(".spx",null,null);
                    startRecorder(fileName);
                } else if (status == RECORDING) {
                    this.setTitle("停止了");
                    stopRecorder();
                }
                break;
            case R.id.play:
                // play here........
                this.setTitle("开始播放");
                if (TextUtils.isEmpty(fileName)){

                    return;
                }
                if (status == PLAYING){
                    stopPlay();
                }else {
                    startPlay(fileName);
                }

                break;
        }

    }
    public int getStatus(){
        if(status == PLAYING && splayer.isStop())
            status = STOPPED;
        return status;
    }

    public void startRecorder(String fileName) {
        recorderInstance = new SpeexRecorder(fileName);
        Thread th = new Thread(recorderInstance);
        th.start();

        recorderInstance.setRecording(true);

        status = RECORDING;
    }

    public void stopRecorder() {
        if (recorderInstance != null) {
            if (recorderInstance.isRecording())
                recorderInstance.setRecording(false);
        }
        status = STOPPED;
    }

    public void startPlay(String fileName) {
        if (splayer != null && splayer.getPlayFileName().equals(fileName)
                && !splayer.isStop() && splayer.isPaused()) {
            splayer.resumePlay();
        } else {
            splayer = new SpeexPlayer(fileName);
            splayer.startPlay();
        }
        status = PLAYING;
    }

    public void stopPlay() {
        if (splayer != null && !splayer.isStop())
            splayer.stopPlay();
        status = STOPPED;
    }

}
