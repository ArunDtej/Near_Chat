package com.bruhdev.myapplication.ConnectionManager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.bruhdev.myapplication.Util;

import java.io.IOException;

@SuppressLint("MissingPermission")
class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(Util.MY_UUID);
            Util.lg("Socket created");
        } catch (IOException e) {
            Util.lg( "Socket's create() method failed: "+ e);
        }
        mmSocket = tmp;
    }

    public void run() {
        Util.adapter.cancelDiscovery();

        try {
            mmSocket.connect();
            Util.lg("Connedted");
        } catch (IOException connectException) {
            cancel();
            return;
        }

    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Util.lg( "Could not close the client socket: "+ e);
        }
    }
}