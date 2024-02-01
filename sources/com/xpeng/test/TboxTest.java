package com.xpeng.test;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.tbox.CarTboxManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
/* loaded from: classes.dex */
public class TboxTest {
    private Car mCar;
    private CarTboxManager mTboxManager;
    public String TAG = "TboxTest";
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xpeng.test.TboxTest.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            String str = TboxTest.this.TAG;
            Log.i(str, "onServiceConnected, name: " + name + ", service: " + service);
            try {
                TboxTest.this.mTboxManager = (CarTboxManager) TboxTest.this.mCar.getCarManager("xp_tbox");
            } catch (CarNotConnectedException e) {
                String str2 = TboxTest.this.TAG;
                Log.e(str2, "get TboxManager failed!" + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            String str = TboxTest.this.TAG;
            Log.i(str, "onServiceDisconnected, name: " + name);
            TboxTest.this.mTboxManager = null;
        }
    };

    public TboxTest(Context context) {
        this.mCar = Car.createCar(context, this.mServiceConnection);
        try {
            this.mCar.connect();
        } catch (IllegalStateException e) {
            String str = this.TAG;
            Log.e(str, "connect to carservice failed :" + e);
        }
    }

    public int getDiagRequest() {
        if (this.mTboxManager == null) {
            Log.e(this.TAG, "not connected to carservice");
            return -1;
        }
        try {
            int ret = this.mTboxManager.getRemoteDiagCaptureRequest();
            return ret;
        } catch (Exception e) {
            String str = this.TAG;
            Log.e(str, "getDiagRequest from tbox failed!" + e);
            return -1;
        }
    }
}
