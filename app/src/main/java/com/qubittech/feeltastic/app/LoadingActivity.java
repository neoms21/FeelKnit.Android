package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

//        try {
//            Thread.sleep(2000);
//            startActivity(new Intent(LoadingActivity.this, AddFeelingActivity.class));
//        } catch (InterruptedException e) {
//
//
//        }
    }


}
