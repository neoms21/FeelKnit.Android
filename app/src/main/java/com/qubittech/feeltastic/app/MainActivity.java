package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qubittech.feeltastic.fragments.AddFeelingFragment;
import com.qubittech.feeltastic.fragments.CommentsFragment;
import com.qubittech.feeltastic.fragments.ForgotFragment;
import com.qubittech.feeltastic.fragments.RelatedFeelingFragment;
import com.qubittech.feeltastic.fragments.UserFeelingsFragment;
import com.qubittech.feeltastic.fragments.commentsFeelingsFragment;
import com.qubittech.feeltastic.models.Feeling;
import com.qubittech.feeltastic.navigation.AbstractNavDrawerActivity;
import com.qubittech.feeltastic.navigation.NavDrawerActivityConfiguration;
import com.qubittech.feeltastic.navigation.NavDrawerAdapter;
import com.qubittech.feeltastic.navigation.NavDrawerItem;
import com.qubittech.feeltastic.navigation.NavMenuBuilder;
import com.qubittech.feeltastic.util.ApplicationHelper;
import com.qubittech.feeltastic.util.JsonHttpClient;
import com.qubittech.feeltastic.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractNavDrawerActivity implements AddFeelingFragment.OnCreateFeelingClick {
    private ApplicationHelper applicationHelper;
    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        NavMenuBuilder navBuilder = new NavMenuBuilder();
        String[] drawerItems = getResources().getStringArray(R.array.navigation_drawer_options);
        int id = 101;
        for (String item : drawerItems) {
            String[] parts = item.split(",");
            navBuilder.addSectionItem(id, parts[0], parts[1], true, getApplicationContext());
            id++;
        }

        NavDrawerItem[] menu = navBuilder.build();

        NavDrawerAdapter adapter = new NavDrawerAdapter(this, R.layout.navdrawer_item, menu);

        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration.Builder()
                .mainLayout(R.layout.activity_main)
                .drawerLayoutId(R.id.drawer_layout)
                .leftDrawerId(R.id.left_drawer)
                .menu(menu)
                .adapter(adapter)
                .drawerIcon(R.drawable.ic_drawer)
                .build();

        return navDrawerActivityConfiguration;
    }

    @Override
    protected void onNavItemSelected(int id) {
        switch (id) {
            case 102:
                StartUserFeelingsFragment();
                break;
            case 103:
                commentsFeelingsFragment fragment = new commentsFeelingsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "Comments Feelings").addToBackStack("Comments" +
                        "Feelings").commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        UserFeelingsFragment myFragment = (UserFeelingsFragment) getSupportFragmentManager().findFragmentByTag("User Feelings");
        if (myFragment != null && myFragment.isVisible()) {
            startActivity(GetIntent(MainActivity.class));
            startActivity(GetIntent(LoginActivity.class));
            startActivity(GetIntent(LoadingActivity.class));
            return;
        }
        super.onBackPressed();
    }

    private Intent GetIntent(Class cls) {
        Intent intent = new Intent(MainActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationHelper = (ApplicationHelper) getApplicationContext();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        int switchNum = 0;
        Intent intent = getIntent();
        //boolean isRegister = false;
        Bundle bundle = intent.getExtras();// ("IsFromRegister", isRegister);
        if (bundle != null) {
            switchNum = bundle.getInt("From");
        }
        //  isRegister = bundle == null ? false : bundle.getBoolean("IsFromRegister");
        Feeling feeling = bundle == null ? null : (Feeling) bundle.get("feeling");

        TextView usrTextView = (TextView) findViewById(R.id.usrName);
        usrTextView.setText(applicationHelper.getUserName());
        Button btnSignout = (Button) findViewById(R.id.signout);

        btnSignout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = applicationHelper.getUserName();
                applicationHelper.setUserName("");
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                settings.edit().remove("Username").commit();
                settings.edit().remove("Password").commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                new ClearUserGcmKeyTask().execute(username);
            }
        });

        switch (switchNum) {
            case 0:
                StartUserFeelingsFragment();
                break;
            //Registration
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddFeelingFragment(), "Add Feeling").addToBackStack("Add Feeling").commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ForgotFragment(), "Forgot Password").commit();
                break;
            case 3:
                ShowCommentsFragment(feeling, null, null);
                break;
            default:
                break;
        }
    }

    private void StartUserFeelingsFragment() {
        UserFeelingsFragment userFeelingsFragment = new UserFeelingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", "From Activity");
        userFeelingsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, userFeelingsFragment, "User Feelings").addToBackStack("UserFeelings").commit();
    }

    @Override
    public void onFeelingCreated(Feeling feeling, List<Feeling> relatedFeelings) {
        RelatedFeelingFragment relatedFeelingsFragment = new RelatedFeelingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feeling", feeling);
        bundle.putSerializable("relatedFeelings", (Serializable) relatedFeelings);

        //set Fragmentclass Arguments
        relatedFeelingsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                relatedFeelingsFragment, "Related Feelings").addToBackStack("RelatedFeelings").commit();
    }

    public void AddCreateFeelingFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddFeelingFragment(), "Share Feeling").addToBackStack("AddFeeling").commit();
    }

    public void ShowCommentsFragment(Feeling feeling, String feelingText,
                                     String username) {

        if (feeling == null) {
            new getUserFeeingTask().execute(feelingText, username);
            return;
        }

        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feeling", feeling);
        bundle.putSerializable("user", applicationHelper.getUserName());
        commentsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, commentsFragment, "Comments").addToBackStack("Comments").commit();
    }

    private class ClearUserGcmKeyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("username", params[0]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String keyUrl = UrlHelper.CLEAR_USER_KEY;
            return jsonHttpClient.PostParams(keyUrl, args);
        }
    }


    private class getUserFeeingTask extends AsyncTask<String, Integer, Feeling> {
        @Override
        protected void onPostExecute(Feeling feeling) {
            ShowCommentsFragment(feeling, null, null);
        }

        @Override
        protected Feeling doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("feeling", params[0]));
            args.add(new BasicNameValuePair("username", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient();
            String url = UrlHelper.USER_FEELINGS;
            String response = jsonHttpClient.Get(url, args);
            Gson gson = new Gson();
            Feeling feeling;
            feeling = gson.fromJson(response, Feeling.class);
            return feeling;
        }
    }


}
