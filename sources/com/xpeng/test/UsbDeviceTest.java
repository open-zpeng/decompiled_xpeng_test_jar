package com.xpeng.test;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import java.util.HashMap;
/* loaded from: classes.dex */
public class UsbDeviceTest {
    final String TAG = "UsbDeviceTest";
    Context mContext;
    private UsbManager mUsbManager;

    public UsbDeviceTest(Context c) {
        this.mContext = c;
        this.mUsbManager = (UsbManager) this.mContext.getSystemService("usb");
    }

    public HashMap<String, UsbDevice> getDeviceList() {
        return this.mUsbManager.getDeviceList();
    }
}
