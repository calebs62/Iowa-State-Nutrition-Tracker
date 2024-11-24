package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.FoodPlan;
import com.example.a1_jubair_6_frontend.models.Group;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;


public class GoalsFragment extends Fragment {
    private static final String TAG = "GoalsFragment";
    private ProfileDataManager profileDataManager;
    private TextView consecLoginGoal;
    private TextView groupNameText;
    private TextView planNameText;
    private TextView caloriesText;
    private TextView proteinText;
    private TextView carbsText;
    private TextView fatText;
    private TextView sodiumText;
    private MaterialCardView planCard;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadUserGroup();
        getUserInfo(profileDataManager.getId());
    }

    private void initViews(View view) {
        consecLoginGoal = view.findViewById(R.id.consecLoginGoal);
        groupNameText = view.findViewById(R.id.groupNameText);
        planNameText = view.findViewById(R.id.planNameText);
        caloriesText = view.findViewById(R.id.caloriesText);
        proteinText = view.findViewById(R.id.proteinText);
        carbsText = view.findViewById(R.id.carbsText);
        fatText = view.findViewById(R.id.fatText);
        sodiumText = view.findViewById(R.id.sodiumText);
        planCard = view.findViewById(R.id.planCard);
    }

    private void loadUserGroup() {
        int userId = profileDataManager.getId();
        String url = AppConstants.SERVER_URL + "/searchGroups?userId=" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Loop through groups to find one where the user is a member
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject groupJson = response.getJSONObject(i);
                            JSONArray members = groupJson.getJSONArray("members");

                            // Check if user is a member of this group
                            for (int j = 0; j < members.length(); j++) {
                                JSONObject memberObj = members.getJSONObject(j);
                                JSONObject memberId = memberObj.getJSONObject("id");
                                if (memberId.getInt("userId") == userId) {
                                    // Found the user's group
                                    Group group = gson.fromJson(groupJson.toString(), Group.class);
                                    if (group.getGroupName() != null) {
                                        groupNameText.setText(group.getGroupName());
                                        if (group.getPlan() != null) {
                                            updatePlanUI(group.getPlan());
                                        } else {
                                            showNoPlanMessage();
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                        showNoGroupMessage();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing groups: " + e.getMessage(), e);
                        showNoGroupMessage();
                    }
                },
                error -> {
                    Log.e(TAG, "Error getting groups: " + error.getMessage());
                    showNoGroupMessage();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void loadGroupPlan(int groupId) {
        String url = AppConstants.SERVER_URL + "/group/" + groupId + "/getPlan";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        FoodPlan plan = gson.fromJson(response.toString(), FoodPlan.class);
                        updatePlanUI(plan);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing plan: " + e.getMessage());
                        showNoPlanMessage();
                    }
                },
                error -> {
                    Log.e(TAG, "Error getting plan: " + error.getMessage());
                    showNoPlanMessage();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void updatePlanUI(FoodPlan plan) {
        if (plan == null) {
            showNoPlanMessage();
            return;
        }

        planNameText.setText(plan.getName());
        caloriesText.setText(getString(R.string.calories_format, plan.getCalories()));
        proteinText.setText(getString(R.string.protein_format, plan.getProtein()));
        carbsText.setText(getString(R.string.carbs_format, plan.getCarbohydrate()));
        fatText.setText(getString(R.string.fat_format, plan.getTotalFat()));
        sodiumText.setText(getString(R.string.sodium_format, plan.getSodium()));
        planCard.setVisibility(View.VISIBLE);
    }

    private void showNoGroupMessage() {
        groupNameText.setText(R.string.no_group_message);
        planCard.setVisibility(View.GONE);
    }

    private void showNoPlanMessage() {
        planNameText.setText(R.string.no_plan_message);
        caloriesText.setText("-");
        proteinText.setText("-");
        carbsText.setText("-");
        fatText.setText("-");
        sodiumText.setText("-");
    }

    public void getUserInfo(int id) {
        String requestUrl = AppConstants.SERVER_URL + "/user/" + id;

        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                null,
                response -> {
                    try {
                        String lastLogin = response.getString("lastLogin");
                        consecLoginGoal(lastLogin);

                    } catch (JSONException e) {
                        Log.e("JSON Error",
                                "Failed to parse user data: " + e.getMessage());
                        Toast.makeText(requireContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "User data retrieval failed: " + error.getMessage());
                    Toast.makeText(requireContext(), "User data retrieval failed.", Toast.LENGTH_SHORT).show();
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(groupRequest);
    }

    public void consecLoginGoal(String info) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        LocalDate lastLoginDate = LocalDate.parse(info, formatter);
        LocalDate currentDate = LocalDate.now();
        String lastLog = lastLoginDate.toString();
        String curDate = currentDate.toString();

        Log.i("Dates: cur, last", currentDate + " " + lastLoginDate);

        int loginCount = profileDataManager.getConsecutiveLoginCount();

        if (!(lastLog.equals(curDate))) {
            loginCount += 1;
            Log.i("Login count: ", String.valueOf(loginCount));
            consecLoginGoal.setText(loginCount + "/5");
            profileDataManager.setConsecutiveLoginCount(loginCount);
            profileDataManager.setLastLoginDate(currentDate.toString());
        } else {
            consecLoginGoal.setText(loginCount + "/5");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}