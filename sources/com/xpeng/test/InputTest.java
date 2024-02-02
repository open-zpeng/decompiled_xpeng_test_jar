package com.xpeng.test;

import android.view.InputDevice;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class InputTest {
    private ArrayList<InputDevice> mDeviceList = new ArrayList<>();
    public int[] mIds = InputDevice.getDeviceIds();

    InputTest() {
        for (int i = 0; i < this.mIds.length; i++) {
            InputDevice device = InputDevice.getDevice(this.mIds[i]);
            this.mDeviceList.add(device);
        }
    }

    public ArrayList<InputDevice> getInputDeviceList() {
        return this.mDeviceList;
    }
}
