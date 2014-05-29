package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;


public class LoginActivity extends Activity {

    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final EditText etUsername = (EditText) findViewById(R.id.txtUserName);
        final EditText etPassword = (EditText) findViewById(R.id.txtPassword);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings != null) {
            etUsername.setText(settings.getString("Username", "").toString());
            etPassword.setText(settings.getString("Password", "").toString());
        }


        Button loginButton = (Button) findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userName = etUsername.getText().toString();
                password = etPassword.getText().toString();
                new LoginUserTask().execute(userName, password);
            }
        });
    }


    private class LoginUserTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            if (s) {
                System.out.println("User RESULT:" + s);
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Username", userName);
                editor.putString("Password", password);
                editor.commit();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", params[0]));
            args.add(new BasicNameValuePair("password", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String response = jsonHttpClient.PostParams("http://10.0.3.2/FeelKnitService/Users/Verify", args);
            return Boolean.parseBoolean(response);
        }
    }
}
