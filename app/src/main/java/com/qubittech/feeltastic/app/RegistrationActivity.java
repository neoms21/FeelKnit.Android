package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.qubittech.feeltastic.services.TrackingService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qubittech.feeltastic.util.ApplicationHelper;
import com.qubittech.feeltastic.util.JsonHttpClient;
import com.qubittech.feeltastic.util.UrlHelper;

/**
 * Created by Manoj on 19/04/2014.
 */
public class RegistrationActivity extends Activity {

    private EditText userName, password, email, location;
    TrackingService myService;
    boolean isBound = false;
    ProgressDialog dialog;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "846765263532";
    boolean regIdRecevied = false;
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

        userName = (EditText) findViewById(R.id.txtUserName);
        password = (EditText) findViewById(R.id.txtPassword);
        email = (EditText) findViewById(R.id.txtEmailAddress);
        location = (EditText) findViewById(R.id.txtLocation);

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
                dialog = ProgressDialog.show(RegistrationActivity.this, "Registering User", "Please wait");
                new SaveUserTask().execute("try");
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

    private class SaveUserTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();

            if (result) {
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Username", userName.getText().toString());
                editor.putString("Password", password.getText().toString());
                editor.commit();
                ApplicationHelper.UserName = userName.getText().toString();
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                intent.putExtra("IsFromRegister", true);
                startActivity(intent);
            }
            else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
                builder1.setMessage("Username already exists!");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", userName.getText().toString()));
            args.add(new BasicNameValuePair("password", password.getText().toString()));
            args.add(new BasicNameValuePair("emailaddress", email.getText().toString()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String res = jsonHttpClient.PostParams(UrlHelper.USER, args);

            while (!regIdRecevied) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    String msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);
                    regIdRecevied = true;
                    args = new ArrayList<NameValuePair>();
                    jsonHttpClient = new JsonHttpClient();
                    args = new ArrayList<NameValuePair>();
                    args.add(new BasicNameValuePair("username", userName.getText().toString()));
                    args.add(new BasicNameValuePair("key", regid));

                    String keyUrl = UrlHelper.USER_KEY;
                    jsonHttpClient.PostParams(keyUrl, args);

                } catch (IOException ex) {
                    Log.i("Error:", ex.getMessage());
                }
            }

            return Boolean.parseBoolean(res);
        }
    }
}
