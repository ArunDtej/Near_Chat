package com.bruhdev.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruhdev.myapplication.ConnectionManager.ManageConnection;
import com.bruhdev.myapplication.UiManagers.ScanManager;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.concurrent.Delayed;


@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {

    private TextView welcome;
    private ProgressBar pbar;
    public static LinearLayout vert;
    public static LinearLayout hori;
    private TextView discoverable;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static Handler handler = new Handler();
    BluetoothAdapter adapter;

    ManageConnection  mc ;
    ScanManager sm = new ScanManager();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.appConext = getApplicationContext();

        welcome = findViewById(R.id.WelcomeText);
        pbar = findViewById(R.id.progressBar);
        MainActivity.vert = findViewById(R.id.DiscoveredScroller);
        hori = findViewById(R.id.PairedScroller);
        discoverable = findViewById(R.id.discovery_text);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);

        adapter = Util.adapter;

        Util.track(this, MainActivity.this);
        Util.setDB();

        discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); // 300 seconds
                startActivity(discoverableIntent);
            }
        });

        MainActivity.vert.setOnTouchListener(new View.OnTouchListener() {
            private float startY;
            private boolean isScrollingUp = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getY();
                        isScrollingUp = currentY < startY;
                        if (!isScrollingUp && MainActivity.vert.getScrollY() == 0) {
                            swipeRefreshLayout.setEnabled(true);
                        } else {
                            swipeRefreshLayout.setEnabled(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isScrollingUp) {
                            swipeRefreshLayout.setEnabled(false);
                        }
                        break;
                }
                return false;
            }
        });


        hori.setOnTouchListener(new View.OnTouchListener() {
            private float startY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getY();
                        if (currentY > startY) {
                            // Scrolling down
                            swipeRefreshLayout.setEnabled(false);
                        } else {
                            // Scrolling up
                            swipeRefreshLayout.setEnabled(MainActivity.vert.getScrollY() == 0);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refresh();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });


        if (!Util.CheckPermissions(this)) {
            startActivityForResult(new Intent(this, IntroActivity.class), 10);
        } else {

            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 1000);

            }catch (Exception e){
                Util.lg("Main activity "+ e);
            }

        }

    }

    public void refresh() {

        Util.EnableBluetooth(MainActivity.this);

        mc = ManageConnection.getInstance();
        mc.acceptConnection();

        sm.setMain(MainActivity.this);
        sm.setContext(this);

        try {
            Util.EnableBluetooth(this);
            Util.EnableLocation(this);
            if (adapter != null) {
                String deviceName = adapter.getName();
                welcome.setText("Hello " + deviceName);
            }

            try {
                sm.getDiscoveredDevices(this);
                sm.getPairedDevices(this);
            } catch (Exception e) {
                Util.lg("main 200:  " + e+ " is sm null : "+ (sm==null));
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    pbar.setVisibility(View.GONE);
                }
            };
            handler.postDelayed(runnable, 3000);
        }catch (Exception e){
            Util.lg("main : "+e);
        }

    }

    private boolean isFirstTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);
        if (isFirstTime) {
            preferences.edit().putBoolean("isFirstTime", false).apply();
        }
        return isFirstTime;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {

            if (resultCode == RESULT_OK) {

                if (isFirstTime()) {
                    TapTarget();
                }

                refresh();

            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }

        }
    }

    void TapTarget(){

        pbar.setVisibility(View.VISIBLE);

        TapTargetSequence tvs = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.discovery_text), "Discoverable", "Makes your discoverable when other devices start a scan")
                                .outerCircleColor(R.color.itemblue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),

                        TapTarget.forView(findViewById(R.id.connection_text), "Connected devices", "List of devices that are previously connected.")
                                .outerCircleColor(R.color.itemblue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),

                        TapTarget.forView(findViewById(R.id.progressBar), "Swipe Down to refresh", "Swipe down to refresh the screen and load devices ")
                                .outerCircleColor(R.color.itemblue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),

                        TapTarget.forView(findViewById(R.id.online_text), "Online devices", "List of devices that are accepting connections.")
                                .outerCircleColor(R.color.itemblue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60),

                        TapTarget.forView(findViewById(R.id.to_messages), "Messages", "Find your chat history with previously connected devices.")
                                .outerCircleColor(R.color.itemblue)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(20)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .targetRadius(60)
                )


                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        pbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        pbar.setVisibility(View.GONE);
                    }
                });
        tvs.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Util.track(this, MainActivity.this);
    }
}