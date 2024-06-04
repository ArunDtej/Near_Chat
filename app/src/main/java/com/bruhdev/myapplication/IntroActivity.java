package com.bruhdev.myapplication;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.listeners.IFinishListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class IntroActivity extends MaterialIntroActivity {

    private boolean isBluetoothPermissionRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private String[] getPermissions(){
        if(!isBluetoothPermissionRequired()) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        }
        return new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.MainBackground)
                        .buttonsColor(R.color.itemskyblue)
                        .title("Bluetooth and Location Permissions Required")
                        .description("This app requires Bluetooth and Location permissions to function properly. Please grant these permissions to continue using the app.")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openUrl("https://developer.android.com/develop/connectivity/bluetooth/bt-permissions");
                    }
                }, "Learn more?"));

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.MainBackground)
                        .buttonsColor(R.color.itemskyblue)
                        .neededPermissions(getPermissions())
                        .title("Grant permissions")
                        .description("Grant the necessary permissions for proper functionality, no info is being tracked or used by the developer!")
                        .build());
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if(Util.CheckPermissions(this)) {
            setResult(RESULT_OK);
        }
        else{
            setResult(RESULT_CANCELED);
        }
    }
}
