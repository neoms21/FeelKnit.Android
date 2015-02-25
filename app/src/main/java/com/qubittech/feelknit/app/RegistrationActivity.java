package com.qubittech.feelknit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationServices;
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
import com.qubittech.feelknit.util.App;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;
import com.splunk.mint.Mint;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import android.support.v4.content.LocalBroadcastManager;

public class RegistrationActivity extends Activity implements Validator.ValidationListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private Validator validator;

    @Required(order = 1)
    @TextRule(order = 1, minLength = 3, message = "Enter at least 3 characters.")
    @Regex(order = 1, pattern = "[a-zA-Z0-9 ]*", message = "Should contain only alphabets or numbers")
    private EditText userName;

    @Required(order = 2)
    @TextRule(order = 2, minLength = 6, message = "Enter at least 6 characters.")
    private EditText password;

    @Required(order = 3)
    @Email(order = 3)
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

    protected GoogleApiClient mGoogleApiClient;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private String phoneNumber;

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
    protected void onStart() {
        super.onStart();
        App.registrationActivity = this;
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.registrationActivity = null;
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
        setContentView(com.qubittech.feelknit.app.R.layout.registration);
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("LocationReceived"));

        userName = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtUserName);
        password = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtPassword);
        email = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtEmailAddress);
        location = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtLocation);

        register = (Button) findViewById(R.id.btnRegister);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tm.getLine1Number();
        if (phoneNumber == null || phoneNumber.equals(""))
            phoneNumber = String.format("%s,%s,%s", tm.getSimSerialNumber(), tm.getNetworkOperatorName(), tm.getNetworkCountryIso());
        String networkOperator = tm.getNetworkOperatorName();
//        if ("".equals(networkOperator)) {
//            // Emulator
//        } else {
//           // startService(new Intent(TrackingService.ACTION_START_MONITORING));
        buildGoogleApiClient();
        //}

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Double currentLatitude = intent.getDoubleExtra("latitude", 0);
//            Double currentLongitude = intent.getDoubleExtra("longitude", 0);
//
//            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
//            List<Address> addresses = null;
//
//            try {
//                addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (addresses.size() > 0)
//                location.setText(addresses.get(0).getLocality());
//
//            // location.setText(String.format("%s%s", currentLatitude.toString(), currentLongitude.toString()));
//        }
//    };

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

    protected Location mLastLocation;

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0)
                location.setText(addresses.get(0).getLocality());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("Feelknit", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("Feelknit", "Connection suspended");
        mGoogleApiClient.connect();
    }

    private class SaveUserTask extends AsyncTask<String, Integer, LoginResult> {

        @Override
        protected void onPostExecute(LoginResult result) {
            dialog.dismiss();

            if (result.isLoginSuccessful()) {
                ApplicationHelper.setUserName(getApplicationContext(), result.getUserName());
                ApplicationHelper.setUserEmail(getApplicationContext(), email.getText().toString());
                Mint.setUserIdentifier(ApplicationHelper.getUserName(getApplicationContext()));
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

            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("username", userName.getText().toString()));
            args.add(new BasicNameValuePair("password", password.getText().toString()));
            args.add(new BasicNameValuePair("emailaddress", email.getText().toString()));
            args.add(new BasicNameValuePair("latitude", latitude != null ? latitude.toString() : "0"));
            args.add(new BasicNameValuePair("longitude", longitude != null ? longitude.toString() : "0"));
            args.add(new BasicNameValuePair("phoneNumber", String.valueOf(phoneNumber)));
            args.add(new BasicNameValuePair("deviceName", ApplicationHelper.getDeviceName()));
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
                    jsonHttpClient = new JsonHttpClient(getApplicationContext());
                    args = new ArrayList<>();
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
