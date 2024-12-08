package com.example.a1_jubair_6_frontend.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.PrivacySettings;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

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
    private static final String KEY_UID = "uid";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String PREF_SHARE_FOOD = "share_food";
    private static final String PREF_SHARE_GOALS = "share_goals";
    private static final String PREF_SHARE_ACHIEVEMENTS = "share_achievements";
    private static final String PREF_SHOW_FOOD = "show_food";
    private static final String PREF_SHOW_GOALS = "show_goals";
    private static final String PREF_SHOW_ACHIEVEMENTS = "show_achievements";
    private static final String KEY_CONSECUTIVE_LOGIN_COUNT = "consecutive_login_count";
    private static final String KEY_LAST_LOGIN_DATE = "last_login_date";
    private static final String KEY_GROUP_ID = "group_id";

    private static final String uploadDir = "uploads/profile-pictures/";

    private final SharedPreferences preferences;
    private final Context context;

    public ProfileDataManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserData(User user){
        preferences.edit()
                .putInt(KEY_UID, user.getId())
                .putString(KEY_FIRSTNAME, user.getFname())
                .putString(KEY_LASTNAME, user.getLname())
                .putString(KEY_EMAIL, user.getUsername())
                .putString(KEY_PASSWORD, user.getPassword())
                .putInt(KEY_HEIGHT, user.getHeight())
                .putInt(KEY_WEIGHT, user.getWeight())
                .putString(KEY_ACCOUNT, user.getAccounttype().toString())
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

    public void setEmail(String email){
        preferences.edit()
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public void setPassword(String password){
        preferences.edit()
                .putString(KEY_PASSWORD, password)
                .apply();
    }

    public void setWeight(int weight){
        preferences.edit()
                .putInt(KEY_WEIGHT, weight)
                .apply();
    }

    public void setHeight(int height){
        preferences.edit()
                .putInt(KEY_HEIGHT, height)
                .apply();
    }

    public void setPhoneNumber(String phoneNumber){
        preferences.edit()
                .putString(KEY_PHONE_NUMBER, phoneNumber)
                .apply();
    }

    public void setFoodSharingEnabled(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHARE_FOOD, enabled).apply();
    }

    public void setGoalSharingEnabled(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHARE_GOALS, enabled).apply();
    }

    public void setAchievementSharingEnabled(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHARE_ACHIEVEMENTS, enabled).apply();
    }

    public void setConsecutiveLoginCount(int count) {
        preferences.edit().putInt(KEY_CONSECUTIVE_LOGIN_COUNT, count).apply();
    }

    public void setLastLoginDate(String date) {
        preferences.edit().putString(KEY_LAST_LOGIN_DATE, date).apply();
    }

    public String getKeyLastLoginDate() {
        return preferences.getString(KEY_LAST_LOGIN_DATE, "");
    }

    public int getConsecutiveLoginCount() {
        return preferences.getInt(KEY_CONSECUTIVE_LOGIN_COUNT, 0);
    }

    public void setGroupId(int groupId) {
        preferences.edit().putInt(KEY_GROUP_ID, groupId).apply();
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

    public int getId() {return preferences.getInt(KEY_UID, -1); }

    public int getGroupId() {return preferences.getInt(KEY_GROUP_ID, -1); }

    public String getPhoneNumber() { return preferences.getString(KEY_PHONE_NUMBER, null); }

    public boolean getFoodSharingEnabled() {
        return preferences.getBoolean(PREF_SHARE_FOOD, true);
    }

    public boolean getGoalSharingEnabled() {
        return preferences.getBoolean(PREF_SHARE_GOALS, true);
    }

    public boolean getAchievementSharingEnabled() {
        return preferences.getBoolean(PREF_SHARE_ACHIEVEMENTS, true);
    }


    public User getUser() {
        int id = getId();
        String username = getEmail();
        String password = getPassword();
        int weight = getWeight();
        int height = getHeight();
        String fname = getFirstname();
        String lname = getLastname();
        User.Account accType = User.Account.valueOf(getAccountType());

        return new User(id, username, password, fname, lname, height, weight, accType);
    }

    public void clearUserData() {
        preferences.edit()
                .remove(KEY_FIRSTNAME)
                .remove(KEY_LASTNAME)
                .remove(KEY_EMAIL)
                .remove(KEY_PASSWORD)
                .remove(KEY_WEIGHT)
                .remove(KEY_HEIGHT)
                .remove(KEY_ACCOUNT)
                .remove(KEY_PROFILE_IMAGE_URI)
                .remove(KEY_PHONE_NUMBER)
                .apply();
    }

    public void setFood(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHARE_FOOD, enabled).apply();
    }

    public void setGoal(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHARE_GOALS, enabled).apply();
    }

    public void setAchievement(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHARE_ACHIEVEMENTS, enabled).apply();
    }

    public boolean getFood() {
        return preferences.getBoolean(PREF_SHARE_FOOD, true);
    }

    public boolean getGoal() {
        return preferences.getBoolean(PREF_SHARE_GOALS, true);
    }

    public boolean getAchievement() {
        return preferences.getBoolean(PREF_SHARE_ACHIEVEMENTS, true);
    }

    public void updatePrivacySettings(PrivacySettings settings, UpdateCallback callback) {
        String url = AppConstants.SERVER_URL + "/privacy/settings/" + getId();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("food", settings.getFood());
            jsonBody.put("goal", settings.getGoal());
            jsonBody.put("achievement", settings.getAchievement());
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                response -> {
                    setFood(settings.getFood());
                    setGoal(settings.getGoal());
                    setAchievement(settings.getAchievement());
                    callback.onSuccess();
                },
                error -> callback.onError("Network error: " + error.getMessage())
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void fetchPrivacySettings() {
        String url = AppConstants.SERVER_URL + "/privacy/settings/" + getId();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        setFoodSharingEnabled(response.getBoolean("food"));
                        setGoalSharingEnabled(response.getBoolean("goal"));
                        setAchievementSharingEnabled(response.getBoolean("achievement"));
                    } catch (JSONException e) {
                        Log.e("Privacy", "Error parsing privacy settings", e);
                    }
                },
                error -> Log.e("Privacy", "Error fetching privacy settings", error)
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void updateUserToServer(){
        String url = AppConstants.SERVER_URL + "/user/update/" + getId();

        User user = getUser();

        JSONObject jsonBody = new JSONObject();

        try{
            jsonBody.put("username", user.getUsername());
            jsonBody.put("password", user.getPassword());
            jsonBody.put("fname", user.getFname());
            jsonBody.put("lname", user.getLname());
            jsonBody.put("weight", user.getWeight());
            jsonBody.put("height", user.getHeight());
        }catch (Exception e){
            Log.e("JSON Exception", e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                response -> {
                    try {
                        int id = response.getInt("UID");
                        String username = response.getString("username");
                        String userPass = response.getString("password");
                        String fname = response.getString("fname");
                        String lname = response.getString("lname");
                        int heightS = response.getInt("height");
                        int weightS = response.getInt("weight");
                        String accountType = response.getString("accountType");

                        User userServer = new User(id, username, userPass, fname, lname, heightS, weightS, User.Account.valueOf(accountType));

                        saveUserData(userServer);
                    }catch (Exception e){
                        Log.e("Request Error", String.valueOf(e.getMessage()));
                    }
                },
                error -> {
                    Log.e("Update User Request Error", String.valueOf(error.getMessage()));
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public boolean isAdminOrContributor() {
        String role = getAccountType();
        return "ADMINISTRATOR".equals(role) || "CONTRIBUTOR".equals(role);
    }

    public void saveBase64Image(String base64Image) {
        try{
            String[] parts = base64Image.split(",");
            String imageString = parts.length > 1 ? parts[1] : parts[0];

            byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);

            String filename = "profile_" + getId() + ".png";

            File file = new File(context.getFilesDir(), filename);
            try (FileOutputStream fos = new FileOutputStream(file)){
                fos.write(imageBytes);
            }

            Uri imageUri = Uri.fromFile(file);
            preferences.edit().putString(KEY_PROFILE_IMAGE_URI, imageUri.toString()).apply();
        }
        catch (Exception e){
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public String getSessionToken() {
        return "1:" + getAccountTypeInt() + ":" + getId();
    }

    private int getAccountTypeInt(){
        String accountType = getAccountType();

        if (accountType.equals("CONTRIBUTOR"))
            return 1;
        else if (accountType.equals("ADMINISTRATOR"))
            return 2;
        else
            return 0;
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String error);
    }

    public boolean getShowGoals() {
        return preferences.getBoolean(PREF_SHOW_GOALS, true);
    }

    public boolean getShowFood() {
        return preferences.getBoolean(PREF_SHOW_FOOD, true);
    }

    public boolean getShowAchievements() {
        return preferences.getBoolean(PREF_SHOW_ACHIEVEMENTS, true);
    }

    public void setShowFood(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHOW_FOOD, enabled).apply();
    }

    public void setShowGoals(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHOW_GOALS, enabled).apply();
    }

    public void setShowAchievements(boolean enabled) {
        preferences.edit().putBoolean(PREF_SHOW_ACHIEVEMENTS, enabled).apply();
    }
}