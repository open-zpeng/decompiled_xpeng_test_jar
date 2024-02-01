package com.xpeng.test;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.util.List;
/* loaded from: classes.dex */
public class WifiTest {
    private static final String TAG = "WifiTest";
    private static final int WIFICIPHER_NOPASS = 1;
    private static final int WIFICIPHER_WEP = 2;
    private static final int WIFICIPHER_WPA = 3;
    private WifiManager.ActionListener mConnectListener;
    private Context mContext;
    private EventCallback mEventCallback;
    private WifiManager.ActionListener mForgetListener;
    private WiFiEventListener mListener;
    private WifiManager mWifiManager;

    /* loaded from: classes.dex */
    public interface WiFiEventListener {
        void onFailure(int i);

        void onSuccess();
    }

    /* loaded from: classes.dex */
    class EventCallback implements WifiManager.ActionListener {
        EventCallback() {
        }

        public void onSuccess() {
            WifiTest.this.mListener.onSuccess();
        }

        public void onFailure(int reason) {
            WifiTest.this.mListener.onFailure(reason);
        }
    }

    public WifiTest(Context context) {
        this.mContext = context;
        this.mEventCallback = new EventCallback();
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public boolean setWifiEnabled(boolean enabled) {
        return this.mWifiManager.setWifiEnabled(enabled);
    }

    public boolean isWifiEnabled() {
        return this.mWifiManager.isWifiEnabled();
    }

    public void connect(int networkId, WiFiEventListener listener) {
        this.mListener = listener;
        this.mWifiManager.connect(networkId, this.mEventCallback);
    }

    public WifiTest(Context context, WifiManager.ActionListener connectListener, WifiManager.ActionListener forgetListener) {
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mConnectListener = connectListener;
        this.mForgetListener = forgetListener;
    }

    public void onInit() {
        this.mWifiManager.setWifiEnabled(true);
    }

    public void openWifi() {
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(true);
        }
    }

    public void closeWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        }
    }

    public int checkState() {
        return this.mWifiManager.getWifiState();
    }

    public String getMacAddress() {
        WifiInfo wifiInfo;
        String macAddress = "null";
        if (this.mWifiManager != null && (wifiInfo = this.mWifiManager.getConnectionInfo()) != null) {
            macAddress = wifiInfo.getMacAddress();
        }
        Log.d(TAG, "getWlanLocalAddr : " + macAddress);
        return macAddress;
    }

    public String getSSID() {
        WifiInfo wifiInfo;
        String ssid = "null";
        if (this.mWifiManager != null && (wifiInfo = this.mWifiManager.getConnectionInfo()) != null) {
            ssid = wifiInfo.getSSID();
        }
        Log.d(TAG, "getSSID : " + ssid);
        return ssid;
    }

    public void addNetwork(WifiConfiguration wcg) {
        this.mWifiManager.connect(wcg, this.mConnectListener);
    }

    public WifiConfiguration createWifiInfo(String SSID) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + SSID + "\"";
        config.allowedKeyManagement.set(0);
        return config;
    }

    public WifiConfiguration createWifiInfo(String ssid, String passward, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            this.mWifiManager.removeNetwork(tempConfig.networkId);
        }
        switch (type) {
            case 1:
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(0);
                config.wepTxKeyIndex = 0;
                break;
            case 2:
                config.hiddenSSID = true;
                String[] strArr = config.wepKeys;
                strArr[0] = "\"" + passward + "\"";
                config.allowedAuthAlgorithms.set(1);
                config.allowedGroupCiphers.set(3);
                config.allowedGroupCiphers.set(2);
                config.allowedGroupCiphers.set(0);
                config.allowedGroupCiphers.set(1);
                config.allowedKeyManagement.set(0);
                config.wepTxKeyIndex = 0;
                break;
            case 3:
                config.preSharedKey = "\"" + passward + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(0);
                config.allowedGroupCiphers.set(2);
                config.allowedKeyManagement.set(1);
                config.allowedPairwiseCiphers.set(1);
                config.allowedGroupCiphers.set(3);
                config.allowedPairwiseCiphers.set(2);
                config.status = 2;
                break;
        }
        return config;
    }

    public void forgetWifi(String SSID) {
        WifiConfiguration tempConfig = isExist(SSID);
        if (tempConfig != null) {
            this.mWifiManager.forget(tempConfig.networkId, this.mForgetListener);
        }
    }

    public WifiConfiguration isExist(String SSID) {
        List<WifiConfiguration> existingConfigs = this.mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                String str = existingConfig.SSID;
                if (str.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
            return null;
        }
        return null;
    }

    public void forgetAllWifi() {
        Log.d(TAG, "forgetAllWifi");
        List<WifiConfiguration> existingConfigs = this.mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                Log.d(TAG, "forgetAllWifi : " + existingConfig.SSID);
                this.mWifiManager.forget(existingConfig.networkId, this.mForgetListener);
            }
        }
    }

    public String getWlanLocalAddr() {
        WifiInfo wifiInfo;
        String macAddress = "null";
        if (this.mWifiManager != null && (wifiInfo = this.mWifiManager.getConnectionInfo()) != null) {
            macAddress = wifiInfo.getMacAddress();
        }
        Log.d(TAG, "getWlanLocalAddr : " + macAddress);
        return macAddress;
    }

    public int getIPAddress() {
        WifiInfo wifiInfo;
        int ip = 0;
        if (this.mWifiManager != null && (wifiInfo = this.mWifiManager.getConnectionInfo()) != null) {
            ip = wifiInfo.getIpAddress();
        }
        Log.d(TAG, "getIPAddress : " + ip);
        return ip;
    }

    public WifiInfo getWifiInfo() {
        if (this.mWifiManager.isWifiEnabled()) {
            return this.mWifiManager.getConnectionInfo();
        }
        return null;
    }
}
