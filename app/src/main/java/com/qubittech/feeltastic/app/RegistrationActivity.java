package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qubittech.feeltastic.services.TrackingService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import util.JsonHttpClient;

/**
 * Created by Manoj on 19/04/2014.
 */
public class RegistrationActivity extends Activity {

    private EditText userName, password, email, location;
    TrackingService myService;
    boolean isBound = false;

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            TrackingService.LocalBinder binder = (TrackingService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("LocationReceived"));

        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        location = (EditText) findViewById(R.id.location);

        Button register = (Button) findViewById(R.id.btnRegister);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tm.getNetworkOperatorName();
        if ("".equals(networkOperator)) {
            // Emulator
        } else {
            startService(new Intent(TrackingService.ACTION_START_MONITORING));
            // Device
        }


        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SaveUserTask().execute("");
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Double currentLatitude = intent.getDoubleExtra("latitude", 0);
            Double currentLongitude = intent.getDoubleExtra("longitude", 0);

            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0)
                location.setText(addresses.get(0).getLocality());


            // location.setText(String.format("%s%s", currentLatitude.toString(), currentLongitude.toString()));
        }
    };

    private class SaveUserTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", userName.getText().toString()));
            args.add(new BasicNameValuePair("email", password.getText().toString()));
            args.add(new BasicNameValuePair("password", email.getText().toString()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            jsonHttpClient.PostParams("http://10.0.3.2/FeelingService/api/User", args);
            return "";
        }
    }
}
