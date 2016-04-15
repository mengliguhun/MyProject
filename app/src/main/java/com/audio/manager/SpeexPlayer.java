package com.audio.manager;

import java.io.File;

/**
 * @author Gauss
 *
 */
public class SpeexPlayer {
    private String fileName = null;
    private SpeexDecoder speexdec = null;
    private RecordPlayThread rpt;
    private boolean isPlay = false;
    public SpeexPlayer(String fileName) {

        this.fileName = fileName;
        System.out.println(this.fileName);
        try {
            speexdec = new SpeexDecoder(new File(this.fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getPlayFileName(){
        return fileName;
    }
    public boolean isPlay() {
        if (isPaused()||speexdec.isStop()){
            isPlay = false;
        }
        return isPlay;
    }

    public boolean isPaused(){
        return speexdec.isPaused();
    }

    public boolean isStop(){
        return speexdec.isStop();
    }

    public void startPlay() {
        rpt = new RecordPlayThread();
        Thread th = new Thread(rpt);
        th.start();
    }

    public void pausePlay(){
        speexdec.setPaused(true);
    }

    public void stopPlay(){
        if (rpt == null)
            return;
        rpt.interrupt();
    }

    public void resumePlay() {
        speexdec.setPaused(false);
    }

    class RecordPlayThread extends Thread {

        public void run() {
            try {
                if (speexdec != null)
                    speexdec.decode();
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
    };
}
