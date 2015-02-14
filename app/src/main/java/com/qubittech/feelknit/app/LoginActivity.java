package com.qubittech.feelknit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.qubittech.feelknit.models.LoginResult;
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
    protected void onStart() {
        super.onStart();
        App.loginActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.loginActivity = null;
    }

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

        etUsername = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtUserName);
        etPassword = (EditText) findViewById(com.qubittech.feelknit.app.R.id.txtPassword);
        if (ApplicationHelper.getUserName(getApplicationContext()) != null && ApplicationHelper.getUserName(getApplicationContext()) != "") {
            Mint.setUserIdentifier(ApplicationHelper.getUserName(getApplicationContext()));
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
    public void onBackPressed() {
        super.onBackPressed();
        App.close();
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
                etUsername.setText("");
                etPassword.setText("");
                ApplicationHelper.setUserName(getApplicationContext(), loginResult.getUserName());
                ApplicationHelper.setAvatar(getApplicationContext(), loginResult.getAvatar());

                ApplicationHelper.setUserEmail(getApplicationContext(), loginResult.getUserEmail());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                ShowAlert("Wrong username/password");
            }
        }

        @Override
        protected LoginResult doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("username", params[0]));
            args.add(new BasicNameValuePair("password", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            String verifyUrl = UrlHelper.USER_LOGIN;
            String response = jsonHttpClient.PostParams(verifyUrl, args);

            Gson gson = new GsonBuilder().create();
            LoginResult result = gson.fromJson(response, LoginResult.class);
            ApplicationHelper.setAuthorizationToken(getApplicationContext(), result.getToken());
            if (result.isLoginSuccessful()) {
                while (!regIdReceived) {
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                        }
                        regId = gcm.register(PROJECT_NUMBER);
                        regIdReceived = true;

                        args = new ArrayList<>();
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

    private void ShowAlert(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
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
