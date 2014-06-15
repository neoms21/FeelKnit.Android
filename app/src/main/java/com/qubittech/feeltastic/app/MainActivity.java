package com.qubittech.feeltastic.app;

import android.os.Bundle;

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
        UserFeelingsFragment userFeelingsFragment = new UserFeelingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", "From Activity");

        //set Fragmentclass Arguments
        userFeelingsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, userFeelingsFragment, "User Feelings").commit();


    }

    @Override
    public void onFeelingCreate(Feeling feeling, List<Feeling> relatedFeelings) {
        RelatedFeelingFragment relatedFeelingsFragment = new RelatedFeelingFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("feeling", feeling);
        bundle.putSerializable("relatedFeelings", (Serializable) relatedFeelings);

        //set Fragmentclass Arguments
        relatedFeelingsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, relatedFeelingsFragment, "Related Feelings").commit();
    }

    public void AddCreateFeelingFragment()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AddFeelingFragment(), "Share Feeling").commit();
    }
}
