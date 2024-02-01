package com.xpeng.test;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAvrcpController;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidHost;
import android.bluetooth.BluetoothMapClient;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothPbap;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.xpeng.test.callback.BluetoothCallback;
import java.util.Set;

/* loaded from: classes.dex */
public class BluetoothTest {
    private static final String TAG = "BluetoothTest";
    private BluetoothA2dp mA2dp;
    private BluetoothA2dpSink mA2dpSink;
    private final BluetoothAdapter mAdapter;
    private BluetoothAvrcpController mAvrcpController;
    private BluetoothCallback mBluetoothCallback;
    private BluetoothReceiver mBluetoothReceiver;
    Context mContext;
    private BluetoothHeadset mHeadset;
    private BluetoothHeadsetClient mHeadsetClient;
    private BluetoothHearingAid mHearingAid;
    private BluetoothHidDevice mHidDevice;
    private BluetoothHidHost mHidHost;
    private BluetoothMapClient mMapClient;
    private BluetoothPan mPan;
    private BluetoothPbap mPbap;
    private BluetoothPbapClient mPbapClient;
    private BluetoothSap mSap;
    private BluetoothProfile.ServiceListener mServiceListener;

    public BluetoothTest(Context context) {
        this.mA2dp = null;
        this.mA2dpSink = null;
        this.mPbap = null;
        this.mPbapClient = null;
        this.mHeadsetClient = null;
        this.mAvrcpController = null;
        this.mHeadset = null;
        this.mHearingAid = null;
        this.mHidDevice = null;
        this.mHidHost = null;
        this.mMapClient = null;
        this.mPan = null;
        this.mSap = null;
        this.mServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.xpeng.test.BluetoothTest.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                Log.d(BluetoothTest.TAG, "onServiceConnected profile = " + profile);
                synchronized (this) {
                    if (profile == 1) {
                        BluetoothTest.this.mHeadset = (BluetoothHeadset) proxy;
                    } else if (profile == 2) {
                        BluetoothTest.this.mA2dp = (BluetoothA2dp) proxy;
                    } else if (profile == 4) {
                        BluetoothTest.this.mHidHost = (BluetoothHidHost) proxy;
                    } else if (profile == 5) {
                        BluetoothTest.this.mPan = (BluetoothPan) proxy;
                    } else if (profile == 6) {
                        BluetoothTest.this.mPbap = (BluetoothPbap) proxy;
                    } else if (profile != 21) {
                        switch (profile) {
                            case 10:
                                BluetoothTest.this.mSap = (BluetoothSap) proxy;
                                break;
                            case 11:
                                BluetoothTest.this.mA2dpSink = (BluetoothA2dpSink) proxy;
                                break;
                            case 12:
                                BluetoothTest.this.mAvrcpController = (BluetoothAvrcpController) proxy;
                                break;
                            default:
                                switch (profile) {
                                    case 16:
                                        BluetoothTest.this.mHeadsetClient = (BluetoothHeadsetClient) proxy;
                                        break;
                                    case 17:
                                        BluetoothTest.this.mPbapClient = (BluetoothPbapClient) proxy;
                                        break;
                                    case 18:
                                        BluetoothTest.this.mMapClient = (BluetoothMapClient) proxy;
                                        break;
                                    case 19:
                                        BluetoothTest.this.mHidDevice = (BluetoothHidDevice) proxy;
                                        break;
                                }
                        }
                    } else {
                        BluetoothTest.this.mHearingAid = (BluetoothHearingAid) proxy;
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int profile) {
                Log.d(BluetoothTest.TAG, "onServiceDisconnected profile = " + profile);
                synchronized (this) {
                    if (profile == 1) {
                        BluetoothTest.this.mHeadset = null;
                    } else if (profile == 2) {
                        BluetoothTest.this.mA2dp = null;
                    } else if (profile == 4) {
                        BluetoothTest.this.mHidHost = null;
                    } else if (profile == 5) {
                        BluetoothTest.this.mPan = null;
                    } else if (profile == 6) {
                        BluetoothTest.this.mPbap = null;
                    } else if (profile != 21) {
                        switch (profile) {
                            case 10:
                                BluetoothTest.this.mSap = null;
                                break;
                            case 11:
                                BluetoothTest.this.mA2dpSink = null;
                                break;
                            case 12:
                                BluetoothTest.this.mAvrcpController = null;
                                break;
                            default:
                                switch (profile) {
                                    case 16:
                                        BluetoothTest.this.mHeadsetClient = null;
                                        break;
                                    case 17:
                                        BluetoothTest.this.mPbapClient = null;
                                        break;
                                    case 18:
                                        BluetoothTest.this.mMapClient = null;
                                        break;
                                    case 19:
                                        BluetoothTest.this.mHidDevice = null;
                                        break;
                                }
                        }
                    } else {
                        BluetoothTest.this.mHearingAid = null;
                    }
                }
            }
        };
        this.mContext = context;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothReceiver = new BluetoothReceiver();
    }

