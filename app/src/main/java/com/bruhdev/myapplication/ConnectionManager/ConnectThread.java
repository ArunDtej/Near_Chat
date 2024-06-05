package com.bruhdev.myapplication.ConnectionManager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.bruhdev.myapplication.Util;

import java.io.IOException;

@SuppressLint("MissingPermission")
class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;

    public boolean isConnected = false;

    public ConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;

        try {
            tmp = device.createRfcommSocketToServiceRecord(Util.MY_UUID);
            Util.lg("Socket created");
        } catch (IOException e) {
            Util.lg("Socket's create() method failed: " + e);
        }
        mmSocket = tmp;
    }

    public void run() {
        Util.adapter.cancelDiscovery();

        try {
            isConnected = true;
            mmSocket.connect();
//            Util.safeInsert(mmSocket.getRemoteDevice());
        } catch (IOException connectException) {
            Util.lg("connect thread run 37: " + connectException);
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