package com.qubittech.feelknit.models;

public class ApplicationSettings {
    private boolean feelingsUpdated;
    private boolean newVersionAvailable;
private String versionName;

    public boolean isFeelingsUpdated() {
        return feelingsUpdated;
    }

    public void setFeelingsUpdated(boolean feelingsUpdated) {
        this.feelingsUpdated = feelingsUpdated;
    }

    public boolean isNewVersionAvailable() {
        return newVersionAvailable;
    }

    public void setNewVersionAvailable(boolean newVersionAvailable) {
        newVersionAvailable = newVersionAvailable;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
