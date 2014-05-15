package com.qubittech.feeltastic.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * Created by Manoj on 07/05/2014.
 */
public class TrackingService extends Service implements LocationListener {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    final static String LOGTAG = "Location Monitoring";

    //Start tracking service
    public final static String ACTION_START_MONITORING = "com.qubittech.START_MONITORING";
    //Stop tracking service
    public final static String ACTION_STOP_MONITORING = "com.qubittech.STOP_MONITORING";
    private final static String HANDLER_THREAD_NAME = "MyLocationThread";

    LocationListener _listener;
    Looper _looper;
    android.os.Handler _handler;

    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread(HANDLER_THREAD_NAME);
        thread.start();

//        _looper = thread.getLooper();
        _handler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doStopTracking();

        if (_looper != null)
            _looper.quit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        String threadId = LogHelper.threadId();
//        Log.d(LOGTAG, "Location Monitoring Service onStartCommand - " + threadId);
        doStartTracking();
        // _handler.sendMessage(_handler.obtainMessage(0, intent));
        return START_STICKY;
    }

    public boolean handleMessage(Message message) {
//               String threadId = LogHelper.threadId();
//        Log.d(LOGTAG, "Location Monitoring Service onStartCommand - " + threadId);
        Log.d(LOGTAG, "in handleMessage");
        Intent intent = (Intent) message.obj;

        String action = intent.getAction();
        Log.d(LOGTAG, "Location Service onStartCommand Action:" + action);

        if (action.equals(ACTION_START_MONITORING)) {
            doStartTracking();
        } else if (action.equals(ACTION_STOP_MONITORING)) {
            doStopTracking();
            stopSelf();
        }

        return true;
    }

    private void doStartTracking() {
        //doStopTracking();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //_listener = new MyLocationListener();

        lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
    }

    private void doStopTracking() {
        if (_listener != null) {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            lm.removeUpdates(_listener);
            _listener = null;
        }

    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onLocationChanged(Location location) {
        String provider = location.getProvider();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        float accuracy = location.getAccuracy();
        long time = location.getTime();

        Intent intent = new Intent("LocationReceived");
        intent.putExtra("latitude", lat);
        intent.putExtra("longitude", lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        // String logMessage = FormatLocationInfo(provider, lat, lng, accuracy, time);

        //Log.d(_logTag, "Monitor Location:" + logMessage);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class LocalBinder extends Binder {
        public TrackingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackingService.this;
        }
    }
}

