package com.xpeng.test;

import android.content.Context;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;

/* loaded from: classes.dex */
public class StorageTest {
    final String TAG = "StorageTest";
    private Context mContext;
    private StorageManager mStorageManager;

    private static native String native_get_blk_devices();

    public StorageTest() {
    }

    public StorageTest(Context context) {
        this.mContext = context;
        this.mStorageManager = StorageManager.from(this.mContext);
    }

    public VolumeInfo[] getStorageVolumes() {
        return null;
    }

    public DiskInfo[] getDisks() {
        return null;
    }

    public String getVolumeState(String volumeLabel) {
        StorageVolume[] storageVolumes = this.mStorageManager.getVolumeList();
        for (StorageVolume storageVolume : storageVolumes) {
            if (storageVolume.getDescription(this.mContext).toUpperCase().contains(volumeLabel)) {
                String storagePath = storageVolume.getPath();
                return this.mStorageManager.getVolumeState(storagePath);
            }
        }
        return null;
    }

    public String getVolumePath(String volumeLabel) {
        StorageVolume[] storageVolumes = this.mStorageManager.getVolumeList();
        for (StorageVolume storageVolume : storageVolumes) {
            if (storageVolume.getDescription(this.mContext).toUpperCase().contains(volumeLabel)) {
                return storageVolume.getPath();
            }
        }
        return null;
    }
}
