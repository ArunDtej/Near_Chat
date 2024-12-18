package com.bruhdev.myapplication.UiManagers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.bruhdev.myapplication.Chat;
import com.bruhdev.myapplication.ConnectionManager.ManageConnection;
import com.bruhdev.myapplication.MainActivity;
import com.bruhdev.myapplication.R;
import com.bruhdev.myapplication.Util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressLint("MissingPermission")
public class ScanManager {

    MainActivity main ;
    Context context;
    ManageConnection mc ;

    private Set<BluetoothDevice> DiscoveredDevice;
    private Set<BluetoothDevice> PairedDevice = new HashSet<>();


    public synchronized void  setMain(MainActivity main){
        this.main = main;
    }

    public synchronized void setContext(Context context){
        this.context = context;
        mc = ManageConnection.getInstance();
    }

    public synchronized void getPairedDevices( MainActivity main) {
        PairedDevice.clear();
        try {
            MainActivity.hori.removeAllViews();
            Set<BluetoothDevice> pairedDevices = Util.adapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device != null && device.getName() != null) {
                        LinearLayout temp = getPairedItem(device);
                        MainActivity.hori.addView(temp);
                        PairedDevice.add(device);
                    }
                }
            }
        }catch (Exception e){
            Util.lg("Sm : 76 "+ e);
            Util.lg((Util.context == null)+"");
        }
        return;
    }

    public synchronized void getDiscoveredDevices( MainActivity main) {
        if (Util.adapter.isDiscovering()) {
            Util.adapter.cancelDiscovery();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        main.registerReceiver(receiver, filter);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean started = Util.adapter.startDiscovery();
            }
        }, 2500);
        return;
    }

    public final  BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null && !isPresent(device) &&!isPaired(device)) {
                    TextView temp = getDiscoveredItem(device);
                    MainActivity.vert.addView(temp);

                }
            }
        }
    };

    private synchronized LinearLayout createProfileView(Context context, String name) {
        LinearLayout profileLayout = new LinearLayout(context);
        profileLayout.setOrientation(LinearLayout.VERTICAL);
        profileLayout.setGravity(Gravity.CENTER);
        int pad = 50;
        profileLayout.setPadding(pad, pad, pad, pad);
        profileLayout.setLayoutParams(new LinearLayout.LayoutParams(
                350,
                450));

        GradientDrawable drawable = new GradientDrawable();
        int color = Util.getCustomColor(context);
        drawable.setColor(color);
        drawable.setCornerRadius(85);
        profileLayout.setBackground(drawable);


        ImageView profileImage = new ImageView(context);
        profileImage.setLayoutParams(new LinearLayout.LayoutParams(
                175, 175));
        profileImage.setImageDrawable(createLetterDrawable(name.substring(0, 1)));
        profileImage.setPadding(0, 0, 0, 20);

        TextView textView = new TextView(context);
        textView.setText(name);
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 10, 0, 0);
        textView.setTextColor(Color.WHITE);
        textView.setHorizontallyScrolling(true);

        profileLayout.addView(profileImage);
        profileLayout.addView(textView);

        return profileLayout;
    }

    private Drawable createLetterDrawable(String letter) {
        int size = 130;
        int backgroundColor = Color.WHITE; // Choose your background color
        int textColor = Color.BLUE;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        paint.setColor(textColor);
        paint.setTextSize(size / 2);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(letter, 0, 1, bounds);

        int x = size / 2;
        int y = (size / 2) - (bounds.centerY());
        canvas.drawText(letter, x, y, paint);
        return new BitmapDrawable(main.getResources(), bitmap);
    }

    LinearLayout getPairedItem(BluetoothDevice device){

        final String name = device.getName();
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setBackgroundColor(Color.TRANSPARENT);
        mainLayout.setPadding(16, 16, 16, 16);


        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProfileClick(device);
                try {
                    Intent i = new Intent(Util.activity, Chat.class);
                    i.putExtra("Address", device.getAddress());
                    i.putExtra("Name", name);
                    Util.context.startActivity(i);
                }catch (Exception e){
                    Util.lg(" in trying to open unregistered device scan manager "+ e);
                }
            }
        });
        mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                RemoveDevice(device);
                return true;
            }
        });

        LinearLayout profileLayout = createProfileView(main, device.getName());

        mainLayout.addView(profileLayout);
        return mainLayout;
    }

    TextView getDiscoveredItem(BluetoothDevice device){
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(35); // Radius for rounded corners
        gradientDrawable.setColor(Color.WHITE); // Background color

        TextView textView = new TextView(main);
        textView.setText(device.getName()+", add: "+device.getAddress());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(16);
        textView.setPadding(25, 25, 25, 25);

        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 5, 0, 10);
        textView.setLayoutParams(params);

        textView.setBackground(gradientDrawable);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Util.vibrateDevice();
                onProfileClick(device);
            }
        });
        return textView;
    }

    void RemoveDevice(BluetoothDevice deviceToRemove){

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to remove pair?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                for (BluetoothDevice device : pairedDevices) {
                    if (device.equals(deviceToRemove)) {
                        try {
                            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                            method.invoke(device, (Object[]) null);
                        } catch (Exception e) {
                            Log.d("Logged", e+"");
                            main.refresh();
                        }
                        break;
                    }
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        int nightModeFlags = Util.context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Util.context, R.color.white));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Util.context, R.color.white));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(Util.context, R.color.white));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(Util.context, R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(Util.context, R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(Util.context, R.color.black));
                break;
        }
    }

    boolean isPresent(BluetoothDevice device){
        int len = MainActivity.vert.getChildCount();
        for(int i = 0;i<len;i++) {
            View temp = MainActivity.vert.getChildAt(0);
            String x = ((TextView) temp).getText().toString();
            if (x.matches(device.getName() + ", add: " + device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    boolean isPaired(BluetoothDevice device){
        return PairedDevice.contains(device);
    }

    public void onProfileClick(BluetoothDevice device){
        try {
            if (Util.isLocationEnabled(Util.context) && Util.isBluetoothEnabled()) {
                mc = ManageConnection.getInstance();
                Util.currentDevice = device;
                mc.reqConnection(device);
            }
            else{
                Toast.makeText(Util.context, "Enable Bluetooth & Location and try again 🤡", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Util.lg(" 309 : "+ e);
        }
    }


}

