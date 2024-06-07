package com.bruhdev.myapplication.ConnectionManager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.bruhdev.myapplication.IOManager.MyBluetoothServices;
import com.bruhdev.myapplication.Util;

import java.io.IOException;

@SuppressLint("MissingPermission")
class ConnectThread extends Thread {
    static BluetoothSocket mmSocket;

    public boolean isConnected = false;

    public ConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;

        if(Util.isBluetoothEnabled() && Util.isLocationEnabled(Util.context)) {
            try {
                tmp = device.createRfcommSocketToServiceRecord(Util.MY_UUID);
                Util.lg("Socket created");
            } catch (IOException e) {
                Util.lg("Socket's create() method failed: " + e);
            }
            ConnectThread.mmSocket = tmp;
        }else{
            Toast.makeText(Util.context, "Enable Bluetooth & Location and try again 🤡", Toast.LENGTH_SHORT).show();
        }
    }

    public void run() {

        try {
            if(Util.isBluetoothEnabled() && Util.isLocationEnabled(Util.context)) {
                Util.lg(" Both are enabled");
                Util.adapter.cancelDiscovery();
                isConnected = true;
                ConnectThread.mmSocket.connect();
                Util.safeInsert(ConnectThread.mmSocket.getRemoteDevice());
                Util.connectedAs = "Sender";
                ManageConnection.isConnected = true;
                ManageConnection.mbs = new MyBluetoothServices(mmSocket);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Util.activity, "Connection Successful", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(Util.context, "Enable Bluetooth & Location and try again 🤡", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException connectException) {

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Util.activity, "Connection failed 😔", Toast.LENGTH_SHORT).show();
                }
            });
            Util.lg("connect thread run 37: " + connectException);
            return;
        }

    }

    public void cancel() {
        try {
            ConnectThread.mmSocket.close();
        } catch (IOException e) {
            Util.lg( "Could not close the client socket: "+ e);
        }
    }
}