package com.bruhdev.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruhdev.myapplication.ChatDB.ChatManager;
import com.bruhdev.myapplication.ChatDB.ChatMessage;
import com.bruhdev.myapplication.DBManager.BluetoothProfile;

import java.util.List;

public class Messenger extends AppCompatActivity {

    LinearLayout profilesLayout ;
    public List<BluetoothProfile> profiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Util.track(this, Messenger.this);

        profilesLayout = findViewById(R.id.profilesLayout);
        Util.track(this, Messenger.this);

        refresh();

    }

    public void refresh(){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                profilesLayout.removeAllViews();
            }
        }, 0);


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

        GradientDrawable background = new GradientDrawable();
        background.setColor(Util.getCustomColor(this));
        background.setShape(GradientDrawable.OVAL);
        profilePictureFrame.setBackground(background);

        profilePictureFrame.addView(initialTextView);

        horizontalLayout.addView(profilePictureFrame);

        TextView textView = new TextView(this);
        textView.setText(profileName);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setPadding(32, 16, 32, 16);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTag(p.getDeviceAddress());

        horizontalLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showCustomDialog(p.getDeviceAddress());
                return true;
            }
        });

        horizontalLayout.addView(textView);


        horizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Messenger.this, Chat.class);
                intent.putExtra("Address", p.getDeviceAddress());
                startActivity(intent);

            }
        });
        return horizontalLayout;
    }
    private AlertDialog dialog = null;

    private void showCustomDialog(String address) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        Button buttonRename = dialogView.findViewById(R.id.button_rename);
        Button buttonDelete = dialogView.findViewById(R.id.button_delete);

        buttonRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    rename(address);
                    dialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteChat(address);
                dialog.dismiss();
            }
        });

        dialog = dialogBuilder.create();
        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = 600;
        dialog.getWindow().setAttributes(layoutParams);
    }

    void DeleteChat(String deviceToRemove){
        AlertDialog.Builder builder = new AlertDialog.Builder(Messenger.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Delete contact and chat history?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Util.removeBluetoothProfile(deviceToRemove);
                ChatManager.deleteChat(deviceToRemove);
                refresh();
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

    }


    public void rename(String address){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save device as");


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (16 * getResources().getDisplayMetrics().density); // 16dp to pixels
        params.setMargins(margin, margin, margin, margin);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);


        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if (!newName.isEmpty()) {
                    new Thread(() -> {
                        BluetoothProfile profile = Util.getProfile(address);
                        profile.setPreferredDeviceName(newName);
                        Util.updatePreferredName(profile.getDeviceAddress(), newName);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refresh();
                            }
                        }, 0);

                    }).start();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.track(this, Messenger.this);
    }
}