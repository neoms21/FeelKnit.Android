package com.qubittech.feeltastic.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.qubittech.feeltastic.models.Feeling;
import com.qubittech.feeltastic.navigation.AbstractNavDrawerActivity;
import com.qubittech.feeltastic.navigation.NavDrawerActivityConfiguration;
import com.qubittech.feeltastic.navigation.NavDrawerAdapter;
import com.qubittech.feeltastic.navigation.NavDrawerItem;
import com.qubittech.feeltastic.navigation.NavMenuBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Manoj on 08/06/2014.
 */
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
                .adapter(adapter)
                .drawerIcon(R.drawable.ic_drawer)
                .build();

        return navDrawerActivityConfiguration;
    }

    @Override
    protected void onNavItemSelected(int id) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean isRegister = false;
        Bundle bld = intent.getExtras();// ("IsFromRegister", isRegister);

        isRegister = bld == null ? false : bld.getBoolean("IsFromRegister");
        Button btnSignout = (Button) findViewById(R.id.signout);
        btnSignout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                settings.edit().remove("Username").commit();
                settings.edit().remove("Password").commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
        bundle.putSerializable("user", "neoms21");
        commentsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, commentsFragment, "Comments").addToBackStack("Comments").commit();
    }
}
