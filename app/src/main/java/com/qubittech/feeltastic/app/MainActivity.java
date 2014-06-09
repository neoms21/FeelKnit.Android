package com.qubittech.feeltastic.app;

import android.app.Activity;
import android.os.Bundle;

import com.qubittech.feeltastic.navigation.AbstractNavDrawerActivity;
import com.qubittech.feeltastic.navigation.NavDrawerActivityConfiguration;
import com.qubittech.feeltastic.navigation.NavDrawerAdapter;
import com.qubittech.feeltastic.navigation.NavDrawerItem;
import com.qubittech.feeltastic.navigation.NavMenuBuilder;

/**
 * Created by Manoj on 08/06/2014.
 */
public class MainActivity extends AbstractNavDrawerActivity {
    @Override
    protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
        NavMenuBuilder navBuilder = new NavMenuBuilder();
        String[] drawerItems = getResources().getStringArray(R.array.navigation_drawer_options);
        int id = 101;
        for (String item : drawerItems) {
            navBuilder.addSectionItem(id, item, item.toLowerCase(), true, getApplicationContext());
            id++;
        }


//                .addSectionItem(105, "wrw", "icon", true, getApplicationContext())
//                .addSectionItem(106, "wfs", "icon", true, getApplicationContext())
//                .addSectionItem(107, "12", "icon", true, getApplicationContext());
//                .addSectionItem(101, R.string.navdrawer_listdetail, R.drawable.navdrawer_friends, true, true)
//                .addSectionItem(102, R.string.navdrawer_airport, R.drawable.navdrawer_airport, true, true)
//                .addSectionItem(103, R.string.navdrawer_simplemap, R.drawable.navdrawer_map, true, true)
//                .addSectionItem(105, R.string.navdrawer_aroundme, R.drawable.navdrawer_aroundme, true, true)
//                .addSection(250, R.string.navdrawer_profile)
//                .addDrawerItem(NavDrawerUserLoginItem.createMenuItem(260, R.drawable.navdrawer_user, mUserHelper))
//                .addSection(200, R.string.navdrawer_general)
//                .addSectionItem(201, R.string.navdrawer_settings, R.drawable.navdrawer_settings, true, true)
//                .addSectionItem(202, R.string.navdrawer_rating, R.drawable.navdrawer_rating, false, false)
//                .addSectionItem(203, R.string.navdrawer_donations, R.drawable.navdrawer_donations, true, true)
//                .addSectionItem(204, R.string.navdrawer_changelog, R.drawable.navdrawer_changelog, false, false)
//                .addSectionItem(205, R.string.navdrawer_eula, R.drawable.navdrawer_eula, false, false);

//        if ( this.mUserHelper.getCurrentUser() != null ) {
//            navBuilder.addDrawerItemAtIndex(logoutDrawerItem, 8);
//        }

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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new UserFeelingsActivity(), "User Feelings").commit();
    }
}
