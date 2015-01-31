package com.qubittech.feelknit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.qubittech.feelknit.models.LoginResult;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;


public class LoginActivity extends Activity implements Validator.ValidationListener {

    private String userName;
    private String password;
    private Validator validator;

    ProgressDialog dialog;
    GoogleCloudMessaging gcm;
    String regId;
    String PROJECT_NUMBER = "846765263532";
    boolean regIdReceived = false;

    @Required(order = 1)
    private EditText etUsername;
    @Required(order = 2)
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        validator = new Validator(this);
        validator.setValidationListener(this);
        setContentView(com.qubittech.feelknit.app.R.layout.login);
        TextView forgotLabel = (TextView) findViewById(com.qubittech.feelknit.app.R.id.forgotLabel);
        forgotLabel.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgotLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("From", 2);
                startActivity(intent);
            }
        });

        etUsername = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtUserName);
        etPassword = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtPassword);
        if (ApplicationHelper.getUserName(getApplicationContext()) != null && ApplicationHelper.getUserName(getApplicationContext()) != "") {
            BugSenseHandler.setUserIdentifier(ApplicationHelper.getUserName(getApplicationContext()));
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {

            Button loginButton = (Button) findViewById(com.qubittech.feelknit.app.R.id.btnLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    validator.validate();
                }
            });

            Button registrationButton = (Button) findViewById(com.qubittech.feelknit.app.R.id.btnRegister);

            registrationButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                }
            });

        }
    }

    @Override
    public void onValidationSucceeded() {
        userName = etUsername.getText().toString();
        password = etPassword.getText().toString();
        new LoginUserTask().execute(userName, password);
        dialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait");
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (failedView instanceof TextView) {
            TextView view = (TextView) failedView;
            view.requestFocus();
            view.setError(failedRule.getFailureMessage());
        }
    }

    private class LoginUserTask extends AsyncTask<String, Integer, LoginResult> {
        @Override
        protected void onPostExecute(LoginResult loginResult) {
            super.onPostExecute(loginResult);
            dialog.dismiss();
            if (loginResult.isLoginSuccessful()) {
//                SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString("Username", userName);
//                editor.putString("Avatar", loginResult.getAvatar());
//                editor.putString("Token", loginResult.getToken());
//                editor.putString("Email", loginResult.getUserEmail());
//                editor.commit();
                ApplicationHelper.setUserName(getApplicationContext(), userName);
                ApplicationHelper.setAvatar(getApplicationContext(), loginResult.getAvatar());
                ApplicationHelper.setAuthorizationToken(getApplicationContext(), loginResult.getToken());
                ApplicationHelper.setUserEmail(getApplicationContext(), loginResult.getUserEmail());
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
        protected LoginResult doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", params[0]));
            args.add(new BasicNameValuePair("password", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            String verifyUrl = UrlHelper.USER_LOGIN;
            String response = jsonHttpClient.PostParams(verifyUrl, args);

            Gson gson = new GsonBuilder().create();
            LoginResult result = gson.fromJson(response, LoginResult.class);

            if (result.isLoginSuccessful()) {
                while (!regIdReceived) {
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                        }
                        regId = gcm.register(PROJECT_NUMBER);
                        String msg = "Device registered, registration ID=" + regId;
                        regIdReceived = true;

                        args = new ArrayList<NameValuePair>();
                        args.add(new BasicNameValuePair("username", params[0]));
                        args.add(new BasicNameValuePair("key", regId));

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