    public BluetoothTest() {
        this.mA2dp = null;
        this.mA2dpSink = null;
        this.mPbap = null;
        this.mPbapClient = null;
        this.mHeadsetClient = null;
        this.mAvrcpController = null;
        this.mHeadset = null;
        this.mHearingAid = null;
        this.mHidDevice = null;
        this.mHidHost = null;
        this.mMapClient = null;
        this.mPan = null;
        this.mSap = null;
        this.mServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.xpeng.test.BluetoothTest.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                Log.d(BluetoothTest.TAG, "onServiceConnected profile = " + profile);
                synchronized (this) {
                    if (profile == 1) {
                        BluetoothTest.this.mHeadset = (BluetoothHeadset) proxy;
                    } else if (profile == 2) {
                        BluetoothTest.this.mA2dp = (BluetoothA2dp) proxy;
                    } else if (profile == 4) {
                        BluetoothTest.this.mHidHost = (BluetoothHidHost) proxy;
                    } else if (profile == 5) {
                        BluetoothTest.this.mPan = (BluetoothPan) proxy;
                    } else if (profile == 6) {
                        BluetoothTest.this.mPbap = (BluetoothPbap) proxy;
                    } else if (profile != 21) {
                        switch (profile) {
                            case 10:
                                BluetoothTest.this.mSap = (BluetoothSap) proxy;
                                break;
                            case 11:
                                BluetoothTest.this.mA2dpSink = (BluetoothA2dpSink) proxy;
                                break;
                            case 12:
                                BluetoothTest.this.mAvrcpController = (BluetoothAvrcpController) proxy;
                                break;
                            default:
                                switch (profile) {
                                    case 16:
                                        BluetoothTest.this.mHeadsetClient = (BluetoothHeadsetClient) proxy;
                                        break;
                                    case 17:
                                        BluetoothTest.this.mPbapClient = (BluetoothPbapClient) proxy;
                                        break;
                                    case 18:
                                        BluetoothTest.this.mMapClient = (BluetoothMapClient) proxy;
                                        break;
                                    case 19:
                                        BluetoothTest.this.mHidDevice = (BluetoothHidDevice) proxy;
                                        break;
                                }
                        }
                    } else {
                        BluetoothTest.this.mHearingAid = (BluetoothHearingAid) proxy;
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int profile) {
                Log.d(BluetoothTest.TAG, "onServiceDisconnected profile = " + profile);
                synchronized (this) {
                    if (profile == 1) {
                        BluetoothTest.this.mHeadset = null;
                    } else if (profile == 2) {
                        BluetoothTest.this.mA2dp = null;
                    } else if (profile == 4) {
                        BluetoothTest.this.mHidHost = null;
                    } else if (profile == 5) {
                        BluetoothTest.this.mPan = null;
                    } else if (profile == 6) {
                        BluetoothTest.this.mPbap = null;
                    } else if (profile != 21) {
                        switch (profile) {
                            case 10:
                                BluetoothTest.this.mSap = null;
                                break;
                            case 11:
                                BluetoothTest.this.mA2dpSink = null;
                                break;
                            case 12:
                                BluetoothTest.this.mAvrcpController = null;
                                break;
                            default:
                                switch (profile) {
                                    case 16:
                                        BluetoothTest.this.mHeadsetClient = null;
                                        break;
                                    case 17:
                                        BluetoothTest.this.mPbapClient = null;
                                        break;
                                    case 18:
                                        BluetoothTest.this.mMapClient = null;
                                        break;
                                    case 19:
                                        BluetoothTest.this.mHidDevice = null;
                                        break;
                                }
                        }
                    } else {
                        BluetoothTest.this.mHearingAid = null;
                    }
                }
            }
        };
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void registerCallback(BluetoothCallback callback) {
        this.mBluetoothCallback = callback;
    }

    public void releaseCallback() {
        this.mBluetoothCallback = null;
    }

    public void getProfileProxy(int profile) {
        Log.d(TAG, "getProfileProxy profile = " + profile);
        if (!this.mAdapter.getProfileProxy(this.mContext, this.mServiceListener, profile)) {
            Log.d(TAG, "getProfileProxy profile = " + profile + " result = fail");
        }
    }

    public void closeProfileProxy() {
        BluetoothProfile bluetoothProfile = this.mPbapClient;
        if (bluetoothProfile != null) {
            this.mAdapter.closeProfileProxy(17, bluetoothProfile);
        }
        BluetoothProfile bluetoothProfile2 = this.mHeadsetClient;
        if (bluetoothProfile2 != null) {
            this.mAdapter.closeProfileProxy(16, bluetoothProfile2);
        }
        BluetoothProfile bluetoothProfile3 = this.mA2dpSink;
        if (bluetoothProfile3 != null) {
            this.mAdapter.closeProfileProxy(11, bluetoothProfile3);
        }
        BluetoothHeadset bluetoothHeadset = this.mHeadset;
        if (bluetoothHeadset != null) {
            this.mAdapter.closeProfileProxy(1, bluetoothHeadset);
        }
        BluetoothA2dp bluetoothA2dp = this.mA2dp;
        if (bluetoothA2dp != null) {
            this.mAdapter.closeProfileProxy(2, bluetoothA2dp);
        }
        BluetoothProfile bluetoothProfile4 = this.mPbap;
        if (bluetoothProfile4 != null) {
            this.mAdapter.closeProfileProxy(6, bluetoothProfile4);
        }
        BluetoothProfile bluetoothProfile5 = this.mSap;
        if (bluetoothProfile5 != null) {
            this.mAdapter.closeProfileProxy(10, bluetoothProfile5);
        }
        BluetoothHearingAid bluetoothHearingAid = this.mHearingAid;
        if (bluetoothHearingAid != null) {
            this.mAdapter.closeProfileProxy(21, bluetoothHearingAid);
        }
        BluetoothProfile bluetoothProfile6 = this.mMapClient;
        if (bluetoothProfile6 != null) {
            this.mAdapter.closeProfileProxy(18, bluetoothProfile6);
        }
        BluetoothProfile bluetoothProfile7 = this.mHidHost;
        if (bluetoothProfile7 != null) {
            this.mAdapter.closeProfileProxy(4, bluetoothProfile7);
        }
        BluetoothProfile bluetoothProfile8 = this.mPan;
        if (bluetoothProfile8 != null) {
            this.mAdapter.closeProfileProxy(5, bluetoothProfile8);
        }
        BluetoothHidDevice bluetoothHidDevice = this.mHidDevice;
        if (bluetoothHidDevice != null) {
            this.mAdapter.closeProfileProxy(19, bluetoothHidDevice);
        }
        BluetoothProfile bluetoothProfile9 = this.mAvrcpController;
        if (bluetoothProfile9 != null) {
            this.mAdapter.closeProfileProxy(12, bluetoothProfile9);
        }
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        filter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.FOUND");
        filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        filter.addAction("android.bluetooth.pbapclient.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.sap.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.mapmce.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.hiddevice.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction("android.bluetooth.avrcp-controller.profile.action.CONNECTION_STATE_CHANGED");
        this.mContext.registerReceiver(this.mBluetoothReceiver, filter);
    }

    public void unregisterReceiver() {
        this.mContext.unregisterReceiver(this.mBluetoothReceiver);
    }

    /* loaded from: classes.dex */
    private class BluetoothReceiver extends BroadcastReceiver {
        private BluetoothReceiver() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            String action = intent.getAction();
            Log.d(BluetoothTest.TAG, "BluetoothReceiver onReceive " + action);
            switch (action.hashCode()) {
                case -1780914469:
                    if (action.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -1765782003:
                    if (action.equals("android.bluetooth.sap.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case -1676167190:
                    if (action.equals("android.bluetooth.pbap.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case -1530327060:
                    if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1021360715:
                    if (action.equals("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 15;
                        break;
                    }
                    c = 65535;
                    break;
                case -843338808:
                    if (action.equals("android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 16;
                        break;
                    }
                    c = 65535;
                    break;
                case -612790895:
                    if (action.equals("android.bluetooth.hearingaid.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case -223687943:
                    if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -83749707:
                    if (action.equals("android.bluetooth.pbapclient.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 6759640:
                    if (action.equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 40146574:
                    if (action.equals("android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 448318136:
                    if (action.equals("android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 545516589:
                    if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case 655892284:
                    if (action.equals("android.bluetooth.mapmce.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 19;
                        break;
                    }
                    c = 65535;
                    break;
                case 921398788:
                    if (action.equals("android.bluetooth.hiddevice.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 17;
                        break;
                    }
                    c = 65535;
                    break;
                case 1123270207:
                    if (action.equals("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 1167529923:
                    if (action.equals("android.bluetooth.device.action.FOUND")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1244161670:
                    if (action.equals("android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case 1347806984:
                    if (action.equals("android.bluetooth.avrcp-controller.profile.action.CONNECTION_STATE_CHANGED")) {
                        c = 18;
                        break;
                    }
                    c = 65535;
                    break;
                case 2116862345:
                    if (action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                    Log.d(BluetoothTest.TAG, "BluetoothReceiver ACTION_STATE_CHANGED state = " + state);
                    if (state == 12) {
                        BluetoothTest.this.mBluetoothCallback.onBtPower(true);
                        return;
                    } else if (state == 10) {
                        BluetoothTest.this.mBluetoothCallback.onBtPower(false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    if (BluetoothTest.this.isDiscovering()) {
                        BluetoothTest.this.mBluetoothCallback.onScanStatus(true);
                        return;
                    }
                    return;
                case 2:
                    if (!BluetoothTest.this.isDiscovering()) {
                        BluetoothTest.this.mBluetoothCallback.onScanStatus(false);
                        return;
                    }
                    return;
                case AudioRecorder.IN_CALL_RECORD_ERROR /* 3 */:
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    int rssi = intent.getShortExtra("android.bluetooth.device.extra.RSSI", Short.MIN_VALUE);
                    BluetoothTest.this.mBluetoothCallback.onScanCallback(device, rssi);
                    return;
                case 4:
                    BluetoothDevice device2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    BluetoothTest.this.mBluetoothCallback.onBtPairStatus(intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -1), device2);
                    return;
                case 5:
                    BluetoothDevice device3 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    int variant = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", -1);
                    int passkey = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", -1);
                    BluetoothTest.this.mBluetoothCallback.onPairRequest(device3, variant, passkey);
                    return;
                case 6:
                    BluetoothTest.this.handleConnectionAction(intent, 22);
                    return;
                case 7:
                    BluetoothTest.this.handleConnectionAction(intent, 17);
                    return;
                case '\b':
                    BluetoothTest.this.handleConnectionAction(intent, 16);
                    return;
                case '\t':
                    BluetoothTest.this.handleConnectionAction(intent, 11);
                    return;
                case '\n':
                    BluetoothTest.this.handleConnectionAction(intent, 2);
                    return;
                case 11:
                    BluetoothTest.this.handleConnectionAction(intent, 10);
                    return;
                case '\f':
                    BluetoothTest.this.handleConnectionAction(intent, 6);
                    return;
                case '\r':
                    BluetoothTest.this.handleConnectionAction(intent, 1);
                    return;
                case 14:
                    BluetoothTest.this.handleConnectionAction(intent, 21);
                    return;
                case 15:
                    BluetoothTest.this.handleConnectionAction(intent, 4);
                    return;
                case 16:
                    BluetoothTest.this.handleConnectionAction(intent, 5);
                    return;
                case 17:
                    BluetoothTest.this.handleConnectionAction(intent, 19);
                    return;
                case 18:
                    BluetoothTest.this.handleConnectionAction(intent, 12);
                    return;
                case 19:
                    BluetoothTest.this.handleConnectionAction(intent, 18);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConnectionAction(Intent intent, int profile) {
        BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
        if (state == 2) {
            getProfileProxy(profile);
        }
        int preState = intent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", Integer.MIN_VALUE);
        this.mBluetoothCallback.onBtConnectStatus(device, state, preState, profile);
    }

    public int getState() {
        return this.mAdapter.getState();
    }

    public boolean enable() {
        if (!this.mAdapter.isEnabled()) {
            return this.mAdapter.enable();
        }
        return true;
    }

    public boolean disable() {
        if (this.mAdapter.isEnabled()) {
            return this.mAdapter.disable();
        }
        return true;
    }

    public boolean isConnected() {
        return this.mAdapter.getConnectionState() == 2;
    }

    public int getConnectedDevicesSize() {
        int size = 0;
        if (this.mAdapter.getConnectionState() == 2) {
            Set<BluetoothDevice> devices = this.mAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                if (device.isConnected()) {
                    Log.d(TAG, "connected: " + device.getName());
                    size++;
                }
            }
        }
        return size;
    }

    public BluetoothDevice getConnectedDevice() {
        if (this.mAdapter.getConnectionState() != 2) {
            return null;
        }
        Set<BluetoothDevice> devices = this.mAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            if (device.isConnected()) {
                Log.d(TAG, "connected: " + device.getName());
                return device;
            }
        }
        return null;
    }

    public String getMacAddress() {
        String mac = this.mAdapter.getAddress();
        return mac;
    }

    boolean factoryReset() {
        return this.mAdapter.factoryReset();
    }

    public boolean isDiscovering() {
        return this.mAdapter.isDiscovering();
    }

    public boolean startDiscovery() {
        return this.mAdapter.startDiscovery();
    }

    public boolean cancelDiscovery() {
        return this.mAdapter.cancelDiscovery();
    }

    public boolean isBondedDevice(BluetoothDevice device) {
        return this.mAdapter.getBondedDevices().contains(device);
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return this.mAdapter.getRemoteDevice(address);
    }

    public void pairOrAcceptPair(BluetoothDevice device, boolean shouldPair) {
        Log.d(TAG, "pairOrAcceptPair shouldPair = " + shouldPair + " device  = " + device);
        if (!this.mAdapter.isEnabled()) {
            Log.d(TAG, "pairOrAcceptPair Bluetooth is not enabled");
            return;
        }
        int state = device.getBondState();
        Log.d(TAG, "pairOrAcceptPair state = " + state);
        switch (state) {
            case 10:
                if (this.mAdapter.getBondedDevices().contains(device)) {
                    Log.d(TAG, "pairOrAcceptPair bluetooth bonded with device, but device dont BOND_NONE");
                    return;
                } else if (shouldPair && !device.createBond()) {
                    Log.d(TAG, "pairOrAcceptPair createBond fail");
                    return;
                } else {
                    return;
                }
            case 11:
                Log.d(TAG, "pairOrAcceptPair BOND_BONDING");
                return;
            case 12:
                if (!this.mAdapter.getBondedDevices().contains(device)) {
                    Log.d(TAG, "pairOrAcceptPair bluetooth dont bonded with device, but device dont BOND_BONDED");
                    return;
                }
                return;
            default:
                Log.d(TAG, "pairOrAcceptPair invalid state: state=" + state);
                return;
        }
    }

    public void unpair(BluetoothDevice device) {
        if (!this.mAdapter.isEnabled()) {
            Log.d(TAG, "unpair Bluetooth is not enabled");
            return;
        }
        int state = device.getBondState();
        Log.d(TAG, "pairOrAcceptPair state = " + state);
        switch (state) {
            case 10:
                Log.d(TAG, "unpair bluetooth device state BOND_NONE");
                return;
            case 11:
                Log.d(TAG, "unpair bluetooth device state BOND_BONDING");
                device.removeBond();
                return;
            case 12:
                Log.d(TAG, "unpair bluetooth device state BOND_BONDED");
                if (this.mAdapter.getBondedDevices().contains(device)) {
                    device.removeBond();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void connectProfile(BluetoothDevice device, int profile) {
        if (!this.mAdapter.isEnabled()) {
            Log.d(TAG, "connectProfile bluetooth not enabled");
        } else if (!this.mAdapter.getBondedDevices().contains(device)) {
            Log.d(TAG, "connectProfile bluetooth not bonded with device");
        } else {
            BluetoothPbapClient proxy = null;
            if (profile == 1) {
                proxy = this.mHeadset;
            } else if (profile == 2) {
                proxy = this.mA2dp;
            } else if (profile == 4) {
                proxy = this.mHidHost;
            } else if (profile == 5) {
                proxy = this.mPan;
            } else if (profile != 6) {
                if (profile != 21) {
                    switch (profile) {
                        case 10:
                            proxy = this.mSap;
                            break;
                        case 11:
                            proxy = this.mA2dpSink;
                            break;
                        case 12:
                            proxy = this.mAvrcpController;
                            break;
                        default:
                            switch (profile) {
                                case 16:
                                    proxy = this.mHeadsetClient;
                                    break;
                                case 17:
                                    proxy = this.mPbapClient;
                                    break;
                                case 18:
                                    proxy = this.mMapClient;
                                    break;
                                case 19:
                                    proxy = this.mHidDevice;
                                    break;
                            }
                    }
                } else {
                    proxy = this.mHearingAid;
                }
            } else {
                proxy = this.mPbap;
            }
            if (proxy == null) {
                Log.d(TAG, "connectProfile proxy == null");
                return;
            }
            int state = proxy.getConnectionState(device);
            Log.d(TAG, "connectProfile state = " + state);
            if (state != 0) {
                if (state == 1) {
                    Log.d(TAG, "connectProfile STATE_CONNECTING");
                    return;
                } else if (state == 2) {
                    Log.d(TAG, "connectProfile STATE_CONNECTED");
                    return;
                } else if (state != 3) {
                    Log.d(TAG, "connectProfile invalid state");
                    return;
                }
            }
            Log.d(TAG, "connectProfile STATE_DISCONNECTED or STATE_DISCONNECTING profile = " + profile);
            if (profile == 17) {
                proxy.connect(device);
            } else if (profile == 16) {
                ((BluetoothHeadsetClient) proxy).connect(device);
            } else if (profile == 11) {
                proxy.connect(device);
            } else if (profile == 1) {
                ((BluetoothHeadset) proxy).connect(device);
            } else if (profile == 2) {
                ((BluetoothA2dp) proxy).connect(device);
            } else if (profile != 6) {
                if (profile == 10) {
                    proxy.connect(device);
                } else if (profile == 21) {
                    ((BluetoothHearingAid) proxy).connect(device);
                } else if (profile == 18) {
                    proxy.connect(device);
                } else if (profile == 4) {
                    proxy.connect(device);
                } else if (profile == 5) {
                    proxy.connect(device);
                } else if (profile == 19) {
                    ((BluetoothHidDevice) proxy).connect(device);
                }
            }
        }
    }

    public void disconnectDevice(BluetoothDevice device) {
        for (int profile = 1; profile <= 21; profile++) {
            disconnectProfile(device, profile);
        }
    }

    public void disconnectProfile(BluetoothDevice device, int profile) {
        if (!this.mAdapter.isEnabled()) {
            Log.d(TAG, "disconnectProfile bluetooth not enabled");
        } else if (!this.mAdapter.getBondedDevices().contains(device)) {
            Log.d(TAG, "disconnectProfile bluetooth not bonded with device");
        } else {
            BluetoothPbapClient proxy = null;
            if (profile == 1) {
                proxy = this.mHeadset;
            } else if (profile == 2) {
                proxy = this.mA2dp;
            } else if (profile == 4) {
                proxy = this.mHidHost;
            } else if (profile == 5) {
                proxy = this.mPan;
            } else if (profile != 6) {
                if (profile != 21) {
                    switch (profile) {
                        case 10:
                            proxy = this.mSap;
                            break;
                        case 11:
                            proxy = this.mA2dpSink;
                            break;
                        case 12:
                            proxy = this.mAvrcpController;
                            break;
                        default:
                            switch (profile) {
                                case 16:
                                    proxy = this.mHeadsetClient;
                                    break;
                                case 17:
                                    proxy = this.mPbapClient;
                                    break;
                                case 18:
                                    proxy = this.mMapClient;
                                    break;
                                case 19:
                                    proxy = this.mHidDevice;
                                    break;
                            }
                    }
                } else {
                    proxy = this.mHearingAid;
                }
            } else {
                proxy = this.mPbap;
            }
            if (proxy == null) {
                Log.d(TAG, "disconnectProfile proxy == null");
                return;
            }
            int state = proxy.getConnectionState(device);
            Log.d(TAG, "disconnectProfile state = " + state);
            if (state == 0) {
                Log.d(TAG, "disconnectProfile STATE_DISCONNECTED");
            } else if (state != 1 && state != 2) {
                if (state == 3) {
                    Log.d(TAG, "disconnectProfile STATE_DISCONNECTING");
                } else {
                    Log.d(TAG, "disconnectProfile invalid state");
                }
            } else {
                Log.d(TAG, "disconnectProfile STATE_CONNECTED or STATE_CONNECTING profile = " + profile);
                if (profile == 17) {
                    proxy.disconnect(device);
                } else if (profile == 16) {
                    ((BluetoothHeadsetClient) proxy).disconnect(device);
                } else if (profile == 11) {
                    proxy.disconnect(device);
                } else if (profile == 1) {
                    ((BluetoothHeadset) proxy).disconnect(device);
                } else if (profile == 2) {
                    ((BluetoothA2dp) proxy).disconnect(device);
                } else if (profile == 6) {
                    proxy.disconnect(device);
                } else if (profile == 10) {
                    proxy.disconnect(device);
                } else if (profile == 21) {
                    ((BluetoothHearingAid) proxy).disconnect(device);
                } else if (profile == 18) {
                    proxy.disconnect(device);
                } else if (profile == 4) {
                    proxy.disconnect(device);
                } else if (profile == 5) {
                    proxy.disconnect(device);
                } else if (profile == 19) {
                    ((BluetoothHidDevice) proxy).disconnect(device);
                }
            }
        }
    }
}
