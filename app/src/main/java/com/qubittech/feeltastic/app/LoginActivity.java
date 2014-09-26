package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bugsense.trace.BugSense;
import com.bugsense.trace.BugSenseHandler;
import com.crittercism.app.Crittercism;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.qubittech.feeltastic.util.ApplicationHelper;
import com.qubittech.feeltastic.util.JsonHttpClient;
import com.qubittech.feeltastic.util.UrlHelper;


public class LoginActivity extends Activity {

    private String userName;
    private String password;

    ProgressDialog dialog;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "846765263532";
    boolean regIdRecevied = false;
    private EditText etUsername;
    private EditText etPassword;
    private ApplicationHelper applicationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        applicationHelper = (ApplicationHelper) getApplicationContext();
        setContentView(R.layout.login);
        TextView forgotLabel = (TextView) findViewById(R.id.forgotLabel);
        forgotLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgotLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("From",2);
                startActivity(intent);
            }
        });

        etUsername = (EditText) findViewById(R.id.txtUserName);
        etPassword = (EditText) findViewById(R.id.txtPassword);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings.getString("Username", null) != null) {
            applicationHelper.setUserName(settings.getString("Username", null));
            BugSenseHandler.setUserIdentifier(applicationHelper.getUserName());
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {

            Button loginButton = (Button) findViewById(R.id.btnLogin);

            //TextWatcher
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    checkFieldsForEmptyValues();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            };
            etUsername.addTextChangedListener(textWatcher);
            etPassword.addTextChangedListener(textWatcher);

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

    private void checkFieldsForEmptyValues() {
        Button b = (Button) findViewById(R.id.btnLogin);

        String s1 = etUsername.getText().toString().trim();
        String s2 = etPassword.getText().toString().trim();

        if (s1.length() == 0 || s2.length() == 0) {
            b.setClickable(false);
            b.setBackgroundColor(getResources().getColor(R.color.greyColor));
        } else {
            b.setClickable(true);
            b.setBackgroundColor(getResources().getColor(R.color.loginLabel));
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
                applicationHelper.setUserName(userName);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
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
