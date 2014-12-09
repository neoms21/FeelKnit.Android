package com.qubittech.feelknit.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.qubittech.feelknit.adapters.AvatarListAdapter;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.OnClickListener;


public class SaveAvatarActivity extends Activity {

    private String selectedAvatar;
    private ApplicationHelper applicationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar);
        applicationHelper = (ApplicationHelper) getApplicationContext();
        ArrayAdapter avatarListAdapter = new AvatarListAdapter(this, R.layout.avatar_listview_item, getResources().getStringArray(R.array.avatars));

        final ListView listview = (ListView) findViewById(R.id.avatarList);

        listview.setAdapter(avatarListAdapter);
        listview.setDivider(new ColorDrawable());
        listview.setDividerHeight(10);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedAvatar = listview.getItemAtPosition(position).toString();
            }
        });
        avatarListAdapter.notifyDataSetChanged();

        Button skipButton = (Button) findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startMainActivity();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveAvatarButton);
        saveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new SaveUserAvatarTask().execute();
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(SaveAvatarActivity.this, MainActivity.class);
        intent.putExtra("From", 1);
        startActivity(intent);
    }

    private class SaveUserAvatarTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            applicationHelper.setAvatar(selectedAvatar);
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", applicationHelper.getUserName()));
            args.add(new BasicNameValuePair("avatar", selectedAvatar));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(applicationHelper);
            String res = jsonHttpClient.PostParams(UrlHelper.SAVE_AVATAR, args);
            return Boolean.parseBoolean(res);
        }

    }
}

