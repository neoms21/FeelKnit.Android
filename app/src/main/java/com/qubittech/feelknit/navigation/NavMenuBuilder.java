package com.qubittech.feelknit.navigation;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class NavMenuBuilder {

    private List<NavDrawerItem> mMenu = new ArrayList<NavDrawerItem>();

    public NavMenuBuilder addSection( int id, int label ) {
        NavMenuSection section = new NavMenuSection();
//        section.setLabel(label);
        mMenu.add(section);
        return this;
    }

    public NavMenuBuilder addSectionItem(  int id, String label, String icon, boolean updateActionBarTitle, Context context ) {
        mMenu.add(NavMenuItem.create(id, label, icon, updateActionBarTitle, context));
        return this;
    }

//    public NavMenuBuilder addSectionItemAtIndex(  int id, int label, int icon, boolean updateActionBarTitle, boolean checkable, int index ) {
//        mMenu.add(index, NavMenuItem.create(id, label, icon, updateActionBarTitle, checkable));
//        return this;
//    }

    public NavMenuBuilder addDrawerItem( NavDrawerItem customItem ) {
        mMenu.add(customItem);
        return this;
    }

    public NavMenuBuilder addDrawerItemAtIndex( NavDrawerItem customItem, int index ) {
        mMenu.add(index, customItem);
        return this;
    }

    public NavDrawerItem[] build() {
        return mMenu.toArray(new NavDrawerItem[mMenu.size()]);
    }
}