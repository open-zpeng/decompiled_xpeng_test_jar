package com.xpeng.test.callback;

import android.bluetooth.BluetoothDevice;
/* loaded from: classes.dex */
public interface BluetoothCallback {
    void onBindSuccess();

    void onBtConnectStatus(BluetoothDevice bluetoothDevice, int i, int i2, int i3);

    void onBtPairStatus(int i, BluetoothDevice bluetoothDevice);

    void onBtPower(boolean z);

    void onPairRequest(BluetoothDevice bluetoothDevice, int i, int i2);

    void onScanCallback(BluetoothDevice bluetoothDevice, int i);

    void onScanStatus(boolean z);
}
