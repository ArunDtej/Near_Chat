package com.bruhdev.myapplication.ConnectionManager;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.bruhdev.myapplication.IOManager.MyBluetoothServices;
import com.bruhdev.myapplication.UiManagers.ScanManager;
import com.bruhdev.myapplication.Util;

import java.util.logging.Handler;

public class ManageConnection {
    public  static ManageConnection instance;
    public  AcceptThread at;
    public ConnectThread ct;
    public static boolean isConnected = false;
    public static MyBluetoothServices mbs ;

    ManageConnection(){
        at  = AcceptThread.getInstance();
    }
    public static ManageConnection getInstance() {
        if (instance == null) {
            synchronized (ScanManager.class) {
                if (instance == null) {
                    instance = new ManageConnection();
                }
            }
        }
        return ManageConnection.instance;
    }

    public  void reqConnection(BluetoothDevice device){
        if (ct != null) {
            ct.cancel();
        }
        ct = new ConnectThread(device);
        ct.start();
    }

    public synchronized void acceptConnection(){
        at = AcceptThread.getInstance();
        try {
            if (!AcceptThread.isAccepting) {
                at.start();
            }
        }catch (Exception e){
            Util.lg(" ManageConnection " + e);
        }
    }

    public synchronized void stopAccepting(){
        if(AcceptThread.isAccepting) {
            at.cancel();
        }
    }

    public BluetoothSocket getCurrentSocket(){
        at = AcceptThread.getInstance();
        if(Util.connectedAs.matches("Sender")){
            return ConnectThread.mmSocket;
        }
        return AcceptThread.mainSocket;
    }
}
