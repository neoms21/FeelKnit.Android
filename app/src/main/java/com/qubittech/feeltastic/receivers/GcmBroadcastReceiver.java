package com.qubittech.feeltastic.receivers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.qubittech.feeltastic.app.MainActivity;
import com.qubittech.feeltastic.services.GcmMessageHandler;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	
	
    @Override
    public void onReceive(Context context, Intent intent) {
       
    	// Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
        		GcmMessageHandler.class.getName());

//        Intent myIntent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                myIntent,PendingIntent.FLAG_ONE_SHOT);

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
