package com.xpeng.test;

import android.content.Context;
import android.util.Log;
/* loaded from: classes.dex */
public class StabilityTest {
    final String TAG = "StabilityTest";
    Context mContext;

    private static native String native_get_cpu_available_frequencies(int i);

    private static native String native_run_cpueater();

    private static native String native_run_memeater(String str);

    private static native String native_run_memtester(String str);

    static {
        System.loadLibrary("xptest");
    }

    public StabilityTest(Context context) {
        this.mContext = context;
    }

    public StabilityTest() {
    }

    public String getCpuAvailFreq(int cpuid) {
        return native_get_cpu_available_frequencies(cpuid);
    }

    public String runCpueater() {
        return native_run_cpueater();
    }

    public void runMemtester(String size) {
        String ret = native_run_memtester(size);
        Log.d("StabilityTest", "runMemtester:" + ret);
    }

    public String runMemeater(String size) {
        String ret = native_run_memtester(size);
        Log.d("StabilityTest", "runMemtester:" + ret);
        return ret;
    }
}
