package com.example.a730n.fairguidingsystem;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MapActivity extends AppCompatActivity {

    Button device, beacon0, beacon1, beacon2, beacon3;
    Button[] beacons;


    //this is needed for calling move function every x seconds:
    Handler timeHandler = new Handler();
    int delay = 1*1000; //1 second=1000 milisecond, 1*1000=5seconds
    Runnable runnable;

    //this is for testing TODO: delete
    int xx = 40;
    int yy = 40;


    //Position of beacons //TODO: implenent real height and width of room
    int beacon0x = 20;
    int beacon0y = 20;
    int beacon1x = 1000;
    int beacon1y = 20;
    int beacon2x = 20;
    int beacon2y = 1600;
    int beacon3x = 1000;
    int beacon3y = 1600;
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        //mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        //Intantialize Beacons (&device) and put them in list
        device = findViewById(R.id.device);
        beacons = new Button[4];
        beacon0 = findViewById(R.id.beacon0);
        beacons[0] = beacon0;
        beacon1 = findViewById(R.id.beacon1);
        beacons[1] = beacon1;
        beacon2 = findViewById(R.id.beacon2);
        beacons[2] = beacon2;
        beacon3 = findViewById(R.id.beacon3);
        beacons[3] = beacon3;

        placeBeacons(beacons, device);



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }


    //Änderung


    //call move-function every x seconds
    @Override
    protected void onResume() {
        //start handler as activity become visible

        timeHandler.postDelayed(new Runnable() {
            public void run() {
                //this is done every x seconds:
                Toast.makeText(getApplicationContext(), "Current delay: 1 second", Toast.LENGTH_SHORT).show();
                //Move Client device
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(20,20);
                lp.setMargins(xx,yy,0,0);
                device.setLayoutParams(lp);
                xx+=10;yy+=10;

                runnable=this;

                timeHandler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }


    //stop moving when map is not visible
    @Override
    protected void onPause() {
        timeHandler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    private void placeBeacons(Button[] beacons, Button device) {
        //place device
        FrameLayout.LayoutParams lpDevice = new FrameLayout.LayoutParams(device.getLayoutParams());
        lpDevice.setMargins(500,800,0,0);
        device.setLayoutParams(lpDevice);
        //beacon 0
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(beacon0.getLayoutParams());
        lp.setMargins(beacon0x,beacon0y,0,0);
        beacons[0].setLayoutParams(lp);

        //beacon1
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(beacon1.getLayoutParams());
        lp1.setMargins(beacon1x,beacon1y,0,0);
        beacons[1].setLayoutParams(lp1);

        //beacon2
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(beacon2.getLayoutParams());
        lp2.setMargins(beacon2x,beacon2y,0,0);
        beacons[2].setLayoutParams(lp2);

        //beacon3
        FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(beacon3.getLayoutParams());
        lp3.setMargins(beacon3x,beacon3y,0,0);
        beacons[3].setLayoutParams(lp3);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
