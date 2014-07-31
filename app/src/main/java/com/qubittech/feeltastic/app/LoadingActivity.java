package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.crittercism.app.Crittercism;

public class LoadingActivity extends Activity {
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        Crittercism.initialize(getApplicationContext(), "53dab3b10729df413b000004");
        dialog = ProgressDialog.show(LoadingActivity.this, "Loading", "Please wait...", true);
        dialog.setContentView(R.layout.progress);

        dialog.setCancelable(true);
        new LoadingTask().execute("");
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
