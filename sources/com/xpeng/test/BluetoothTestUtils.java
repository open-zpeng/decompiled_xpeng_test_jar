package com.xpeng.test;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHidHost;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class BluetoothTestUtils {
    private static final int CONNECT_DISCONNECT_PROFILE_TIMEOUT = 20000;
    private static final int CONNECT_PROXY_TIMEOUT = 5000;
    private static final int DISCOVERABLE_UNDISCOVERABLE_TIMEOUT = 5000;
    private static final int ENABLE_DISABLE_TIMEOUT = 20000;
    private static final int PAIR_UNPAIR_TIMEOUT = 20000;
    private static final int POLL_TIME = 100;
    private static final int START_STOP_SCAN_TIMEOUT = 5000;
    private static final int START_STOP_SCO_TIMEOUT = 10000;
    private BluetoothA2dp mA2dp;
    private Context mContext;
    private BluetoothHeadset mHeadset;
    private BluetoothHidHost mInput;
    private String mOutputFile;
    private BufferedWriter mOutputWriter;
    private BluetoothPan mPan;
    private List<BroadcastReceiver> mReceivers;
    private BluetoothProfile.ServiceListener mServiceListener;
    private String mTag;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class FlagReceiver extends BroadcastReceiver {
        private int mExpectedFlags;
        private int mFiredFlags = 0;
        private long mCompletedTime = -1;

        public FlagReceiver(int expectedFlags) {
            this.mExpectedFlags = 0;
            this.mExpectedFlags = expectedFlags;
        }

        public int getFiredFlags() {
            int i;
            synchronized (this) {
                i = this.mFiredFlags;
            }
            return i;
        }

        public long getCompletedTime() {
            long j;
            synchronized (this) {
                j = this.mCompletedTime;
            }
            return j;
        }

        protected void setFiredFlag(int flag) {
            synchronized (this) {
                this.mFiredFlags |= flag;
                if ((this.mFiredFlags & this.mExpectedFlags) == this.mExpectedFlags) {
                    this.mCompletedTime = System.currentTimeMillis();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BluetoothReceiver extends FlagReceiver {
        private static final int DISCOVERY_FINISHED_FLAG = 2;
        private static final int DISCOVERY_STARTED_FLAG = 1;
        private static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE_FLAG = 16;
        private static final int SCAN_MODE_CONNECTABLE_FLAG = 8;
        private static final int SCAN_MODE_NONE_FLAG = 4;
        private static final int STATE_OFF_FLAG = 32;
        private static final int STATE_ON_FLAG = 128;
        private static final int STATE_TURNING_OFF_FLAG = 256;
        private static final int STATE_TURNING_ON_FLAG = 64;

        public BluetoothReceiver(int expectedFlags) {
            super(expectedFlags);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.DISCOVERY_STARTED".equals(intent.getAction())) {
                setFiredFlag(1);
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(intent.getAction())) {
                setFiredFlag(2);
            } else if ("android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(intent.getAction())) {
                int mode = intent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", -1);
                if (mode == -1) {
                    throw new IllegalArgumentException("mode= " + mode);
                } else if (mode == 20) {
                    setFiredFlag(SCAN_MODE_NONE_FLAG);
                } else if (mode == 21) {
                    setFiredFlag(SCAN_MODE_CONNECTABLE_FLAG);
                } else if (mode == 23) {
                    setFiredFlag(SCAN_MODE_CONNECTABLE_DISCOVERABLE_FLAG);
                }
            } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
                int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
                if (state == -1) {
                    throw new IllegalArgumentException("state= " + state);
                }
                switch (state) {
                    case 10:
                        setFiredFlag(STATE_OFF_FLAG);
                        return;
                    case 11:
                        setFiredFlag(STATE_TURNING_ON_FLAG);
                        return;
                    case 12:
                        setFiredFlag(STATE_ON_FLAG);
                        return;
                    case 13:
                        setFiredFlag(STATE_TURNING_OFF_FLAG);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PairReceiver extends FlagReceiver {
        private static final int STATE_BONDED_FLAG = 1;
        private static final int STATE_BONDING_FLAG = 2;
        private static final int STATE_NONE_FLAG = 4;
        private BluetoothDevice mDevice;
        private int mPasskey;
        private byte[] mPin;

        public PairReceiver(BluetoothDevice device, int passkey, byte[] pin, int expectedFlags) {
            super(expectedFlags);
            this.mDevice = device;
            this.mPasskey = passkey;
            this.mPin = pin;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!this.mDevice.equals(intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"))) {
                return;
            }
            if ("android.bluetooth.device.action.PAIRING_REQUEST".equals(intent.getAction())) {
                int varient = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", -1);
                if (varient == -1) {
                    throw new IllegalArgumentException("varient= " + varient);
                }
                if (varient != 0) {
                    if (varient == 1) {
                        this.mDevice.setPasskey(this.mPasskey);
                        return;
                    } else if (varient == 2 || varient == 3) {
                        this.mDevice.setPairingConfirmation(true);
                        return;
                    } else if (varient == 6) {
                        this.mDevice.setRemoteOutOfBandData();
                        return;
                    } else if (varient != 7) {
                        return;
                    }
                }
                this.mDevice.setPin(this.mPin);
            } else if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(intent.getAction())) {
                int state = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -1);
                if (state == -1) {
                    throw new IllegalArgumentException("state= " + state);
                }
                switch (state) {
                    case 10:
                        setFiredFlag(STATE_NONE_FLAG);
                        return;
                    case 11:
                        setFiredFlag(2);
                        return;
                    case 12:
                        setFiredFlag(1);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectProfileReceiver extends FlagReceiver {
        private static final int STATE_CONNECTED_FLAG = 4;
        private static final int STATE_CONNECTING_FLAG = 2;
        private static final int STATE_DISCONNECTED_FLAG = 1;
        private static final int STATE_DISCONNECTING_FLAG = 8;
        private String mConnectionAction;
        private BluetoothDevice mDevice;
        private int mProfile;

        public ConnectProfileReceiver(BluetoothDevice device, int profile, int expectedFlags) {
            super(expectedFlags);
            this.mDevice = device;
            this.mProfile = profile;
            int i = this.mProfile;
            if (i == 1) {
                this.mConnectionAction = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
            } else if (i == 2) {
                this.mConnectionAction = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";
            } else if (i == STATE_CONNECTED_FLAG) {
                this.mConnectionAction = "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED";
            } else if (i == 5) {
                this.mConnectionAction = "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED";
            } else {
                this.mConnectionAction = null;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String str = this.mConnectionAction;
            if (str == null || !str.equals(intent.getAction()) || !this.mDevice.equals(intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"))) {
                return;
            }
            int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
            if (state == -1) {
                throw new IllegalArgumentException("state= " + state);
            } else if (state != 0) {
                if (state == 1) {
                    setFiredFlag(2);
                } else if (state == 2) {
                    setFiredFlag(STATE_CONNECTED_FLAG);
                } else if (state == 3) {
                    setFiredFlag(STATE_DISCONNECTING_FLAG);
                }
            } else {
                setFiredFlag(1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectPanReceiver extends ConnectProfileReceiver {
        private int mRole;

        public ConnectPanReceiver(BluetoothDevice device, int role, int expectedFlags) {
            super(device, 5, expectedFlags);
            this.mRole = role;
        }

        @Override // com.xpeng.test.BluetoothTestUtils.ConnectProfileReceiver, android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (this.mRole != intent.getIntExtra("android.bluetooth.pan.extra.LOCAL_ROLE", -1)) {
                return;
            }
            super.onReceive(context, intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class StartStopScoReceiver extends FlagReceiver {
        private static final int STATE_CONNECTED_FLAG = 1;
        private static final int STATE_DISCONNECTED_FLAG = 2;

        public StartStopScoReceiver(int expectedFlags) {
            super(expectedFlags);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                int state = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", -1);
                if (state == -1) {
                    throw new IllegalArgumentException("state= " + state);
                } else if (state == 0) {
                    setFiredFlag(2);
                } else if (state == 1) {
                    setFiredFlag(1);
                }
            }
        }
    }

    public BluetoothTestUtils(Context context, String tag) {
        this(context, tag, null);
    }

    public BluetoothTestUtils(Context context, String tag, String outputFile) {
        this.mServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.xpeng.test.BluetoothTestUtils.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                synchronized (this) {
                    if (profile == 1) {
                        BluetoothTestUtils.this.mHeadset = (BluetoothHeadset) proxy;
                    } else if (profile == 2) {
                        BluetoothTestUtils.this.mA2dp = (BluetoothA2dp) proxy;
                    } else if (profile == 4) {
                        BluetoothTestUtils.this.mInput = (BluetoothHidHost) proxy;
                    } else if (profile == 5) {
                        BluetoothTestUtils.this.mPan = (BluetoothPan) proxy;
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int profile) {
                synchronized (this) {
                    if (profile == 1) {
                        BluetoothTestUtils.this.mHeadset = null;
                    } else if (profile == 2) {
                        BluetoothTestUtils.this.mA2dp = null;
                    } else if (profile == 4) {
                        BluetoothTestUtils.this.mInput = null;
                    } else if (profile == 5) {
                        BluetoothTestUtils.this.mPan = null;
                    }
                }
            }
        };
        this.mReceivers = new ArrayList();
        this.mA2dp = null;
        this.mHeadset = null;
        this.mInput = null;
        this.mPan = null;
        this.mContext = context;
        this.mTag = tag;
        this.mOutputFile = outputFile;
        if (this.mOutputFile == null) {
            this.mOutputWriter = null;
            return;
        }
        try {
            this.mOutputWriter = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory(), this.mOutputFile), true));
        } catch (IOException e) {
            Log.w(this.mTag, "Test output file could not be opened", e);
            this.mOutputWriter = null;
        }
    }

    public void close() {
        while (!this.mReceivers.isEmpty()) {
            this.mContext.unregisterReceiver(this.mReceivers.remove(0));
        }
        BufferedWriter bufferedWriter = this.mOutputWriter;
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                Log.w(this.mTag, "Test output file could not be closed", e);
            }
        }
    }

    public void enable(BluetoothAdapter adapter) {
        writeOutput("Enabling Bluetooth adapter.");
        if (adapter.isEnabled()) {
            throw new IllegalArgumentException("adapter.isEnabled()= " + adapter.isEnabled());
        }
        int btState = adapter.getState();
        final Semaphore completionSemaphore = new Semaphore(0);
        BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.xpeng.test.BluetoothTestUtils.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!"android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
                    return;
                }
                int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                if (state == 12) {
                    completionSemaphore.release();
                }
            }
        };
        IntentFilter filter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(receiver, filter);
        if (!adapter.enable()) {
            throw new IllegalArgumentException("adapter.enable()= false");
        }
        boolean success = false;
        try {
            success = completionSemaphore.tryAcquire(20000L, TimeUnit.MILLISECONDS);
            writeOutput(String.format("enable() completed in 0 ms", new Object[0]));
        } catch (InterruptedException e) {
        }
        this.mContext.unregisterReceiver(receiver);
        if (!success) {
            throw new IllegalArgumentException(String.format("enable() timeout: state=%d (expected %d)", Integer.valueOf(btState), 12));
        }
    }

    public void disable(BluetoothAdapter adapter) {
        writeOutput("Disabling Bluetooth adapter.");
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException("adapter.isEnabled()= false");
        }
        int btState = adapter.getState();
        final Semaphore completionSemaphore = new Semaphore(0);
        BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.xpeng.test.BluetoothTestUtils.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!"android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
                    return;
                }
                int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                if (state == 10) {
                    completionSemaphore.release();
                }
            }
        };
        IntentFilter filter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        this.mContext.registerReceiver(receiver, filter);
        if (!adapter.disable()) {
            throw new IllegalArgumentException("adapter.disable()= false");
        }
        boolean success = false;
        try {
            success = completionSemaphore.tryAcquire(20000L, TimeUnit.MILLISECONDS);
            writeOutput(String.format("disable() completed in 0 ms", new Object[0]));
        } catch (InterruptedException e) {
        }
        this.mContext.unregisterReceiver(receiver);
        if (!success) {
            throw new IllegalArgumentException(String.format("disable() timeout: state=%d (expected %d)", Integer.valueOf(btState), 10));
        }
    }

    public void discoverable(BluetoothAdapter adapter) {
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException("discoverable() bluetooth not enabled");
        }
        int scanMode = adapter.getScanMode();
        if (scanMode != 21) {
            return;
        }
        final Semaphore completionSemaphore = new Semaphore(0);
        BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.xpeng.test.BluetoothTestUtils.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!"android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(action)) {
                    return;
                }
                int mode = intent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", 20);
                if (mode == 23) {
                    completionSemaphore.release();
                }
            }
        };
        IntentFilter filter = new IntentFilter("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
        this.mContext.registerReceiver(receiver, filter);
        if (!adapter.setScanMode(23)) {
            throw new IllegalArgumentException("adapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)= false");
        }
        boolean success = false;
        try {
            success = completionSemaphore.tryAcquire(5000L, TimeUnit.MILLISECONDS);
            writeOutput(String.format("discoverable() completed in 0 ms", new Object[0]));
        } catch (InterruptedException e) {
        }
        this.mContext.unregisterReceiver(receiver);
        if (!success) {
            throw new IllegalArgumentException(String.format("discoverable() timeout: scanMode=%d (expected %d)", Integer.valueOf(scanMode), 23));
        }
    }

    public void undiscoverable(BluetoothAdapter adapter) {
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException("undiscoverable() bluetooth not enabled");
        }
        int scanMode = adapter.getScanMode();
        if (scanMode != 23) {
            return;
        }
        final Semaphore completionSemaphore = new Semaphore(0);
        BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.xpeng.test.BluetoothTestUtils.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!"android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(action)) {
                    return;
                }
                int mode = intent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", 20);
                if (mode == 21) {
                    completionSemaphore.release();
                }
            }
        };
        IntentFilter filter = new IntentFilter("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
        this.mContext.registerReceiver(receiver, filter);
        if (!adapter.setScanMode(21)) {
            throw new IllegalArgumentException("adapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE)= false");
        }
        boolean success = false;
        try {
            success = completionSemaphore.tryAcquire(5000L, TimeUnit.MILLISECONDS);
            writeOutput(String.format("undiscoverable() completed in 0 ms", new Object[0]));
        } catch (InterruptedException e) {
        }
        this.mContext.unregisterReceiver(receiver);
        if (!success) {
            throw new IllegalArgumentException(String.format("undiscoverable() timeout: scanMode=%d (expected %d)", Integer.valueOf(scanMode), 21));
        }
    }

    public void startScan(BluetoothAdapter adapter) {
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException("startScan() bluetooth not enabled");
        }
        if (adapter.isDiscovering()) {
            return;
        }
        BluetoothReceiver receiver = getBluetoothReceiver(1);
        long start = System.currentTimeMillis();
        if (!adapter.startDiscovery()) {
            throw new IllegalArgumentException("adapter.startDiscovery()= false");
        }
        while (System.currentTimeMillis() - start < 5000) {
            if (adapter.isDiscovering() && (receiver.getFiredFlags() & 1) == 1) {
                writeOutput(String.format("startScan() completed in %d ms", Long.valueOf(receiver.getCompletedTime() - start)));
                removeReceiver(receiver);
                return;
            }
            sleep(100L);
        }
        int firedFlags = receiver.getFiredFlags();
        removeReceiver(receiver);
        throw new IllegalArgumentException(String.format("startScan() timeout: isDiscovering=%b, flags=0x%x (expected 0x%x)", Boolean.valueOf(adapter.isDiscovering()), Integer.valueOf(firedFlags), 1));
    }

    public void stopScan(BluetoothAdapter adapter) {
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException("stopScan() bluetooth not enabled");
        }
        if (!adapter.isDiscovering()) {
            return;
        }
        BluetoothReceiver receiver = getBluetoothReceiver(2);
        long start = System.currentTimeMillis();
        if (!adapter.cancelDiscovery()) {
            throw new IllegalArgumentException("adapter.cancelDiscovery()= false");
        }
        while (System.currentTimeMillis() - start < 5000) {
            if (!adapter.isDiscovering() && (receiver.getFiredFlags() & 2) == 2) {
                writeOutput(String.format("stopScan() completed in %d ms", Long.valueOf(receiver.getCompletedTime() - start)));
                removeReceiver(receiver);
                return;
            }
            sleep(100L);
        }
        int firedFlags = receiver.getFiredFlags();
        removeReceiver(receiver);
        throw new IllegalArgumentException(String.format("stopScan() timeout: isDiscovering=%b, flags=0x%x (expected 0x%x)", Boolean.valueOf(adapter.isDiscovering()), Integer.valueOf(firedFlags), 2));
    }

    public void enablePan(BluetoothAdapter adapter) {
        if (this.mPan == null) {
            this.mPan = connectProxy(adapter, 5);
        }
        if (this.mPan == null) {
            throw new IllegalArgumentException("mPan= " + this.mPan);
        }
        long start = System.currentTimeMillis();
        this.mPan.setBluetoothTethering(true);
        long stop = System.currentTimeMillis();
        if (!this.mPan.isTetheringOn()) {
            throw new IllegalArgumentException("mPan.isTetheringOn()= false");
        }
        writeOutput(String.format("enablePan() completed in %d ms", Long.valueOf(stop - start)));
    }

    public void disablePan(BluetoothAdapter adapter) {
        if (this.mPan == null) {
            this.mPan = connectProxy(adapter, 5);
        }
        if (this.mPan == null) {
            throw new IllegalArgumentException("mPan= " + this.mPan);
        }
        long start = System.currentTimeMillis();
        this.mPan.setBluetoothTethering(false);
        long stop = System.currentTimeMillis();
        if (this.mPan.isTetheringOn()) {
            throw new IllegalArgumentException("mPan.isTetheringOn()= true");
        }
        writeOutput(String.format("disablePan() completed in %d ms", Long.valueOf(stop - start)));
    }

    public void pair(BluetoothAdapter adapter, BluetoothDevice device, int passkey, byte[] pin) {
        pairOrAcceptPair(adapter, device, passkey, pin, true);
    }

    public void acceptPair(BluetoothAdapter adapter, BluetoothDevice device, int passkey, byte[] pin) {
        pairOrAcceptPair(adapter, device, passkey, pin, false);
    }

    private void pairOrAcceptPair(BluetoothAdapter adapter, BluetoothDevice device, int passkey, byte[] pin, boolean shouldPair) {
        int mask = 3;
        long start = -1;
        String methodName = shouldPair ? String.format("pair(device=%s)", device) : String.format("acceptPair(device=%s)", device);
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException(String.format("%s bluetooth not enabled", methodName));
        }
        PairReceiver receiver = getPairReceiver(device, passkey, pin, 3);
        int state = device.getBondState();
        switch (state) {
            case 10:
                if (adapter.getBondedDevices().contains(device)) {
                    throw new IllegalArgumentException("adapter.getBondedDevices().contains(device)= true");
                }
                start = System.currentTimeMillis();
                if (shouldPair && !device.createBond()) {
                    throw new IllegalArgumentException("device.createBond()= false");
                }
                break;
            case 11:
                mask = 0;
                break;
            case 12:
                if (!adapter.getBondedDevices().contains(device)) {
                    throw new IllegalArgumentException("adapter.getBondedDevices().contains(device)= false");
                }
                return;
            default:
                removeReceiver(receiver);
                throw new IllegalArgumentException("fail: " + methodName + " invalid state: state=" + state);
        }
        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < 20000) {
            state = device.getBondState();
            if (state != 12 || (receiver.getFiredFlags() & mask) != mask) {
                sleep(100L);
            } else if (!adapter.getBondedDevices().contains(device)) {
                throw new IllegalArgumentException("adapter.getBondedDevices().contains(device)= false");
            } else {
                long finish = receiver.getCompletedTime();
                if (start == -1 || finish == -1) {
                    writeOutput(String.format("%s completed", methodName));
                } else {
                    writeOutput(String.format("%s completed in %d ms", methodName, Long.valueOf(finish - start)));
                }
                removeReceiver(receiver);
                return;
            }
        }
        int firedFlags = receiver.getFiredFlags();
        removeReceiver(receiver);
        throw new IllegalArgumentException(String.format("%s timeout: state=%d (expected %d), flags=0x%x (expected 0x%x)", methodName, Integer.valueOf(state), 12, Integer.valueOf(firedFlags), Integer.valueOf(mask)));
    }

    public void unpair(BluetoothAdapter adapter, BluetoothDevice device) {
        long start;
        String methodName = String.format("unpair(device=%s)", device);
        if (adapter.isEnabled()) {
            PairReceiver receiver = getPairReceiver(device, 0, null, 4);
            int state = device.getBondState();
            switch (state) {
                case 10:
                    if (adapter.getBondedDevices().contains(device)) {
                        throw new IllegalArgumentException("adapter.getBondedDevices().contains(device)= true");
                    }
                    removeReceiver(receiver);
                    return;
                case 11:
                    start = System.currentTimeMillis();
                    if (!device.removeBond()) {
                        throw new IllegalArgumentException("device.removeBond()= false");
                    }
                    break;
                case 12:
                    if (!adapter.getBondedDevices().contains(device)) {
                        throw new IllegalArgumentException("adapter.getBondedDevices().contains(device)= false");
                    }
                    start = System.currentTimeMillis();
                    if (!device.removeBond()) {
                        throw new IllegalArgumentException("device.removeBond()= false");
                    }
                    break;
                default:
                    removeReceiver(receiver);
                    throw new IllegalArgumentException(String.format("%s invalid state: state=%d", methodName, Integer.valueOf(state)));
            }
            long s = System.currentTimeMillis();
            while (System.currentTimeMillis() - s < 20000) {
                if (device.getBondState() == 10 && (receiver.getFiredFlags() & 4) == 4) {
                    if (adapter.getBondedDevices().contains(device)) {
                        throw new IllegalArgumentException("adapter.getBondedDevices().contains(device)= true");
                    }
                    long finish = receiver.getCompletedTime();
                    if (start == -1 || finish == -1) {
                        writeOutput(String.format("%s completed", methodName));
                    } else {
                        writeOutput(String.format("%s completed in %d ms", methodName, Long.valueOf(finish - start)));
                    }
                    removeReceiver(receiver);
                    return;
                }
            }
            int firedFlags = receiver.getFiredFlags();
            removeReceiver(receiver);
            throw new IllegalArgumentException(String.format("%s timeout: state=%d (expected %d), flags=0x%x (expected 0x%x)", methodName, Integer.valueOf(state), 12, Integer.valueOf(firedFlags), 4));
        }
        throw new IllegalArgumentException(String.format("%s bluetooth not enabled", methodName));
    }

    public void unpairAll(BluetoothAdapter adapter) {
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            unpair(adapter, device);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x00bb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void connectProfile(android.bluetooth.BluetoothAdapter r22, android.bluetooth.BluetoothDevice r23, int r24, java.lang.String r25) {
        /*
            Method dump skipped, instructions count: 378
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xpeng.test.BluetoothTestUtils.connectProfile(android.bluetooth.BluetoothAdapter, android.bluetooth.BluetoothDevice, int, java.lang.String):void");
    }

    public void disconnectProfile(BluetoothAdapter adapter, BluetoothDevice device, int profile, String methodName) {
        int i = 2;
        char c = 0;
        String methodName2 = methodName == null ? String.format("disconnectProfile(profile=%d, device=%s)", Integer.valueOf(profile), device) : methodName;
        int mask = 9;
        long start = -1;
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException(String.format("%s bluetooth not enabled", methodName2));
        }
        if (!adapter.getBondedDevices().contains(device)) {
            throw new IllegalArgumentException(String.format("%s device not paired", methodName2));
        }
        BluetoothProfile proxy = connectProxy(adapter, profile);
        if (proxy == null) {
            throw new IllegalArgumentException("proxy= " + proxy);
        }
        ConnectProfileReceiver receiver = getConnectProfileReceiver(device, profile, 9);
        int state = proxy.getConnectionState(device);
        if (state == 0) {
            removeReceiver(receiver);
            return;
        }
        if (state == 1 || state == 2) {
            start = System.currentTimeMillis();
            if (profile == 2) {
                if (!((BluetoothA2dp) proxy).disconnect(device)) {
                    throw new IllegalArgumentException("((BluetoothA2dp)proxy).disconnect(device)= false");
                }
            } else if (profile == 1) {
                if (!((BluetoothHeadset) proxy).disconnect(device)) {
                    throw new IllegalArgumentException("((BluetoothHeadset)proxy).disconnect(device)= false");
                }
            } else if (profile == 4 && !((BluetoothHidHost) proxy).disconnect(device)) {
                throw new IllegalArgumentException("((BluetoothHidHost)proxy).disconnect(device)= false");
            }
        } else if (state == 3) {
            mask = 0;
        } else {
            removeReceiver(receiver);
            throw new IllegalArgumentException(String.format("%s invalid state: state=%d", methodName2, Integer.valueOf(state)));
        }
        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < 20000) {
            state = proxy.getConnectionState(device);
            if (state != 0 || (receiver.getFiredFlags() & mask) != mask) {
                sleep(100L);
                i = 2;
                c = 0;
            } else {
                long finish = receiver.getCompletedTime();
                if (start != -1 && finish != -1) {
                    Object[] objArr = new Object[i];
                    objArr[c] = methodName2;
                    objArr[1] = Long.valueOf(finish - start);
                    writeOutput(String.format("%s completed in %d ms", objArr));
                } else {
                    Object[] objArr2 = new Object[1];
                    objArr2[c] = methodName2;
                    writeOutput(String.format("%s completed", objArr2));
                }
                removeReceiver(receiver);
                return;
            }
        }
        int firedFlags = receiver.getFiredFlags();
        removeReceiver(receiver);
        throw new IllegalArgumentException(String.format("%s timeout: state=%d (expected %d), flags=0x%x (expected 0x%x)", methodName2, Integer.valueOf(state), 0, Integer.valueOf(firedFlags), Integer.valueOf(mask)));
    }

    public void connectPan(BluetoothAdapter adapter, BluetoothDevice device) {
        connectPanOrIncomingPanConnection(adapter, device, true);
    }

    public void incomingPanConnection(BluetoothAdapter adapter, BluetoothDevice device) {
        connectPanOrIncomingPanConnection(adapter, device, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x00a4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void connectPanOrIncomingPanConnection(android.bluetooth.BluetoothAdapter r24, android.bluetooth.BluetoothDevice r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 350
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xpeng.test.BluetoothTestUtils.connectPanOrIncomingPanConnection(android.bluetooth.BluetoothAdapter, android.bluetooth.BluetoothDevice, boolean):void");
    }

    public void disconnectPan(BluetoothAdapter adapter, BluetoothDevice device) {
        disconnectFromRemoteOrVerifyConnectNap(adapter, device, true);
    }

    public void incomingPanDisconnection(BluetoothAdapter adapter, BluetoothDevice device) {
        disconnectFromRemoteOrVerifyConnectNap(adapter, device, false);
    }

    private void disconnectFromRemoteOrVerifyConnectNap(BluetoothAdapter adapter, BluetoothDevice device, boolean disconnect) {
        String methodName;
        int mask;
        int role;
        long start = -1;
        if (disconnect) {
            methodName = String.format("disconnectPan(device=%s)", device);
            mask = 9;
            role = 2;
        } else {
            methodName = String.format("incomingPanDisconnection(device=%s)", device);
            mask = 1;
            role = 1;
        }
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException(String.format("%s bluetooth not enabled", methodName));
        }
        if (!adapter.getBondedDevices().contains(device)) {
            throw new IllegalArgumentException(String.format("%s device not paired", methodName));
        }
        this.mPan = (BluetoothPan) connectProxy(adapter, 5);
        if (this.mPan == null) {
            throw new IllegalArgumentException("mPan= " + this.mPan);
        }
        ConnectPanReceiver receiver = getConnectPanReceiver(device, role, mask);
        int state = this.mPan.getConnectionState(device);
        if (state == 0) {
            removeReceiver(receiver);
            return;
        }
        int i = 2;
        if (state == 1 || state == 2) {
            start = System.currentTimeMillis();
            if (role == 2 && !this.mPan.disconnect(device)) {
                throw new IllegalArgumentException("mPan.disconnect(device)= false");
            }
        } else if (state == 3) {
            mask = 0;
        } else {
            removeReceiver(receiver);
            throw new IllegalArgumentException(String.format("%s invalid state: state=%d", methodName, Integer.valueOf(state)));
        }
        long s = System.currentTimeMillis();
        while (System.currentTimeMillis() - s < 20000) {
            state = this.mPan.getConnectionState(device);
            if (state != 0 || (receiver.getFiredFlags() & mask) != mask) {
                sleep(100L);
                s = s;
                i = 2;
            } else {
                long finish = receiver.getCompletedTime();
                if (start == -1 || finish == -1) {
                    writeOutput(String.format("%s completed", methodName));
                } else {
                    Object[] objArr = new Object[i];
                    objArr[0] = methodName;
                    objArr[1] = Long.valueOf(finish - start);
                    writeOutput(String.format("%s completed in %d ms", objArr));
                }
                removeReceiver(receiver);
                return;
            }
        }
        int firedFlags = receiver.getFiredFlags();
        removeReceiver(receiver);
        throw new IllegalArgumentException(String.format("%s timeout: state=%d (expected %d), flags=0x%x (expected 0x%s)", methodName, Integer.valueOf(state), 0, Integer.valueOf(firedFlags), Integer.valueOf(mask)));
    }

    public void startSco(BluetoothAdapter adapter, BluetoothDevice device) {
        startStopSco(adapter, device, true);
    }

    public void stopSco(BluetoothAdapter adapter, BluetoothDevice device) {
        startStopSco(adapter, device, false);
    }

    private void startStopSco(BluetoothAdapter adapter, BluetoothDevice device, boolean isStart) {
        String methodName;
        int mask;
        if (isStart) {
            methodName = String.format("startSco(device=%s)", device);
            mask = 1;
        } else {
            methodName = String.format("stopSco(device=%s)", device);
            mask = 2;
        }
        if (!adapter.isEnabled()) {
            throw new IllegalArgumentException(String.format("%s bluetooth not enabled", methodName));
        }
        if (!adapter.getBondedDevices().contains(device)) {
            throw new IllegalArgumentException(String.format("%s device not paired", methodName));
        }
        AudioManager manager = (AudioManager) this.mContext.getSystemService("audio");
        if (manager == null) {
            throw new IllegalArgumentException("manager == " + manager);
        } else if (!manager.isBluetoothScoAvailableOffCall()) {
            throw new IllegalArgumentException(String.format("%s device does not support SCO", methodName));
        } else {
            boolean isScoOn = manager.isBluetoothScoOn();
            if (isStart == isScoOn) {
                return;
            }
            StartStopScoReceiver receiver = getStartStopScoReceiver(mask);
            long start = System.currentTimeMillis();
            if (isStart) {
                manager.startBluetoothSco();
            } else {
                manager.stopBluetoothSco();
            }
            long s = System.currentTimeMillis();
            while (System.currentTimeMillis() - s < 10000) {
                isScoOn = manager.isBluetoothScoOn();
                if (isStart != isScoOn || (receiver.getFiredFlags() & mask) != mask) {
                    sleep(100L);
                } else {
                    long finish = receiver.getCompletedTime();
                    if (start == -1 || finish == -1) {
                        writeOutput(String.format("%s completed", methodName));
                    } else {
                        writeOutput(String.format("%s completed in %d ms", methodName, Long.valueOf(finish - start)));
                    }
                    removeReceiver(receiver);
                    return;
                }
            }
            int firedFlags = receiver.getFiredFlags();
            removeReceiver(receiver);
            throw new IllegalArgumentException(String.format("%s timeout: on=%b (expected %b), flags=0x%x (expected 0x%x)", methodName, Boolean.valueOf(isScoOn), Boolean.valueOf(isStart), Integer.valueOf(firedFlags), Integer.valueOf(mask)));
        }
    }

    public void writeOutput(String s) {
        Log.i(this.mTag, s);
        BufferedWriter bufferedWriter = this.mOutputWriter;
        if (bufferedWriter == null) {
            return;
        }
        try {
            bufferedWriter.write(s + "\n");
            this.mOutputWriter.flush();
        } catch (IOException e) {
            Log.w(this.mTag, "Could not write to output file", e);
        }
    }

    private void addReceiver(BroadcastReceiver receiver, String[] actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        this.mContext.registerReceiver(receiver, filter);
        this.mReceivers.add(receiver);
    }

    private BluetoothReceiver getBluetoothReceiver(int expectedFlags) {
        String[] actions = {"android.bluetooth.adapter.action.DISCOVERY_FINISHED", "android.bluetooth.adapter.action.DISCOVERY_STARTED", "android.bluetooth.adapter.action.SCAN_MODE_CHANGED", "android.bluetooth.adapter.action.STATE_CHANGED"};
        BluetoothReceiver receiver = new BluetoothReceiver(expectedFlags);
        addReceiver(receiver, actions);
        return receiver;
    }

    private PairReceiver getPairReceiver(BluetoothDevice device, int passkey, byte[] pin, int expectedFlags) {
        String[] actions = {"android.bluetooth.device.action.PAIRING_REQUEST", "android.bluetooth.device.action.BOND_STATE_CHANGED"};
        PairReceiver receiver = new PairReceiver(device, passkey, pin, expectedFlags);
        addReceiver(receiver, actions);
        return receiver;
    }

    private ConnectProfileReceiver getConnectProfileReceiver(BluetoothDevice device, int profile, int expectedFlags) {
        String[] actions = {"android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED", "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED", "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED"};
        ConnectProfileReceiver receiver = new ConnectProfileReceiver(device, profile, expectedFlags);
        addReceiver(receiver, actions);
        return receiver;
    }

    private ConnectPanReceiver getConnectPanReceiver(BluetoothDevice device, int role, int expectedFlags) {
        String[] actions = {"android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED"};
        ConnectPanReceiver receiver = new ConnectPanReceiver(device, role, expectedFlags);
        addReceiver(receiver, actions);
        return receiver;
    }

    private StartStopScoReceiver getStartStopScoReceiver(int expectedFlags) {
        String[] actions = {"android.media.ACTION_SCO_AUDIO_STATE_UPDATED"};
        StartStopScoReceiver receiver = new StartStopScoReceiver(expectedFlags);
        addReceiver(receiver, actions);
        return receiver;
    }

    private void removeReceiver(BroadcastReceiver receiver) {
        this.mContext.unregisterReceiver(receiver);
        this.mReceivers.remove(receiver);
    }

    private BluetoothProfile connectProxy(BluetoothAdapter adapter, int profile) {
        if (profile == 1) {
            BluetoothHeadset bluetoothHeadset = this.mHeadset;
            if (bluetoothHeadset != null) {
                return bluetoothHeadset;
            }
        } else if (profile == 2) {
            BluetoothA2dp bluetoothA2dp = this.mA2dp;
            if (bluetoothA2dp != null) {
                return bluetoothA2dp;
            }
        } else if (profile == 4) {
            BluetoothHidHost bluetoothHidHost = this.mInput;
            if (bluetoothHidHost != null) {
                return bluetoothHidHost;
            }
        } else if (profile != 5) {
            return null;
        } else {
            BluetoothPan bluetoothPan = this.mPan;
            if (bluetoothPan != null) {
                return bluetoothPan;
            }
        }
        adapter.getProfileProxy(this.mContext, this.mServiceListener, profile);
        long s = System.currentTimeMillis();
        if (profile == 1) {
            while (this.mHeadset == null && System.currentTimeMillis() - s < 5000) {
                sleep(100L);
            }
            return this.mHeadset;
        } else if (profile == 2) {
            while (this.mA2dp == null && System.currentTimeMillis() - s < 5000) {
                sleep(100L);
            }
            return this.mA2dp;
        } else if (profile == 4) {
            while (this.mInput == null && System.currentTimeMillis() - s < 5000) {
                sleep(100L);
            }
            return this.mInput;
        } else if (profile != 5) {
            return null;
        } else {
            while (this.mPan == null && System.currentTimeMillis() - s < 5000) {
                sleep(100L);
            }
            return this.mPan;
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
