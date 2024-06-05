package com.bruhdev.myapplication.DBManager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.List;


@Dao
public interface BluetoothProfileDao {

    @Insert
    void insert(BluetoothProfile profile);

    @Update
    void update(BluetoothProfile profile);

    @Query("SELECT * FROM bluetooth_profiles WHERE deviceAddress = :deviceAddress LIMIT 1")
    BluetoothProfile getProfileByAddress(String deviceAddress);

    @Query("SELECT * FROM bluetooth_profiles ORDER BY lastSeenTime DESC")
    List<BluetoothProfile> getAllProfilesSortedByLastSeenTime();

    @Transaction
    default void safeInsertOrUpdateProfile(BluetoothProfile profile) {
        BluetoothProfile existingProfile = getProfileByAddress(profile.getDeviceAddress());
        if (existingProfile != null) {
            existingProfile.setLastSeenTime(profile.getLastSeenTime());
            update(existingProfile);
        } else {
            insert(profile);
        }
    }

    @Transaction
    default void updatePreferredName(String deviceAddress, String preferredName) {
        BluetoothProfile existingProfile = getProfileByAddress(deviceAddress);
        if (existingProfile != null) {
            existingProfile.setPreferredDeviceName(preferredName);
            update(existingProfile);
        }
    }
}
