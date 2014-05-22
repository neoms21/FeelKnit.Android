package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//
//
//        Button loginButton = (Button) findViewById(R.id.btnLogin);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                List<NameValuePair> args = new ArrayList<NameValuePair>();
//                args.add(new BasicNameValuePair("username", "Manoj"));
//                args.add(new BasicNameValuePair("email", "WElcome1"));
//                args.add(new BasicNameValuePair("password", "m.sethi@ms.com"));
//                JsonHttpClient jsonHttpClient = new JsonHttpClient();
//                jsonHttpClient.PostParams("http://10.0.3.2/FeelingService/api/User", args);
//                // new SaveUserTask().execute("try");
//            }
//        });
    }


}
