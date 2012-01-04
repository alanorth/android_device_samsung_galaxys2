package com.teamhacksung.tvout;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Tvout;
import android.nfc.Tag;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class TvOutService extends Service {

    public static final String TAG = "TvOutService_java";

    private Tvout mTvOut;
    private boolean mWasOn = false; // For enabling on screen on

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HDMI_AUDIO_PLUG.equals(action)) {
                int state = intent.getIntExtra("state", 0);
                if (state == 1 && !mTvOut.getStatus()) {
                    // Enable when cable is plugged
                    Log.i(TAG, "HDMI plugged");
                    getTvoutInstance();
                    mWasOn = false;
                    enable();
                } else if (mTvOut.getStatus()) {
                    // Disable when cable is unplugged
                    Log.i(TAG, "HDMI unplugged");
                    releaseTvout();
                    mWasOn = false;
                    disable();
                    stopSelf();
                }
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (mWasOn) {
                    Log.i(TAG, "Screen On - Resume TvOut stream");
                    mWasOn = false;
                    enable();
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                if (mTvOut.getStatus()) {
                    Log.i(TAG, "Screen Off - Pausing TvOut stream");
                    mWasOn = true;
                    disable();
                }
            }
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_HDMI_AUDIO_PLUG);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);
        Log.i(TAG, "Registered Receiver");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private boolean getTvoutInstance() {
        releaseTvout();

        try {
            mTvOut = new Tvout();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void releaseTvout() {
        if (mTvOut != null) {
            mTvOut.release();
            mTvOut = null;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void enable() {
        mTvOut.setStatus(true);
        mTvOut.setCableStatus(true);
    }

    private void disable() {
        mTvOut.setStatus(false);
        mTvOut.setCableStatus(false);
    }

}
