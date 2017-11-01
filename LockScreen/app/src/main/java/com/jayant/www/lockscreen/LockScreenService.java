package com.jayant.www.lockscreen;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.jayant.www.lockscreen.LockScreenReceiver;

import static android.content.Intent.ACTION_SYNC;

public class LockScreenService extends Service {

    private BroadcastReceiver receiver;
    private BroadcastReceiver rec;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    @SuppressWarnings("deprecation")
    public void onCreate() {

        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();



        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);
    //    startForeground();
        return START_STICKY;

    }

    // Run service in foreground so it is less likely to be killed by system
  /*  private void startForeground() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("Running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        startForeground(9999,notification);
    }
*/

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }



}
