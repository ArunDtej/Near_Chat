package com.bruhdev.myapplication.IOManager;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.bruhdev.myapplication.ConnectionManager.ManageConnection;
import com.bruhdev.myapplication.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyBluetoothServices {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler handler = new Handler(Looper.getMainLooper());

    public final ConnectedThread ct;

    public MyBluetoothServices(BluetoothSocket socket) {
        handler = new Handler(Looper.getMainLooper());
        ct = new ConnectedThread(socket);
        ct.start();
    }

    public boolean send(String msg){
        return ct.write(msg.getBytes());
    }


    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Util.lg("Error occurred when creating input stream" + e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Util.lg("Error occurred when creating output stream" + e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;

            while (true) {
                try {
                    numBytes = mmInStream.read(mmBuffer);
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                    String message = new String((byte[]) readMsg.obj, 0, readMsg.arg1);
                    Util.lg(" Received message: " + message);

                    ManageConnection.isConnected = true;
                    Util.lg(" Connection Stable");
                } catch (IOException e) {
                    ManageConnection.isConnected = false;
                    Util.lg(" Input stream was disconnected " + e);
                    break;
                }
                catch (Exception e){
                    Util.lg(" Input stream er " + e);
                }
            }
        }

        public boolean write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();

            } catch (IOException e) {
                Util.lg("Error occurred when sending data" + e);
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
                return false;
            }
            return true;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Util.lg("Could not close the connect socket" + e);
            }
        }
    }
}
