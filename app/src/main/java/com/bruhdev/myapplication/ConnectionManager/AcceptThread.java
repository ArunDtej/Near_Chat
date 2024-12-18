package com.bruhdev.myapplication.ConnectionManager;

import static com.bruhdev.myapplication.MainActivity.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bruhdev.myapplication.IOManager.MyBluetoothServices;
import com.bruhdev.myapplication.MainActivity;
import com.bruhdev.myapplication.R;
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
                try{
                    Util.vibrateDevice();
                }catch (Exception e){
                    Util.lg(" message ");
                }
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
        Util.connectedAs = "Receiver";
        (Util.activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(Util.activity);
                builder.setTitle("Connection request");
                builder.setMessage("Connection request from: " + socket.getRemoteDevice().getName());

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainSocket = socket;
                        accepted = true;
                        ManageConnection.isConnected = true;
                        ManageConnection.mbs = new MyBluetoothServices(mainSocket);
                        try {
                            Util.safeInsert(mainSocket.getRemoteDevice());
                        } catch (Exception e) {
                            Util.lg(" at safe insert " + e);
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

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
                    }
                });
                dialog.show();

                mc.acceptConnection();

            }
        });

    }

    public static BluetoothSocket getMainSocket() {
        return mainSocket;
    }

}
