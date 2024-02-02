package com.xpeng.test;

import android.content.Context;
import android.media.AudioManager;
/* loaded from: classes.dex */
public class LoopbackTest {
    private final String PARAMETER = "mic2spklb";
    private AudioManager audioManager;
    private Context context;

    public LoopbackTest(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService("audio");
    }

    public void startLoopAudio() {
        this.audioManager.setParameters("mic2spklb=on");
    }

    public void stopLoopAudio() {
        this.audioManager.setParameters("mic2spklb=off");
    }
}
