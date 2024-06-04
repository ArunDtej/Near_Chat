package com.bruhdev.myapplication.ConnectionManager;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.bruhdev.myapplication.UiManagers.ScanManager;
import com.bruhdev.myapplication.Util;

import java.util.logging.Handler;

public class ManageConnection {
    public  static ManageConnection instance;
    public  AcceptThread at;
    public ConnectThread ct = null;

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
        ct = new ConnectThread(device);
        ct.start();
    }

    public synchronized void acceptConnection(){
        at = AcceptThread.getInstance();
        try {
            if (!AcceptThread.isAccepting) {
                Util.lg("Called set to accepting: is not accepting");
                at.start();

                Util.lg("Accepting");
            }
            Util.lg("Called set to accepting: is accepting");

        }catch (Exception e){
            Util.lg(""+e);
        }
    }

    public synchronized void stopAccepting(){
        if(AcceptThread.isAccepting) {
            at.cancel();
        }
    }
}
