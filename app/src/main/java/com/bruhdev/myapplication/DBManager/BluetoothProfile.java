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

    // Getters and setters
    // Constructor
}

