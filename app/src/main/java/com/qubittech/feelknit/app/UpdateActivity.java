package com.qubittech.feelknit.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.qubittech.feelknit.util.App;


public class UpdateActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        App.updateActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.updateActivity = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.close();
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        TextView skipButton = (TextView) findViewById(R.id.skipButton);
        TextView updateButton = (TextView) findViewById(R.id.updateButton);

        skipButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              startActivity(new Intent(UpdateActivity.this, LoginActivity.class));
                                          }
                                      }
        );

        updateButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Uri uri = Uri.parse("market://details?id=com.qubittech.feelknit.app");
                                                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

                                                try {

                                                    startActivity(myAppLinkToMarket);

                                                } catch (ActivityNotFoundException e) {

                                                    //the device hasn't installed Google Play
//                          Toast.makeText(Settings.this, "You don't have Google Play installed", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
