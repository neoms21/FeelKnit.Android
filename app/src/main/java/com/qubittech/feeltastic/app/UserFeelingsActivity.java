package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.qubittech.feeltastic.adapters.FeelingsAdapter;
import com.qubittech.feeltastic.helpers.NavigationDrawerHelper;
import com.qubittech.feeltastic.models.Feeling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import util.JsonHttpClient;
import util.UrlHelper;

/**
 * Created by Manoj on 31/05/2014.
 */
public class UserFeelingsActivity extends Activity implements ListView.OnItemClickListener{

    private List<Feeling> _feelings = null;
    ProgressDialog dialog;

    NavigationDrawerHelper mNavigationDrawerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_feelings);

        String username = "";
        dialog = ProgressDialog.show(UserFeelingsActivity.this, "Loading", "Please wait...", true);
        Button newFeeling = (Button) findViewById(R.id.newFeelingButton);

//        mNavigationDrawerHelper = new NavigationDrawerHelper();
//        mNavigationDrawerHelper.init(this, this);
        newFeeling.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(UserFeelingsActivity.this, AddFeelingActivity.class));
            }
        });
        username = getUserName(username);
        new FetchUserFeelingsTask().execute(username);
    }

    private String getUserName(String username) {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if (settings != null) {
            username = settings.getString("Username", "").toString();
        }
        return username;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int optionLib, long l) {
//        mCoursePagerAdapter.setCourseLib(optionLib);
        mNavigationDrawerHelper.handleSelect(optionLib);
    }


    private class FetchUserFeelingsTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            System.out.println("User Feelings:" + s);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            Type collectionType = new TypeToken<List<Feeling>>() {
            }.getType();
            _feelings = (List<Feeling>) gson.fromJson(s, collectionType);


            ArrayAdapter arrayAdapter = new FeelingsAdapter(UserFeelingsActivity.this, R.layout.listview, _feelings);

            ListView listview = (ListView) findViewById(R.id.userFeelingsList);

            listview.setAdapter(arrayAdapter);
            listview.setDivider(new ColorDrawable());
            listview.setDividerHeight(15);
            arrayAdapter.notifyDataSetChanged();
            dialog.dismiss();

            mNavigationDrawerHelper = new NavigationDrawerHelper();

        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            return jsonHttpClient.Get(UrlHelper.USERNAME + params[0], args);
        }
    }

}
