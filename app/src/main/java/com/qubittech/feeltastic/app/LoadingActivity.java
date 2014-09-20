package com.qubittech.feeltastic.app;

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

import com.bugsense.trace.BugSenseHandler;
import com.crittercism.app.Crittercism;

public class LoadingActivity extends Activity {
    ProgressDialog dialog;

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

        setContentView(R.layout.loading);
        BugSenseHandler.initAndStartSession(getApplication(), "e9e97454");
        dialog = ProgressDialog.show(LoadingActivity.this, "Loading", "Please wait...", true);
        dialog.setContentView(R.layout.progress);

        dialog.setCancelable(true);
        new LoadingTask().execute("");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private class LoadingTask extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
            dialog.dismiss();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                Thread.sleep(200);

            } catch (InterruptedException e) {


            }

            return null;
        }
    }

}
