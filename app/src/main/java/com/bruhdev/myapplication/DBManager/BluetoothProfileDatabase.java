package com.bruhdev.myapplication.DBManager;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {BluetoothProfile.class}, version = 1, exportSchema = false)
public abstract class BluetoothProfileDatabase extends RoomDatabase {

    public abstract BluetoothProfileDao bluetoothProfileDao();

    private static volatile BluetoothProfileDatabase INSTANCE;

    public static BluetoothProfileDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BluetoothProfileDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    BluetoothProfileDatabase.class, "bluetooth_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

