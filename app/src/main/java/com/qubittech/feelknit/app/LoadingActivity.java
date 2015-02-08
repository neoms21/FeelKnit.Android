package com.qubittech.feelknit.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.qubittech.feelknit.util.App;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;
import com.splunk.mint.Mint;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends Activity {
    ProgressDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        App.loadingActivity =this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.loadingActivity = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoadingActivity.this);
            builder1.setMessage("There is no internet connectivity. Please try again in some time.");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    }
            );
            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }

        setContentView(com.qubittech.feelknit.app.R.layout.loading);
        Mint.initAndStartSession(getApplication(), "e9e97454");
        dialog = ProgressDialog.show(LoadingActivity.this, "Loading", "Please wait...", true, true);

        dialog.setCancelable(true);
        new LoadingTask().execute("");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private class LoadingTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ApplicationHelper.setFeelTexts(getApplicationContext(), s);
            startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            return jsonHttpClient.Get(UrlHelper.GET_FEELS, args);
        }
    }

}
