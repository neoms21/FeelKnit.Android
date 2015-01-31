package com.qubittech.feelknit.app;

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
import android.media.AsyncPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Regex;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;
import com.qubittech.feelknit.models.LoginResult;
import com.qubittech.feelknit.services.TrackingService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

public class RegistrationActivity extends Activity implements Validator.ValidationListener {

    private Validator validator;

    @Required(order = 1)
    @TextRule(order = 1, minLength = 3, message = "Enter at least 3 characters.")
    @Regex(order = 1, pattern = "[a-zA-Z0-9 ]*", message = "Should contain only alphabets or numbers")
    private EditText userName;

    @Required(order = 2)
    @TextRule(order = 2, minLength = 6, message = "Enter at least 6 characters.")
    private EditText password;

    @Required(order = 3)
    private EditText email;

    private EditText location;
    private Button register;

    TrackingService myService;
    boolean isBound = false;
    ProgressDialog dialog;
    GoogleCloudMessaging gcm;
    String regId;
    String PROJECT_NUMBER = "846765263532";
    boolean regIdReceived = false;
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
        validator = new Validator(this);
        validator.setValidationListener(this);
        setContentView(com.qubittech.feelknit.app.R.layout.registration);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("LocationReceived"));

        userName = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtUserName);
        password = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtPassword);
        email = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtEmailAddress);
        location = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtLocation);

        register = (Button) findViewById(R.id.btnRegister);

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
                validator.validate();
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

    @Override
    public void onValidationSucceeded() {
        dialog = ProgressDialog.show(RegistrationActivity.this, "Registering User", "Please wait");
        new SaveUserTask().execute("try");
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (failedView instanceof TextView) {
            TextView view = (TextView) failedView;
            view.requestFocus();
            view.setError(failedRule.getFailureMessage());
        }
    }

    private class SaveUserTask extends AsyncTask<String, Integer, LoginResult> {

        @Override
        protected void onPostExecute(LoginResult result) {
            dialog.dismiss();

            if (result.isLoginSuccessful()) {
                ApplicationHelper.setUserName(getApplicationContext(), userName.getText().toString());

                ApplicationHelper.setUserEmail(getApplicationContext(), email.getText().toString());
                BugSenseHandler.setUserIdentifier(ApplicationHelper.getUserName(getApplicationContext()));
                Intent intent = new Intent(RegistrationActivity.this, SaveAvatarActivity.class);
                intent.putExtra("From", 1);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(RegistrationActivity.this);
                builder1.setMessage(result.getError());
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
        protected LoginResult doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", userName.getText().toString()));
            args.add(new BasicNameValuePair("password", password.getText().toString()));
            args.add(new BasicNameValuePair("emailaddress", email.getText().toString()));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            String res = jsonHttpClient.PostParams(UrlHelper.USER, args);

            Gson gson = new GsonBuilder().create();
            LoginResult result = gson.fromJson(res, LoginResult.class);

            if (result.isLoginSuccessful()) {
                ApplicationHelper.setAuthorizationToken(getApplicationContext(), result.getToken());
            } else
                return result;

            while (!regIdReceived) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register(PROJECT_NUMBER);
                    String msg = "Device registered, registration ID=" + regId;
                    Log.i("GCM", msg);
                    regIdReceived = true;
                    args = new ArrayList<NameValuePair>();
                    jsonHttpClient = new JsonHttpClient(getApplicationContext());
                    args = new ArrayList<NameValuePair>();
                    args.add(new BasicNameValuePair("username", userName.getText().toString()));
                    args.add(new BasicNameValuePair("key", regId));

                    String keyUrl = UrlHelper.USER_KEY;
                    jsonHttpClient.PostParams(keyUrl, args);

                } catch (IOException ex) {
                    Log.i("Error:", ex.getMessage());
                }
            }

            return result;
        }
    }
}
