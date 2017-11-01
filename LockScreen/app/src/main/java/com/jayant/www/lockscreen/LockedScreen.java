package com.jayant.www.lockscreen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;

public class LockedScreen extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    private TextView batteryStatus;

    private OverlayDialog mOverlayDialog;
    private OnLockStatusChangedListener mLockStatusChangedListener;

    private Button btnUnlock;

    private TextView txtTime;
    private Typeface TimeFace;
    private TextView txtDate;
    private ImageView st;
    private Handler mHandler;

    private LockscreenUtils mLockscreenUtils;

    public static boolean isService = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked_screen);


        startService(new Intent(this, LockScreenService.class).setAction(Intent.ACTION_SYNC));
        if (!isMyServiceRunning(LockScreenService.class)) {
            startService(new Intent(this, LockScreenService.class).setAction(Intent.ACTION_SYNC));
        }

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);


   /*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

       View view = (View) findViewById(R.id.layout);
        view.startAnimation(AnimationUtils.loadAnimation(
                context, R.anim.right_out;
        ));
*/
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);


        txtTime = (TextView) findViewById(R.id.textClock2);
        TimeFace = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        txtTime.setTypeface(TimeFace);
        txtDate = (TextView) findViewById(R.id.date);
        batteryStatus = (TextView) findViewById(R.id.test);

        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable, 500);

        init();
        reset();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private void init() {
        btnUnlock = (Button) findViewById(R.id.bnUnlock);
        btnUnlock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // unlock home button and then screen on button press
                finish();
            }
        });
    }


    private final Runnable m_Runnable = new Runnable() {
        public void run()

        {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM");
            Date d = new Date();
            String dayOfTheWeek = sdf.format(d);
            txtDate.setText(dayOfTheWeek);

            SimpleDateFormat ap = new SimpleDateFormat("HH");
            Date t = new Date();
            String dayTime = ap.format(t);
            st = (ImageView) findViewById(R.id.ampm);
            if (Integer.parseInt(dayTime) > Integer.parseInt("06") && Integer.parseInt(dayTime) < Integer.parseInt("18")) {
                st.setImageResource(R.drawable.sun);
            } else {
                st.setImageResource(R.drawable.moon);
            }


            BroadcastReceiver batteryLevel = new BroadcastReceiver() {

                public void onReceive(Context context, Intent intent) {
                    context.unregisterReceiver(this);
                    int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int level = -1;
                    if (currentLevel >= 0 && scale > 0) {
                        level = (currentLevel * 100) / scale;
                    }
                    batteryStatus.setText(level + "%");
                }
            };

            IntentFilter batteryLevelFilter = new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(batteryLevel, batteryLevelFilter);


            mHandler.postDelayed(m_Runnable, 500);

        }

    };

    public class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.w("BAM", "Screen went on");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.w("BAM", "Screen went off");
            }
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        ConstraintLayout bimg = (ConstraintLayout) findViewById(R.id.layout);
        String background = String.valueOf(bimg.getTag());
        if (background.equals("img")) {
            bimg.setBackgroundResource(R.drawable.wallp);
            bimg.setTag("wallp");
        } else {
            if (background.equals("wallp")) {
                bimg.setBackgroundResource(R.drawable.fond);
                bimg.setTag("fond");
            } else {
                if (background.equals("fond")) {
                    bimg.setBackgroundResource(R.drawable.img);
                    bimg.setTag("img");
                }
            }
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDown: " + event.toString());
        return true;
    }

    /*    B Y    J A Y A N T       D A B A S       */

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {
        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
            case 1:
                //top
                txtDate.setText("hi");
                return true;
            case 2:
                //left
                Intent i = new Intent(getApplicationContext(), UnlockActivity.class);
                startActivity(i);
                overridePendingTransition(R.animator.activity_open_scale,R.animator.activity_close_translate);
                return true;
            case 3:
                //down
                return true;
            case 4:
                //right
                overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        // overridePendingTransitionExit();
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }


    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle >= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
    }


    @Override
    public void onLongPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onStop() {

        super.onStop();
        // Don't hang around.
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("KEYCODE_BACK");
            return false;
        }
        return false;
    }

    public void locked(Activity activity) {
        if (mOverlayDialog == null) {
            mOverlayDialog = new OverlayDialog(activity);
            mOverlayDialog.show();
            mLockStatusChangedListener = (OnLockStatusChangedListener) activity;
        }
    }

    private static class OverlayDialog extends AlertDialog {

        public OverlayDialog(Activity activity) {
            super(activity, R.style.OverlayDialog);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            params.dimAmount = 0.0F;
            params.width = 0;
            params.height = 0;
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    0xffffff);
            setOwnerActivity(activity);
            setCancelable(false);
        }
    }

    public interface OnLockStatusChangedListener
    {
        public void onLockStatusChanged(boolean isLocked);
    }

    // Reset variables
    public void reset() {
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(LockedScreen.this);
    }

    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();
    }

    // Simply unlock device when home button is successfully unlocked

    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice()
    {
        finish();
    }

}
