package com.qubittech.feelknit.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qubittech.feelknit.fragments.AddFeelingFragment;
import com.qubittech.feelknit.fragments.BackHandledFragment;
import com.qubittech.feelknit.fragments.CommentsFragment;
import com.qubittech.feelknit.fragments.CurrentFeelingsFragment;
import com.qubittech.feelknit.fragments.ForgotFragment;
import com.qubittech.feelknit.fragments.ProfileFragment;
import com.qubittech.feelknit.fragments.RelatedFeelingFragment;
import com.qubittech.feelknit.fragments.UserFeelingsFragment;
import com.qubittech.feelknit.fragments.commentsFeelingsFragment;
import com.qubittech.feelknit.models.Feeling;
import com.qubittech.feelknit.navigation.AbstractNavDrawerActivity;
import com.qubittech.feelknit.navigation.NavDrawerActivityConfiguration;
import com.qubittech.feelknit.navigation.NavDrawerAdapter;
import com.qubittech.feelknit.navigation.NavDrawerItem;
import com.qubittech.feelknit.navigation.NavMenuBuilder;
import com.qubittech.feelknit.util.App;
import com.qubittech.feelknit.util.ApplicationHelper;
import com.qubittech.feelknit.util.ImageHelper;
import com.qubittech.feelknit.util.JsonHttpClient;
import com.qubittech.feelknit.util.UrlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractNavDrawerActivity implements AddFeelingFragment.OnCreateFeelingClick, BackHandledFragment.BackHandlerInterface {
    private Bundle intentBundle;

    @Override
    protected void onStart() {
        super.onStart();
        App.mainActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.mainActivity = null;
    }

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

        NavDrawerAdapter adapter = new NavDrawerAdapter(this, com.qubittech.feelknit.app.R.layout.navdrawer_item, menu);

        NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration.Builder()
                .mainLayout(com.qubittech.feelknit.app.R.layout.activity_main)
                .drawerLayoutId(com.qubittech.feelknit.app.R.id.drawer_layout)
                .leftDrawerId(com.qubittech.feelknit.app.R.id.left_drawer)
                .menu(menu)
                .adapter(adapter)
                .drawerIcon(com.qubittech.feelknit.app.R.drawable.ic_drawer)
                .build();

        return navDrawerActivityConfiguration;
    }

    @Override
    protected void onNavItemSelected(int id) {
        switch (id) {
            case 101:
                ShowProfileFragment(ApplicationHelper.getAvatar(getApplicationContext()));
                break;
            case 102:
                ShowCurrentFeelingsFragment();
                break;
            case 103:
                StartUserFeelingsFragment();
                break;
            case 104:
                showCommentsFeelingsFragment();
                break;
            case 105:
                ShowRelatedFeelingFragment();
                break;
            case 106:
                final Dialog d = new Dialog(this, R.style.CustomDialogTheme);
                d.setContentView(R.layout.custom_dialog);
                d.show();

                TextView version = (TextView) d.findViewById(R.id.versionTextView);
                TextView saripaar = (TextView) d.findViewById(R.id.saripaar);
                TextView licenseLink = (TextView) d.findViewById(R.id.licenseLink);
                final TextView license = (TextView) d.findViewById(R.id.license);
                saripaar.setText(Html.fromHtml("<u>android-saripaar</u>"));
                licenseLink.setText(Html.fromHtml("(<u>license</u>)"));

                saripaar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uriUrl = Uri.parse("https://github.com/ragunathjawahar/android-saripaar");
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                });

                license.setText("Copyright 2012 - 2015 Mobs and Geeks\n\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License.");
                licenseLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        license.setVisibility(license.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                });
                try {
                    version.setText(getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                TextView close_btn = (TextView) d.findViewById(R.id.okButton);
                close_btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
        }
    }

    private void ShowCurrentFeelingsFragment() {
        CurrentFeelingsFragment.fromMainActivity = true;
        CurrentFeelingsFragment fragment = new CurrentFeelingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "CurrentFeelings").addToBackStack("CurrentFeelings").commit();
    }

    private void ShowRelatedFeelingFragment() {
        RelatedFeelingFragment relatedFeelingFragment = new RelatedFeelingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, relatedFeelingFragment, "RelatedFeelings")
                .addToBackStack("RelatedFeelings").commit();
    }

    private void showCommentsFeelingsFragment() {
        commentsFeelingsFragment fragment = new commentsFeelingsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "CommentsFeelings").addToBackStack("CommentsFeelings").commit();
    }

    private void ShowProfileFragment(String avatar) {
        ProfileFragment profileFragment = ProfileFragment.newInstance();
        Bundle bundle = profileFragment.getArguments();
        bundle.putString("Avatar", avatar);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, profileFragment, "Profile")
                .addToBackStack("Profile").commit();
    }

    @Override
    public void onBackPressed() {

        BackHandledFragment fragment = getActiveFragment();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if ((count == 2 && fragment != null && (fragment.getTag().equals("CommentsFeelings")))
                || (count == 1 && fragment != null && (fragment.getTag().equals("RelatedFeelings")))) {
            ShowCurrentFeelingsFragment();
            return;
        }
        if (fragment != null && fragment.getTagText().equals("Comments") && count == 1) {
            showCommentsFeelingsFragment();
            return;
        }

        CurrentFeelingsFragment myFragment = (CurrentFeelingsFragment) getSupportFragmentManager().findFragmentByTag("CurrentFeelings");
        if (myFragment != null && myFragment.isVisible()) {
            App.close();
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
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        int switchNum = 0;
        Intent intent = getIntent();
        //boolean isRegister = false;
        intentBundle = intent.getExtras();
        Feeling feeling = null;
        String feelingId = null;
        if (intentBundle != null) {
            switchNum = intentBundle.getInt("From");
            feeling = (Feeling) intentBundle.get("feeling");
            feelingId = (String) intentBundle.get("feelingId");
        }
        //  isRegister = bundle == null ? false : bundle.getBoolean("IsFromRegister");


        ImageView userIconImageView = (ImageView) findViewById(R.id.leftDrawerUserIcon);
        if (ApplicationHelper.getAvatar(getApplicationContext()) != null)
            ImageHelper.setBitMap(userIconImageView, getApplicationContext(), ApplicationHelper.getAvatar(getApplicationContext()), 100, 100);

        TextView usrTextView = (TextView) findViewById(com.qubittech.feelknit.app.R.id.usrName);
        usrTextView.setText(ApplicationHelper.getUserName(getApplicationContext()));
        Button btnSignout = (Button) findViewById(com.qubittech.feelknit.app.R.id.signout);

        btnSignout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = ApplicationHelper.getUserName(getApplicationContext());
                ApplicationHelper.setUserName(getApplicationContext(), "");
                ApplicationHelper.setAvatar(getApplicationContext(), "");

                ApplicationHelper.setUserEmail(getApplicationContext(), "");
                App.close();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                new ClearUserGcmKeyTask().execute(username);
            }
        });

        switch (switchNum) {
            case 0:
                ShowCurrentFeelingsFragment();
                break;
            //Registration
            case 1:
                getSupportFragmentManager().beginTransaction().replace(com.qubittech.feelknit.app.R.id.content_frame, new AddFeelingFragment(), "Add Feeling").addToBackStack("Add Feeling").commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(com.qubittech.feelknit.app.R.id.content_frame, new ForgotFragment(), "Forgot Password").commit();
                break;
            case 3:
                ShowCommentsFragment(feeling, feelingId, null, null);
                break;
            case 4:
                String avatar = intentBundle.getString("Avatar", "");
                ShowProfileFragment(avatar);
                break;
            case 5:
                ShowRelatedFeelingFragment();
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
        getSupportFragmentManager().beginTransaction().replace(com.qubittech.feelknit.app.R.id.content_frame, userFeelingsFragment, "UserFeelings").addToBackStack("UserFeelings").commit();
    }

    @Override
    public void onFeelingCreated(Feeling feeling, List<Feeling> relatedFeelings) {
        RelatedFeelingFragment relatedFeelingsFragment = new RelatedFeelingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feeling", feeling);
        bundle.putSerializable("relatedFeelings", (Serializable) relatedFeelings);

        relatedFeelingsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(com.qubittech.feelknit.app.R.id.content_frame,
                relatedFeelingsFragment, "RelatedFeelings").addToBackStack("RelatedFeelings").commit();
    }

    public void AddCreateFeelingFragment() {
        getSupportFragmentManager().beginTransaction().replace(com.qubittech.feelknit.app.R.id.content_frame, new AddFeelingFragment(), "AddFeeling").addToBackStack("AddFeeling").commit();
    }

    public void ShowCommentsFragment(Feeling feeling, String feelingId, String feelingText,
                                     String username) {

        if (feeling == null && feelingId == null) {
            new getUserFeelingTask().execute(feelingText, username);
            return;
        }

        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feeling", feeling);
        bundle.putSerializable("feelingId", feelingId);
        bundle.putSerializable("user", ApplicationHelper.getUserName(getApplicationContext()));
        commentsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(com.qubittech.feelknit.app.R.id.content_frame, commentsFragment, "Comments").addToBackStack("Comments").commit();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {

    }


    public BackHandledFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return (BackHandledFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    private class ClearUserGcmKeyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ApplicationHelper.setAuthorizationToken(getApplicationContext(), "");
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("username", params[0]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            String keyUrl = UrlHelper.CLEAR_USER_KEY;
            return jsonHttpClient.PostParams(keyUrl, args);
        }
    }

    private class getUserFeelingTask extends AsyncTask<String, Integer, Feeling> {
        @Override
        protected void onPostExecute(Feeling feeling) {
            ShowCommentsFragment(feeling, null, null, null);
        }

        @Override
        protected Feeling doInBackground(String... params) {
            List<NameValuePair> args = new ArrayList<>();
            args.add(new BasicNameValuePair("feeling", params[0]));
            args.add(new BasicNameValuePair("username", params[1]));
            JsonHttpClient jsonHttpClient = new JsonHttpClient(getApplicationContext());
            String url = UrlHelper.USER_FEELINGS;
            String response = jsonHttpClient.Get(url, args);
            Gson gson = new Gson();
            return gson.fromJson(response, Feeling.class);
        }
    }
}
