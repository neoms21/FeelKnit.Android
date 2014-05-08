//package util;
//
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//import java.text.SimpleDateFormat;
//import java.util.TimeZone;
//
///**
// * Created by Manoj on 05/05/2014.
// */
//public class MyLocationListener implements LocationListener {
//    final String _logTag = "Monitor Location";
//    static final String _timeStampFormat = "yyyy-MM-dd'T'HH:mm:ss";
//    static final String _timeStampTimeZoneId = "UTC";
//
//    public void onLocationChanged(Location location) {
//        String provider = location.getProvider();
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        float accuracy = location.getAccuracy();
//        long time = location.getTime();
//
//        Intent intent = new Intent("LocationReceived");
//        intent.putExtra("latitude", 21.05);
//        intent.putExtra("longitude", 32.04);
//        LocalBroadcastManager.getInstance().sendBroadcast(intent);
//
//        String logMessage = FormatLocationInfo(provider, lat, lng, accuracy, time);
//
//        Log.d(_logTag, "Monitor Location:" + logMessage);
//    }
//
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    public void onProviderEnabled(String s) {
//        Log.d(_logTag, "Monitor Location - Provider Enabled:" + s);
//    }
//
//    public void onProviderDisabled(String s) {
//        Log.d(_logTag, "Monitor Location - Provider DISabled:" + s);
//    }
//
//    public static String FormatLocationInfo(String provider, double lat, double lng, float accuracy, long time) {
//        SimpleDateFormat timeStampFormatter = new SimpleDateFormat(_timeStampFormat);
//        timeStampFormatter.setTimeZone(TimeZone.getTimeZone(_timeStampTimeZoneId));
//
//        String timeStamp = timeStampFormatter.format(time);
//
//        String logMessage = String.format("%s | lat/lng=%f/%f | accuracy=%f | Time=%s",
//                provider, lat, lng, accuracy, timeStamp);
//
//        return logMessage;
//    }
//}
