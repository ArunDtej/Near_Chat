package com.bruhdev.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruhdev.myapplication.DBManager.BluetoothProfile;

public class Chat extends AppCompatActivity {

    private Toolbar toolbar;
    private String preferredName;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Util.track(this, Chat.this);

        toolbar = findViewById(R.id.Chattoolbar);
        Intent intent = getIntent();
        address = intent.getStringExtra("Address");
        setToolbarItems();
    }

    public void setToolbarItems(){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);

// Create the TextView for the profile image
        TextView profileImage = new TextView(this);
        int size = (int) (40 * getResources().getDisplayMetrics().density); // 40dp to pixels
        LinearLayout.LayoutParams profileImageParams = new LinearLayout.LayoutParams(size, size);
        profileImageParams.setMargins(16, 0, 32, 0); // Set margins in pixels
        profileImage.setLayoutParams(profileImageParams);
        profileImage.setPadding(10, 10, 10, 10);
        profileImage.setGravity(Gravity.CENTER);
        profileImage.setTextSize(20);
        profileImage.setTextColor(Color.WHITE);

        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(Util.getCustomColor(this)); // Circle color
        profileImage.setBackground(circleDrawable);

        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);

        TextView toolbarTitle = new TextView(this);
        toolbarTitle.setTextColor(Color.WHITE);
        toolbarTitle.setTextSize(20);
        toolbarTitle.setTypeface(null, Typeface.BOLD);
        toolbarTitle.setGravity(Gravity.CENTER_VERTICAL);

        TextView statusText = new TextView(this);
        statusText.setTextColor(Color.WHITE);
        statusText.setTextSize(13);
//        statusText.setTypeface(null, Typeface.BOLD);
        statusText.setGravity(Gravity.CENTER_VERTICAL);

        verticalLayout.addView(toolbarTitle);
        verticalLayout.addView(statusText);

        layout.addView(profileImage);
        layout.addView(verticalLayout);

        Toolbar.LayoutParams toolbarLayoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(layout, toolbarLayoutParams);

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

//                boolean isOnline = profile.isOnline(); // Assuming you have a method to check if the profile is online
//                statusText.setText(isOnline ? "Online" : "Offline");
                statusText.setText("Offline");
            });
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.track(this, Chat.this);
    }
}
