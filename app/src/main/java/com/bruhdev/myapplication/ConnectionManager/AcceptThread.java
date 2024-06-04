package com.bruhdev.myapplication.ConnectionManager;

import static com.bruhdev.myapplication.MainActivity.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bruhdev.myapplication.Util;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Handler;

@SuppressLint("MissingPermission")
class AcceptThread extends Thread {
    public  static  AcceptThread instance = new AcceptThread();
    private final BluetoothServerSocket mmServerSocket;
    public static BluetoothSocket mainSocket;
    public static  boolean isAccepting = false;
    public static boolean accepted = false;
    ManageConnection mc = ManageConnection.getInstance();

    public AcceptThread() {
        BluetoothServerSocket tmp = null;
        try {
            tmp = Util.adapter.listenUsingRfcommWithServiceRecord("NAME", Util.MY_UUID);
        } catch (IOException e) {
            Util.lg( "Socket's listen() method failed: "+e);
        }
        mmServerSocket = tmp;
    }

    public static AcceptThread getInstance() {
        return instance;
    }

    public void run() {
        AcceptThread.accepted = false;
        BluetoothSocket socket = null;
        while (true) {
            AcceptThread.isAccepting = true;
            try {
                socket = mmServerSocket.accept();
                Util.lg( "Socket's accept() method Success: ");
            } catch (IOException e) {
                Util.lg( "Socket's accept() method failed: "+ e);
                break;
            }

            if (socket != null) {
                AcceptThread.isAccepting = true;
                displayDialog(socket);
                AcceptThread.accepted = true;
                Util.lg("Connection Accepted");
            }
        }
    }

    public void cancel() {
        try {
            mmServerSocket.close();
            AcceptThread.isAccepting = false;
            Util.lg("Accepting socket closed");
        } catch (IOException e) {
            Util.lg( "Could not close the connect socket: "+ e);
        }
    }

    synchronized void displayDialog(BluetoothSocket socket){

        mc = ManageConnection.getInstance();
        (Util.activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(Util.activity);
                builder.setTitle("Connection request");
                builder.setMessage("Connection request from: "+ socket.getRemoteDevice().getName());

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainSocket = socket;
                        accepted = true;
                        Toast.makeText(Util.context, ""+ isAccepting, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                mc.acceptConnection();

            }
        });

    }

    public static BluetoothSocket getMainSocket() {
        return mainSocket;
    }

}
