package com.qubittech.feelknit.services;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qubittech.feelknit.app.MainActivity;
import com.qubittech.feelknit.app.R;
import com.qubittech.feelknit.receivers.GcmBroadcastReceiver;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.splunk.mint.Mint;

public class GcmMessageHandler extends IntentService {

    String mes;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.

        String user = extras.getString("user");
        String feelingJson = extras.getString("feeling");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        String feelingId = gson.fromJson(feelingJson, String.class);
        mes = user != null ? String.format("%s %s", extras.getString("message"), user) : extras.getString("message");
        //NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.iconsmall) // notification icon
                .setContentTitle("FeelKnit!") // title for notification
                .setLights(Color.WHITE, 1000, 3000)
                .setContentText(mes) // message for notification
                .setAutoCancel(true); // clear notification after click

       // mBuilder.setStyle(inboxStyle);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings.getString("Username", null) != null && ApplicationHelper.getUserName(getApplicationContext()) == null) {
            Mint.initAndStartSession(getApplicationContext(), "e9e97454");
            ApplicationHelper.setUserName(getApplicationContext(), settings.getString("Username", null));
            Mint.setUserIdentifier(ApplicationHelper.getUserName(getApplicationContext()));
        }

        Intent intnt = new Intent(this, MainActivity.class);
        intnt.putExtra("From", feelingId == null ? 5 : 3);
        intnt.putExtra("feelingId", feelingId);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intnt, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
