package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.ApplicationHelper;
import util.JsonHttpClient;
import util.UrlHelper;


public class LoginActivity extends Activity {

    private String userName;
    private String password;

    ProgressDialog dialog;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "846765263532";
    boolean regIdRecevied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        TextView forgotLabel = (TextView) findViewById(R.id.forgotLabel);
        forgotLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        final EditText etUsername = (EditText) findViewById(R.id.txtUserName);
        final EditText etPassword = (EditText) findViewById(R.id.txtPassword);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings.getString("Username", null) != null) {
            ApplicationHelper.UserName = settings.getString("Username", null);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {


            Button loginButton = (Button) findViewById(R.id.btnLogin);

            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    userName = etUsername.getText().toString();
                    password = etPassword.getText().toString();
                    new LoginUserTask().execute(userName, password);
                    dialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait");
                }
            });

            Button registrationButton = (Button) findViewById(R.id.btnRegister);

            registrationButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                }
            });

        }
    }


    private class LoginUserTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPostExecute(Boolean loginSuccessful) {
            super.onPostExecute(loginSuccessful);
            dialog.dismiss();
            if (loginSuccessful) {
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Username", userName);
                editor.putString("Password", password);
                editor.commit();
                ApplicationHelper.UserName = userName;
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            else
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                builder1.setMessage("Wrong username/password");
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
            args.add(new BasicNameValuePair("username", params[0]));
            args.add(new BasicNameValuePair("password", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String verifyUrl = UrlHelper.USER_VERIFY;
            String response = jsonHttpClient.PostParams(verifyUrl, args);
            boolean result = Boolean.parseBoolean(response);

            if (result) {
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
                        args.add(new BasicNameValuePair("username", params[0]));
                        args.add(new BasicNameValuePair("key", regid));

                        String keyUrl = UrlHelper.USER_KEY;
                        jsonHttpClient.PostParams(keyUrl, args);

                    } catch (IOException ex) {
                        Log.i("Error:", ex.getMessage());
                    }
                }
            }

            return result;
        }
    }
}
