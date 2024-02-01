package com.xpeng.test;

import android.view.InputDevice;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class InputTest {
    private ArrayList<InputDevice> mDeviceList = new ArrayList<>();
    public int[] mIds = InputDevice.getDeviceIds();

    InputTest() {
        int i = 0;
        while (true) {
            int[] iArr = this.mIds;
            if (i < iArr.length) {
                InputDevice device = InputDevice.getDevice(iArr[i]);
                this.mDeviceList.add(device);
                i++;
            } else {
                return;
            }
        }
    }

    public ArrayList<InputDevice> getInputDeviceList() {
        return this.mDeviceList;
    }
}
