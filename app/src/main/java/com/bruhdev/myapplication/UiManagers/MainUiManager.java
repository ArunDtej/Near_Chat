package com.bruhdev.myapplication.UiManagers;

import android.annotation.SuppressLint;


@SuppressLint("MissingPermission")
public class MainUiManager {
    private static MainUiManager instance = null;

    public static synchronized MainUiManager getInstance() {
        if (instance == null){
            instance = new MainUiManager();
        }
        return instance;
    }


}