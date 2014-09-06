package com.qubittech.feeltastic.services;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qubittech.feeltastic.app.MainActivity;
import com.qubittech.feeltastic.app.R;

import com.qubittech.feeltastic.models.Feeling;
import com.qubittech.feeltastic.receivers.GcmBroadcastReceiver;
import com.qubittech.feeltastic.util.ApplicationHelper;

import java.util.List;

public class GcmMessageHandler extends IntentService {

    String mes;
    private Handler handler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        String user = extras.getString("user");
        String feelingJson = extras.getString("feeling");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        Feeling feeling = gson.fromJson(feelingJson, Feeling.class);
        mes = String.format("%s %s", extras.getString("message"), user);
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon) // notification icon
                .setContentTitle("FeelKnit!") // title for notification
                .setContentText(mes) // message for notification
                .setAutoCancel(true); // clear notification after click

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings.getString("Username", null) != null && ApplicationHelper.UserName == null) {
            Crittercism.initialize(getApplicationContext(), "53dab3b10729df413b000004");
            ApplicationHelper.UserName = settings.getString("Username", null);
            Crittercism.setUsername(ApplicationHelper.UserName);
        }

        Intent intnt = new Intent(this, MainActivity.class);
        intnt.putExtra("feeling",feeling);

        PendingIntent pi = PendingIntent.getActivity(this, 0, intnt, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
