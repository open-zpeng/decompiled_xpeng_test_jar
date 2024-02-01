package com.xpeng.test;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;
/* loaded from: classes.dex */
public class CameraTest {
    public String TAG = "CameraTest";
    public String[] mCameraIdList;
    CameraManager mCm;

    CameraTest(Context context) {
        this.mCm = (CameraManager) context.getSystemService("camera");
        try {
            this.mCameraIdList = this.mCm.getCameraIdList();
        } catch (CameraAccessException e) {
            String str = this.TAG;
            Log.e(str, "get camera id list failed!" + e);
        }
    }

    public String[] getCameraIdList() {
        return this.mCameraIdList;
    }
}
