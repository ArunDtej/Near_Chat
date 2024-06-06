package com.bruhdev.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruhdev.myapplication.ConnectionManager.ManageConnection;
import com.bruhdev.myapplication.DBManager.BluetoothProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Chat extends AppCompatActivity {

    private TextView profileImage;
    private TextView toolbarTitle;
    private TextView statusText;
    private ImageButton send;
    private EditText messageInput;
    LinearLayout ChatBox;
    ScrollView ChatScroller;
    

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

        profileImage = findViewById(R.id.profile_image);
        toolbarTitle = findViewById(R.id.toolbar_title);
        statusText = findViewById(R.id.status_text);
        messageInput = findViewById(R.id.messageInput);
        send = findViewById(R.id.send);
        ChatBox = findViewById(R.id.Chat);
        ChatScroller = findViewById(R.id.ChatScroller);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
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


    private void sendMessage() {
        try {
            String msg = messageInput.getText().toString();
            messageInput.setText("");

            //            ManageConnection.mbs.send(msg);

            LinearLayout temp = getSendMessage(msg);
            ChatBox.addView(temp);

            ChatScroller.post(new Runnable() {
                @Override
                public void run() {
                    ChatScroller.fullScroll(View.FOCUS_DOWN);
                }
            });

        } catch (Exception e) {
            Toast.makeText(Chat.this, "Message not send, reconnect and try again 😂", Toast.LENGTH_SHORT).show();
            Util.lg(" " + e);
        }
    }

    private LinearLayout getReceiveMessage(String msg){
        LinearLayout messageLayoutWrapper = new LinearLayout(this);
        messageLayoutWrapper.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayoutWrapper.setGravity(Gravity.START);

        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.VERTICAL);
        messageLayout.setPadding(25, 20, 25, 20);
        messageLayout.setBackgroundResource(R.drawable.rounded_bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 150, 10); // Adjust left margin to align to the left
        messageLayout.setLayoutParams(params);

        TextView messageTextView = new TextView(this);
        messageTextView.setText(msg);
        messageTextView.setTextSize(16);
        messageTextView.setTextColor(Color.WHITE);

        messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Chat.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", messageTextView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        long currentTimeMillis = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTimestamp = sdf.format(new Date(currentTimeMillis));

        TextView timestampTextView = new TextView(this);
        timestampTextView.setText(formattedTimestamp);
        timestampTextView.setTextSize(10);
        timestampTextView.setPadding(10,2,0,3); // Adjust padding for left alignment
        timestampTextView.setGravity(Gravity.LEFT); // Set gravity to left for timestamp
        timestampTextView.setTextColor(Color.WHITE);

        messageLayout.addView(messageTextView);
        messageLayout.addView(timestampTextView);
        messageLayoutWrapper.addView(messageLayout);
        return messageLayoutWrapper;
    }


    private LinearLayout getSendMessage(String msg){
        LinearLayout messageLayoutWrapper = new LinearLayout(this);
        messageLayoutWrapper.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayoutWrapper.setGravity(Gravity.END);

        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setOrientation(LinearLayout.VERTICAL);
        messageLayout.setPadding(25, 20, 25, 20);
        messageLayout.setBackgroundResource(R.drawable.rounded_bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(150, 10, 10, 10);
        messageLayout.setLayoutParams(params);

        TextView messageTextView = new TextView(this);
        messageTextView.setText(msg);
        messageTextView.setTextSize(16);
        messageTextView.setTextColor(Color.WHITE);

        messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Chat.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", messageTextView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        long currentTimeMillis = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTimestamp = sdf.format(new Date(currentTimeMillis));

        TextView timestampTextView = new TextView(this);
        timestampTextView.setText(formattedTimestamp);
        timestampTextView.setTextSize(10);
        timestampTextView.setPadding(0,2,10,3);
        timestampTextView.setGravity(Gravity.RIGHT);
        timestampTextView.setTextColor(Color.WHITE);

        messageLayout.addView(messageTextView);
        messageLayout.addView(timestampTextView);
        messageLayoutWrapper.addView(messageLayout);
        return messageLayoutWrapper;
    }



    private void setToolbarItems(){

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
                Thread.sleep(1000);
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
        this.interrupt();
    }
}



