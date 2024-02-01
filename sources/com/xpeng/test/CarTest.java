package com.xpeng.test;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.diagnostic.XpDiagnosticManager;
import android.car.hardware.CarPropertyConfig;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.mcu.CarMcuManager;
import android.car.hardware.tbox.CarTboxManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import java.util.List;

/* loaded from: classes.dex */
public class CarTest {
    private Car mCar;
    private CarMcuManager mCarMcuManager;
    private CarTboxManager mTboxManager;
    private TestCallBack mTestCallBack;
    private XpDiagnosticManager mXpDiagnosticManager;
    public String TAG = "CarTest";
    private boolean isConnected = false;
    private int mMcuCallBackCount = 0;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xpeng.test.CarTest.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            String str = CarTest.this.TAG;
            Log.i(str, "onServiceConnected, name: " + name + ", service: " + service);
            try {
                CarTest.this.mTboxManager = (CarTboxManager) CarTest.this.mCar.getCarManager("xp_tbox");
                CarTest.this.mXpDiagnosticManager = (XpDiagnosticManager) CarTest.this.mCar.getCarManager("xp_diagnostic");
                CarTest.this.mCarMcuManager = (CarMcuManager) CarTest.this.mCar.getCarManager("xp_mcu");
                CarTest.this.mTestCallBack = new TestCallBack();
                CarTest.this.isConnected = true;
            } catch (CarNotConnectedException e) {
                String str2 = CarTest.this.TAG;
                Log.e(str2, "get TboxManager failed!" + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            String str = CarTest.this.TAG;
            Log.i(str, "onServiceDisconnected, name: " + name);
            CarTest.this.mXpDiagnosticManager = null;
            CarTest.this.mTboxManager = null;
        }
    };

    static /* synthetic */ int access$608(CarTest x0) {
        int i = x0.mMcuCallBackCount;
        x0.mMcuCallBackCount = i + 1;
        return i;
    }

    public CarTest(Context context) {
        this.mCar = Car.createCar(context, this.mServiceConnection);
        try {
            this.mCar.connect();
        } catch (IllegalStateException e) {
            String str = this.TAG;
            Log.e(str, "connect to carservice failed :" + e);
        }
    }

    public boolean getIsConnected() {
        return this.isConnected;
    }

    public void registerAllCallBack() {
        while (true) {
            CarMcuManager carMcuManager = this.mCarMcuManager;
            if (carMcuManager == null) {
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.w(this.TAG, "registerAllCallBack :wait connect to carservice");
            } else {
                try {
                    carMcuManager.registerCallback(this.mTestCallBack);
                    return;
                } catch (Exception e2) {
                    String str = this.TAG;
                    Log.e(str, "registerAllCallBack :" + e2);
                    return;
                }
            }
        }
    }

    public int getMcuCallBackCount() {
        return this.mMcuCallBackCount;
    }

    /* loaded from: classes.dex */
    class TestCallBack implements CarMcuManager.CarMcuEventCallback {
        TestCallBack() {
        }

        public void onChangeEvent(CarPropertyValue value) {
            String str = CarTest.this.TAG;
            Log.d(str, "onChangeEvent:" + value);
            CarTest.access$608(CarTest.this);
        }

        public void onErrorEvent(int propertyId, int zone) {
        }
    }

    public int getDiagRequest() {
        CarTboxManager carTboxManager = this.mTboxManager;
        if (carTboxManager == null) {
            Log.e(this.TAG, "not connected to carservice");
            return -1;
        }
        try {
            int ret = carTboxManager.getRemoteDiagCaptureRequest();
            return ret;
        } catch (Exception e) {
            String str = this.TAG;
            Log.e(str, "getDiagRequest from tbox failed!" + e);
            return -1;
        }
    }

    public int getDiagPropList() {
        XpDiagnosticManager xpDiagnosticManager = this.mXpDiagnosticManager;
        if (xpDiagnosticManager == null) {
            Log.e(this.TAG, "not connected to carservice");
            return -1;
        }
        try {
            List<CarPropertyConfig> list = xpDiagnosticManager.getPropertyList();
            int ret = list.size();
            return ret;
        } catch (Exception e) {
            String str = this.TAG;
            Log.e(str, "getDiagRequest from tbox failed!" + e);
            return -1;
        }
    }
}
