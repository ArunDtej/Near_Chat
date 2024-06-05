package com.bruhdev.myapplication.DBManager;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bluetooth_profiles")
public class BluetoothProfile {

    @PrimaryKey
    @NonNull
    private String deviceAddress;
    private String deviceName;
    private String preferredDeviceName;
    private long lastSeenTime;

    @NonNull
    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(@NonNull String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public String getPreferredDeviceName() {
        return preferredDeviceName;
    }

    public void setPreferredDeviceName(String preferredDeviceName) {
        this.preferredDeviceName = preferredDeviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}

