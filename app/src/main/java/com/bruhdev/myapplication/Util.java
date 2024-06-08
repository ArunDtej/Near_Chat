package com.bruhdev.myapplication;

import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import android.Manifest;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bruhdev.myapplication.DBManager.BluetoothProfile;
import com.bruhdev.myapplication.DBManager.BluetoothProfileDao;
import com.bruhdev.myapplication.DBManager.BluetoothProfileDatabase;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class Util {

    public final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public final static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    public static boolean InChat = false;

    public static Context appConext;
    public static Context context;
    public static Activity activity;

    public static BluetoothProfileDatabase database;
    public static BluetoothProfileDao dao;
    public static BluetoothDevice currentDevice ;
    public static String connectedAs = "";

    public static void lg(String message) {
        Log.d("Logged", message);
    }

    public static void setDatabase(){

        database = BluetoothProfileDatabase.getInstance();
        dao = database.bluetoothProfileDao();

    }

    public static void safeInsert(BluetoothDevice device){
        new Thread(() -> {
            dao.safeInsertOrUpdateProfile(Util.getBluetoothProfile(device));
        }).start();
    }

    public static void removeBluetoothProfile(String address){
        new Thread(() -> {
            dao.deleteProfileByAddress(address);
        }).start();
    }

    public static List<BluetoothProfile> getBluetoothProfiles(){
        return dao.getAllProfilesSortedByLastSeenTime();
    }

    public static BluetoothProfile getProfile(String address){
        return dao.getProfileByAddress(address);
    }

    public static void updatePreferredName(String address,String name){
        new Thread(() -> {
            dao.updatePreferredName(address, name);
        }).start();
    }

    public static BluetoothProfile getBluetoothProfile(BluetoothDevice device){
        BluetoothProfile profile = new BluetoothProfile();
        profile.setDeviceAddress(device.getAddress());
        profile.setDeviceName(device.getName());
        profile.setPreferredDeviceName(device.getName());
        profile.setLastSeenTime(System.currentTimeMillis());
        return profile;
    }


    private static boolean isBluetoothPermissionRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    static boolean CheckPermissions(Activity context) {
        if (isBluetoothPermissionRequired()) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;


    }

    public static boolean isLocationEnabled(Context main){
        LocationManager locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public static void EnableBluetooth(Activity main){
        if (!isBluetoothEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            main.startActivityForResult(enableBtIntent, 10);
        }
    }
    public static void EnableLocation(Activity main){
        if (!isLocationEnabled(main)){
            AlertDialog dialog = new AlertDialog.Builder(main)
                    .setTitle("Enable Location")
                    .setMessage("Location services are required for this app. Please enable location.")
                    .setPositiveButton("Enable", (dialogInterface, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        main.startActivityForResult(intent, 20);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                        Toast.makeText(main, "Location is required for Bluetooth discovery.", Toast.LENGTH_SHORT).show();
                    })
                    .create();

            dialog.setOnShowListener(dialogInterface -> {

                int nightModeFlags = Util.activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Util.activity, R.color.white));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Util.activity, R.color.white));
                        break;

                    case Configuration.UI_MODE_NIGHT_NO:
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Util.activity, R.color.black));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Util.activity, R.color.black));
                        break;
                }

            });

            dialog.show();

        }
    }


    public static void track(Context context, Activity activity) {

        Util.activity = activity;
        Util.context = context;

    }

    public static int getCustomColor(Context context){

        Set<Integer> numberSet = new HashSet<>();

        numberSet.add(ContextCompat.getColor(context, R.color.itemyellow));
        numberSet.add(ContextCompat.getColor(context, R.color.itemgreen));
        numberSet.add(ContextCompat.getColor(context, R.color.itemblue));
        numberSet.add(ContextCompat.getColor(context, R.color.itempink));
        numberSet.add(ContextCompat.getColor(context, R.color.itemorange));
        numberSet.add(ContextCompat.getColor(context, R.color.itemskyblue));

        Integer[] numbersArray = numberSet.toArray(new Integer[0]);
        Random random = new Random();
        int randomIndex = random.nextInt(numbersArray.length);

        return numbersArray[randomIndex];
    }

    public static void vibrateDevice() {

        try {
            Vibrator vibrator = (Vibrator) Util.context.getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
            }
        }catch (Exception e){
            Util.lg(" in vibrate: " +e);
        }

    }

}

