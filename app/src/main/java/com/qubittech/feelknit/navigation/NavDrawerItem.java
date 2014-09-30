package com.qubittech.feelknit.navigation;

/**
 * Created by Manoj on 08/06/2014.
 */
public interface NavDrawerItem {
    public int getId();
    public String getLabel();
    public int getType();
    public boolean isEnabled();
    public boolean updateActionBarTitle();
}