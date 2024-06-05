package com.bruhdev.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bruhdev.myapplication.DBManager.BluetoothProfile;

import java.util.List;

public class Messenger extends AppCompatActivity {

    public List<BluetoothProfile> profiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Util.track(this, Messenger.this);
        refresh();

    }

    public void refresh(){
        new Thread(()->{
            profiles = Util.getBluetoothProfiles();
            for(int i = 0;i<profiles.size(); i++){
                Util.lg(profiles.get(i).getDeviceName());
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}