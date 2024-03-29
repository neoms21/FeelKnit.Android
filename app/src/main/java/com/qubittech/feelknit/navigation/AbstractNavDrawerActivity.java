package com.qubittech.feelknit.navigation;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qubittech.feelknit.app.R;

public abstract class AbstractNavDrawerActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private NavDrawerActivityConfiguration navConf;

    protected abstract NavDrawerActivityConfiguration getNavDrawerConfiguration();

    protected abstract void onNavItemSelected(int id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navConf = getNavDrawerConfiguration();

        setContentView(navConf.getMainLayout());
        //View footer = getLayoutInflater().inflate(R.layout.drawer_list_footer, null);
        mTitle = mDrawerTitle = getTitle();


        mDrawerLayout = (DrawerLayout) findViewById(navConf.getDrawerLayoutId());
        mDrawerList = (ListView) findViewById(navConf.getLeftDrawerId());
        mDrawerList.setAdapter(navConf.getAdapter());
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//        mDrawerList.addFooterView(footer);
        this.initDrawerShadow();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                getDrawerIcon(),
                navConf.getDrawerOpenDesc(),
                navConf.getDrawerCloseDesc()
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar mActionBar = getActionBar();
        View v = getLayoutInflater().inflate(R.layout.header, null);
        if(mActionBar == null)
            return;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayUseLogoEnabled(true);
        mActionBar.setCustomView(v);
        mActionBar.setLogo(R.drawable.drawerheader);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        mActionBar.setIcon(R.drawable.drawerheader);
    }

    protected void initDrawerShadow() {

        mDrawerLayout.setDrawerShadow(navConf.getDrawerShadow(), GravityCompat.START);
    }

    protected int getDrawerIcon() {
        return R.drawable.drawer_shadow;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (navConf.getActionMenuItemsToHideWhenDrawerOpen() != null) {
            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
            for (int iItem : navConf.getActionMenuItemsToHideWhenDrawerOpen()) {
                menu.findItem(iItem).setVisible(!drawerOpen);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//            if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
//                this.mDrawerLayout.closeDrawer(this.mDrawerList);
//            } else {
//                this.mDrawerLayout.openDrawer(this.mDrawerList);
//            }
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    protected DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    protected ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {
        NavDrawerItem selectedItem = navConf.getNavItems()[position];

        this.onNavItemSelected(selectedItem.getId());
        mDrawerList.setItemChecked(position, true);

        if (selectedItem.updateActionBarTitle()) {
            setTitle(selectedItem.getLabel());
        }

        mDrawerLayout.closeDrawers();
//        if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
//            mDrawerLayout.closeDrawer(mDrawerLayout);
//        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}