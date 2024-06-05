package com.bruhdev.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruhdev.myapplication.DBManager.BluetoothProfile;

import java.util.List;

public class Messenger extends AppCompatActivity {

    LinearLayout profilesLayout ;
    public List<BluetoothProfile> profiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        profilesLayout = findViewById(R.id.profilesLayout);
        Util.track(this, Messenger.this);

        refresh();

    }

    public void refresh(){
        Util.lg(" Messenger refresh ");
        new Thread(()->{
            profiles = Util.getBluetoothProfiles();
            for(int i = 0;i<profiles.size(); i++){
                Util.lg(profiles.get(i).getDeviceName() + " size : "+ profiles.size());

                LinearLayout temp = createProfileItem(profiles.get(i));
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        profilesLayout.addView(temp);
                    }
                });


            }
        }).start();
    }

    private LinearLayout createProfileItem(BluetoothProfile p) {

        String profileName = p.getPreferredDeviceName();
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setPadding(16, 18, 16, 18);
        horizontalLayout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8); // margin for separation between items
        horizontalLayout.setLayoutParams(params);

        // Create a FrameLayout to hold the profile picture
        FrameLayout profilePictureFrame = new FrameLayout(this);
        int imageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        profilePictureFrame.setLayoutParams(imageParams);

        TextView initialTextView = new TextView(this);
        initialTextView.setText(String.valueOf(profileName.charAt(0)));
        initialTextView.setTextColor(Color.WHITE);
        initialTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        initialTextView.setGravity(Gravity.CENTER);
        initialTextView.setTypeface(null, Typeface.BOLD);

        // Set background drawable for profile picture
        GradientDrawable background = new GradientDrawable();
        background.setColor(Util.getCustomColor(this));
        background.setShape(GradientDrawable.OVAL);
        profilePictureFrame.setBackground(background);

        // Add the initial TextView to the FrameLayout
        profilePictureFrame.addView(initialTextView);

        horizontalLayout.addView(profilePictureFrame);

        TextView textView = new TextView(this);
        textView.setText(profileName);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setPadding(32, 16, 32, 16);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        horizontalLayout.addView(textView);


        horizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BluetoothDevice device = Util.adapter(p.deviceAddress);
//                makeConnection(device, Messenger.this, Util.MY_UUID);
//                startChat(p);
            }
        });

        horizontalLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                DeleteChat(p.deviceAddress);
                return true;
            }
        });
        return horizontalLayout;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}