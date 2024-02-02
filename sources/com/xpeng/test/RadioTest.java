package com.xpeng.test;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.radio.CarRadioManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.xpeng.test.callback.CarCallback;
/* loaded from: classes.dex */
public class RadioTest {
    private Car mCar;
    private CarCallback mCarCallback;
    Context mContext;
    private CarRadioManager mRadioManager;
    final String TAG = "RadioTest ";
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xpeng.test.RadioTest.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("RadioTest ", "onServiceConnected, name: " + name + ", service: " + service);
            try {
                RadioTest.this.mRadioManager = (CarRadioManager) RadioTest.this.mCar.getCarManager("xp_radio");
                RadioTest.this.mRadioManager.registerCallback(RadioTest.this.mCarRadioEventCallback);
            } catch (CarNotConnectedException e) {
                Log.e("RadioTest ", "get CarRadioManager failed!" + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Log.i("RadioTest ", "onServiceDisconnected, name: " + name);
        }
    };
    private CarRadioManager.CarRadioEventCallback mCarRadioEventCallback = new CarRadioManager.CarRadioEventCallback() { // from class: com.xpeng.test.RadioTest.2
        public void onChangeEvent(CarPropertyValue value) {
            Log.d("RadioTest ", "onChangeEvent" + value.getValue());
            if (RadioTest.this.mCarCallback != null) {
                RadioTest.this.mCarCallback.handleChangeEvent(value);
            }
        }

        public void onErrorEvent(int propertyId, int zone) {
            Log.d("RadioTest ", "onErrorEvent propertyId = " + propertyId + " , zone = " + zone);
            if (RadioTest.this.mCarCallback != null) {
                RadioTest.this.mCarCallback.handleErrorEvent(propertyId, zone);
            }
        }
    };

    public RadioTest(Context context) {
        this.mContext = context;
        this.mCar = Car.createCar(context, this.mServiceConnection);
        this.mCar.connect();
    }

    public void registerCallback(CarCallback carCallback) {
        this.mCarCallback = carCallback;
    }

    public void releaseCallback() {
        this.mCarCallback = null;
    }

    public boolean openFm() {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setPowerOnTunner();
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e("RadioTest ", "openFm failed! " + e);
            return false;
        }
    }

    public boolean closeFm() {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setPowerOffTunner();
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e("RadioTest ", "closeFm failed! " + e);
            return false;
        }
    }

    public boolean setRadioFrequency(int band, int frequency) {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setRadioFrequency(band, frequency);
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e("RadioTest ", "setRadioFrequency failed! " + e);
            return false;
        }
    }

    public boolean searchStationUp() {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setRadioSearchStationUp();
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e("RadioTest ", "search up failed! " + e);
            return false;
        }
    }

    public boolean searchStationDown() {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setRadioSearchStationDown();
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e("RadioTest ", "search down failed! " + e);
            return false;
        }
    }

    public void setRadioBand(int band) {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setRadioBand(band);
            }
        } catch (Exception e) {
            Log.e("RadioTest ", "setRadioBand failed! " + e);
        }
    }

    public void setRadioVolumePercent(int channel, int vol) {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setRadioVolumePercent(channel, vol);
            }
        } catch (Exception e) {
            Log.e("RadioTest ", "setRadioVolumePercent failed! " + e);
        }
    }

    public void setRadioVolumeAutoFocus(int percent) {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setRadioVolumeAutoFocus(percent);
            }
        } catch (Exception e) {
            Log.e("RadioTest ", "setRadioVolumeAutoFocus failed! " + e);
        }
    }

    public void setFmVolume(int channel, int volume) {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setFmVolume(channel, volume);
            }
        } catch (Exception e) {
            Log.e("RadioTest ", "setFmVolume failed! " + e);
        }
    }

    public int[] getRadioFrequency() {
        try {
            if (this.mRadioManager == null) {
                return null;
            }
            int[] result = this.mRadioManager.getRadioFrequency();
            return result;
        } catch (Exception e) {
            Log.e("RadioTest ", "getRadioFrequency failed! " + e);
            return null;
        }
    }

    public void setStartFullBandScan() {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setStartFullBandScan();
            }
        } catch (Exception e) {
            Log.e("RadioTest ", "setStartFullBandScan failed! " + e);
        }
    }

    public void setStopFullBandScan() {
        try {
            if (this.mRadioManager != null) {
                this.mRadioManager.setStopFullBandScan();
            }
        } catch (Exception e) {
            Log.e("RadioTest ", "setStopFullBandScan failed! " + e);
        }
    }
}
