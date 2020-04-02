package com.example.consumerapp.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {

    private static final String PREFS_NAME = "MovieCataloguePref";
    private static final String APP_FIRST_RUN = "app_first_run";
    private SharedPreferences preferences;

    public AppPreference(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setAppFirstRun(Boolean input) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(APP_FIRST_RUN, input);
        editor.apply();
    }

    public Boolean getAppFirstRun() {
        return preferences.getBoolean(APP_FIRST_RUN, true);
    }
}
