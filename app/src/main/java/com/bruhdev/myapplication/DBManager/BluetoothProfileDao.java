package com.bruhdev.myapplication.DBManager;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
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
}
