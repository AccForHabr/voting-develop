package com.neoquest.voting.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.neoquest.voting.App;

public enum  SettingsModel {
    INSTANCE;

    public static final String PREFERENCE_FILE_KEY = "start_pref";
    private SharedPreferences sharedPreferences
            = App.getContext().getSharedPreferences(SettingsModel.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

    private static final String K = "k";
    private static final String PUBLIC_KEY = "public_key";
    private static final String IS_USER_VOTED = "user_name";

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    private void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    private String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    private void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void saveK(String k) {
        saveString(K, k);
    }

    public String getK() {
        return getString(K);
    }

    public void savePublicKey(String publicKey) {
        saveString(PUBLIC_KEY, publicKey);
    }

    public String getPublicKey() {
        return getString(PUBLIC_KEY);
    }

    public void setUserVoted() {
        saveBoolean(IS_USER_VOTED, true);
    }

    public boolean isUserAlreadyVoted() {
        return  getBoolean(IS_USER_VOTED);
    }
}
