package com.xpeng.test;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public class RecordModel {
    public static final int MAX_LENGTH = 600000;
    Context mContext;
    File mFile;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mPlayer = null;
    private static final String TAG = RecordModel.class.getSimpleName();
    private static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/record/xpTest.amr";
    private static final String mFilePath = Environment.getExternalStorageDirectory() + "/record/";

    public RecordModel(Context context) {
        this.mContext = null;
        this.mFile = null;
        this.mContext = context;
        this.mFile = new File(mFilePath);
        if (!this.mFile.exists()) {
            this.mFile.mkdirs();
        }
    }

    public void startRecord() {
        if (this.mMediaRecorder == null) {
            this.mMediaRecorder = new MediaRecorder();
        }
        try {
            this.mMediaRecorder.setAudioSource(0);
            this.mMediaRecorder.setOutputFormat(4);
            this.mMediaRecorder.setAudioEncoder(2);
            this.mMediaRecorder.setOutputFile(BASE_PATH);
            this.mMediaRecorder.setMaxDuration(MAX_LENGTH);
            this.mMediaRecorder.prepare();
            this.mMediaRecorder.start();
        } catch (IOException e) {
            String str = TAG;
            Log.i(str, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IllegalStateException e2) {
            String str2 = TAG;
            Log.i(str2, "call startAmr(File mRecAudioFile) failed!" + e2.getMessage());
        }
    }

    public void stopRecord() {
        if (this.mMediaRecorder == null) {
            return;
        }
        try {
            this.mMediaRecorder.stop();
            this.mMediaRecorder.reset();
            this.mMediaRecorder.release();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        this.mMediaRecorder = null;
    }

    public void play() {
        if (this.mPlayer == null) {
            this.mPlayer = new MediaPlayer();
            try {
                this.mPlayer.setDataSource(BASE_PATH);
                this.mPlayer.prepare();
                this.mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlay() {
        try {
            if (this.mPlayer != null) {
                this.mPlayer.stop();
                this.mPlayer.release();
                this.mPlayer = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void releaseAll() {
        try {
            if (this.mMediaRecorder != null) {
                this.mMediaRecorder.stop();
                this.mMediaRecorder.release();
            }
            if (this.mPlayer != null) {
                this.mPlayer.stop();
                this.mPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
