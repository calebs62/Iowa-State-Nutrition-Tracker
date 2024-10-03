package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.a1_jubair_6_frontend.models.User;

public class ProfileDataManager {
    private static final String PREF_NAME = "ProfilePreferences";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_ACCOUNT = "account";

    private final SharedPreferences preferences;

    public ProfileDataManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserData(User user){
        preferences.edit()
                .putString(KEY_FIRSTNAME, user.getFname())
                .putString(KEY_LASTNAME, user.getLname())
                .putString(KEY_EMAIL, user.getUsername())
                .putString(KEY_PASSWORD, user.getPassword())
                .putInt(KEY_HEIGHT, user.getHeight())
                .putInt(KEY_WEIGHT, user.getWeight())
                .putString(KEY_ACCOUNT, user.getAccounttype())
                .apply();
    }

    public void saveProfileImageUri(Uri uri) {
        preferences.edit()
                .putString(KEY_PROFILE_IMAGE_URI, uri.toString())
                .apply();
    }

    public Uri getProfileImageUri() {
        String uriString = preferences.getString(KEY_PROFILE_IMAGE_URI, null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    public void saveEmailAndPassword(String email, String password){
        preferences.edit()
                .putString(KEY_EMAIL, email)
                .apply();
        preferences.edit()
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public String getEmail(){
        return preferences.getString(KEY_EMAIL, "");
    }

    public String getPassword(){
        return preferences.getString(KEY_PASSWORD, "");
    }

    public int getWeight(){
        return preferences.getInt(KEY_WEIGHT, -1);
    }

    public int getHeight() {
        return preferences.getInt(KEY_HEIGHT, -1);
    }

    public String getFirstname() {
        return preferences.getString(KEY_FIRSTNAME, "User");
    }

    public String getLastname() {
        return preferences.getString(KEY_LASTNAME, "");
    }

    public String getAccountType(){
        return preferences.getString(KEY_ACCOUNT, null);
    }
}