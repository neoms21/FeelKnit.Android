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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.qubittech.feelknit.adapters.AvatarListAdapter;
import com.qubittech.feelknit.util.App;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.OnClickListener;


public class SaveAvatarActivity extends Activity {

    private String selectedAvatar;
    private String[] avatars;
    private boolean fromProfile;

    private ImageView selectedAvatarImageView;

    @Override
    protected void onStart() {
        super.onStart();
        App.saveAvatarActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.saveAvatarActivity = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avatar);
        Button skipButton = (Button) findViewById(R.id.skipButton);
        Button saveButton = (Button) findViewById(R.id.saveAvatarButton);

        avatars = getResources().getStringArray(R.array.avatars);
        AvatarListAdapter avatarListAdapter = new AvatarListAdapter(this, R.layout.avatar_listview_item, avatars);

        final GridView gridView = (GridView) findViewById(R.id.avatarGrid);
        selectedAvatarImageView = (ImageView) findViewById(R.id.selectedAvatarImage);

        gridView.setAdapter(avatarListAdapter);

        fromProfile = getIntent().getBooleanExtra("Profile", false);

        if (fromProfile) {
            String avatar = ApplicationHelper.getAvatar(getApplicationContext());
            if (avatar != null && avatar != "") {
                int avatarIndex = Arrays.asList(avatars).indexOf(avatar);
                selectedAvatar = gridView.getItemAtPosition(avatarIndex).toString();
                ImageHelper.setBitMap(selectedAvatarImageView, getApplicationContext(), selectedAvatar, 100, 100);
//                gridView.setSelection(avatarIndex);
//                gridView.requestFocusFromTouch();
//                gridView.setSelection(avatarIndex);
//                gridView.setItemChecked(avatarIndex, true);
                skipButton.setText("Cancel");
                saveButton.setText("Select");
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedAvatar = gridView.getItemAtPosition(position).toString();
                ImageHelper.setBitMap(selectedAvatarImageView, getApplicationContext(), selectedAvatar, 100, 100);
            }
        });
        avatarListAdapter.notifyDataSetChanged();
        skipButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (fromProfile)
                    finish();
                else
                    startMainActivity(1, "");
            }
        });
        saveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (fromProfile) {
                    startMainActivity(4, selectedAvatar);
                } else {
                    new SaveUserAvatarTask().execute();
                    startMainActivity(1, selectedAvatar);
                }
            }
        });
    }

    private void startMainActivity(int value, String selectedAvatar) {
        Intent intent = new Intent(SaveAvatarActivity.this, MainActivity.class);
        intent.putExtra("From", value);
        if (selectedAvatar != "") {
            intent.putExtra("Avatar", selectedAvatar);
        }
        startActivity(intent);
    }

    private class SaveUserAvatarTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            ApplicationHelper.setAvatar(getApplicationContext(), selectedAvatar);
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", ApplicationHelper.getUserName(getApplicationContext())));
            args.add(new BasicNameValuePair("avatar", selectedAvatar));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            String res = jsonHttpClient.PostParams(UrlHelper.SAVE_AVATAR, args);
            return Boolean.parseBoolean(res);
        }
    }
}

