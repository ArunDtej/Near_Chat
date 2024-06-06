package com.bruhdev.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bruhdev.myapplication.ConnectionManager.ManageConnection;
import com.bruhdev.myapplication.DBManager.BluetoothProfile;

public class Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView profileImage;
    private TextView toolbarTitle;
    private TextView statusText;
    private ImageButton send;
    private EditText messageInput;
    

    private String preferredName;
    private String address;
    public ManageConnection mc;

    public BluetoothConnectionChecker bcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Util.track(this, Chat.this);
        Util.InChat = true;

        toolbar = findViewById(R.id.Chattoolbar);
        profileImage = findViewById(R.id.profile_image);
        toolbarTitle = findViewById(R.id.toolbar_title);
        statusText = findViewById(R.id.status_text);
        messageInput = findViewById(R.id.messageInput);
        send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String msg = messageInput.getText().toString();
                    ManageConnection.mbs.send(msg);
                    messageInput.setText("");
                    
                }catch (Exception e){
                    Toast.makeText(Chat.this, "Message not send, reconnect and try again 😂", Toast.LENGTH_SHORT).show();
                    Util.lg(" "+ e);
                }
            }
        });

        Intent intent = getIntent();
        address = intent.getStringExtra("Address");
        setToolbarItems();

        GradientDrawable drawable = (GradientDrawable) profileImage.getBackground();
        drawable.setColor(Util.getCustomColor(this));

        try {
            bcc = new BluetoothConnectionChecker(Util.adapter, this);
            bcc.start();
        }catch (Exception e){
            Util.lg(" "+e);
        }

    }

    public void setToolbarItems(){

        new Thread(() -> {
            BluetoothProfile profile = Util.getProfile(address);
            preferredName = profile.getPreferredDeviceName();

            new Handler(Looper.getMainLooper()).post(() -> {
                String temp = preferredName;
                if (preferredName.length() > 22) {
                    temp = preferredName.substring(0, 18);
                }
                toolbarTitle.setText(temp);
                String initialLetter = preferredName.substring(0, 1);
                profileImage.setText(initialLetter);

                statusText.setText("Offline");
            });
        }).start();
    }

    void updateStatus(){

        try {
            mc = ManageConnection.getInstance();
            if (mc.getCurrentSocket() != null) {
                if (ManageConnection.isConnected) {
                    statusText.setText("Online");
                } else {
                    statusText.setText("Offline");
                }
            }
            else{
                Util.lg(" current socket is null");
            }
        }catch (Exception e){
            Util.lg(" "+ e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.track(this, Chat.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.InChat = false;
        bcc.stopChecker();
    }
}


@SuppressLint("MissingPermission")
class BluetoothConnectionChecker extends Thread {

    private static final String TAG = "BluetoothConnectionChecker";
    private volatile boolean running = true;
    private final Chat main;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @SuppressLint("MissingPermission")
    public BluetoothConnectionChecker(BluetoothAdapter bluetoothAdapter, Chat context) {
        this.main = context;
    }

    @Override
    public void run() {
        while (running) {
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        main.updateStatus();
                    }
                });
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Util.lg(" Error in BluetoothConnectionChecker: "+ e);
            }
        }
    }

    public void stopChecker() {
        running = false;
        this.interrupt(); // Ensure the thread exits promptly
    }
}



