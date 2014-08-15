package com.qubittech.feeltastic.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qubittech.feeltastic.fragments.AddFeelingFragment;
import com.qubittech.feeltastic.fragments.CommentsFragment;
import com.qubittech.feeltastic.fragments.RelatedFeelingFragment;
import com.qubittech.feeltastic.fragments.UserFeelingsFragment;
import com.qubittech.feeltastic.fragments.commentsFeelingsFragment;
import com.qubittech.feeltastic.models.Feeling;
import com.qubittech.feeltastic.navigation.AbstractNavDrawerActivity;
import com.qubittech.feeltastic.navigation.NavDrawerActivityConfiguration;
import com.qubittech.feeltastic.navigation.NavDrawerAdapter;
import com.qubittech.feeltastic.navigation.NavDrawerItem;
import com.qubittech.feeltastic.navigation.NavMenuBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.qubittech.feeltastic.util.ApplicationHelper;
import com.qubittech.feeltastic.util.JsonHttpClient;
import com.qubittech.feeltastic.util.UrlHelper;

public class MainActivity extends AbstractNavDrawerActivity implements AddFeelingFragment.OnCreateFeelingClick {

    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        NavMenuBuilder navBuilder = new NavMenuBuilder();
        String[] drawerItems = getResources().getStringArray(R.array.navigation_drawer_options);
        int id = 101;
        for (String item : drawerItems) {
            navBuilder.addSectionItem(id, item, item.toLowerCase(), true, getApplicationContext());
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
            case 103:
                commentsFeelingsFragment fragment = new commentsFeelingsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment , "Comments Feelings").addToBackStack("Comments" +
                        "Feelings").commit();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean isRegister = false;
        Bundle bld = intent.getExtras();// ("IsFromRegister", isRegister);

        isRegister = bld == null ? false : bld.getBoolean("IsFromRegister");
        TextView usrTextView = (TextView) findViewById(R.id.usrName);
        usrTextView.setText(ApplicationHelper.UserName);
        Button btnSignout = (Button) findViewById(R.id.signout);

        btnSignout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ApplicationHelper.UserName = "";
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                settings.edit().remove("Username").commit();
                settings.edit().remove("Password").commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                new ClearUserGcmKeyTask().execute("");
            }
        });


        //set Fragmentclass Arguments
        if (isRegister) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddFeelingFragment(), "Add Feeling").addToBackStack("Add Feeling").commit();
        } else {
            UserFeelingsFragment userFeelingsFragment = new UserFeelingsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", "From Activity");
            userFeelingsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, userFeelingsFragment, "User Feelings").addToBackStack("UserFeelings").commit();
        }
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

    public void ShowCommentsFragment(Feeling feeling) {
        CommentsFragment commentsFragment = new CommentsFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("feeling", feeling);
        bundle.putSerializable("user", ApplicationHelper.UserName);
        commentsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, commentsFragment, "Comments").addToBackStack("Comments").commit();
    }

    private class ClearUserGcmKeyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

                List<NameValuePair> args = new ArrayList<NameValuePair>();
                args.add(new BasicNameValuePair("username", ApplicationHelper.UserName));
                JsonHttpClient jsonHttpClient = new JsonHttpClient();
                String keyUrl = UrlHelper.CLEAR_USER_KEY;
                return jsonHttpClient.PostParams(keyUrl, args);
            }
    }


}
