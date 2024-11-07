package com.example.a1_jubair_6_frontend.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.ChatActivity;
import com.example.a1_jubair_6_frontend.activities.LoginSignupActivity;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GroupFragment extends Fragment {

    private ProfileDataManager profileDataManager;
    Button enter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGroupData(view, 29);

        enter = view.findViewById(R.id.btnEnter);
        enter.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            startActivity(intent);
        });
    }

    public void getGroupData(View view, int groupId) {
        String requestUrl = AppConstants.SERVER_URL + "/group/" + groupId;

        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("Stop", "Stopped here");
                            String groupName = response.getString("groupName");
                            JSONObject plan = response.getJSONObject("plan");
                            String planName = plan.getString("name");
                            int calories = plan.getInt("calories");
                            int totalFat = plan.getInt("totalFat");
                            int sodium = plan.getInt("sodium");
                            int carbohydrate = plan.getInt("carbohydrate");
                            int protein = plan.getInt("protein");

                            JSONArray members = response.getJSONArray("members");
                            JSONObject member = members.getJSONObject(0);
                            JSONObject id = member.getJSONObject("id");
                            int userId = id.getInt("userId");

                            updatePlan(view, groupName, planName, calories, totalFat, sodium, carbohydrate, protein);
                            getUserInfo(view, userId);


                        } catch (JSONException e) {
                            Log.e("JSON Error", "Failed to parse group data: " + e.getMessage());
                            Toast.makeText(requireContext(), "Failed to load group data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Group data retrieval failed: " + error.getMessage());
                        Toast.makeText(requireContext(), "Group data retrieval failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(groupRequest);
    }

    private void updatePlan(View view, String groupName, String planName, int calories, int totalFat, int sodium, int carbohydrate, int protein) {

        TextView planNameTextView = view.findViewById(R.id.planName);
        TextView caloriesTextView = view.findViewById(R.id.calories);
        TextView totalFatTextView = view.findViewById(R.id.totalFat);
        TextView sodiumTextView = view.findViewById(R.id.sodium);
        TextView carbohydrateTextView = view.findViewById(R.id.carbohydrate);
        TextView proteinTextView = view.findViewById(R.id.protein);
        TextView groupNameTextView = view.findViewById(R.id.groupName);

        planNameTextView.setText(planName);
        caloriesTextView.setText(String.valueOf(calories));
        totalFatTextView.setText(String.valueOf(totalFat));
        sodiumTextView.setText(String.valueOf(sodium));
        carbohydrateTextView.setText(String.valueOf(carbohydrate));
        proteinTextView.setText(String.valueOf(protein));
        groupNameTextView.setText(groupName);

    }

    public void updateMembers(View view, String memberName) {

        TextView memberNameTextView = view.findViewById(R.id.memberName);
        memberNameTextView.setText(memberName);
    }

    public void getUserInfo(View view, int id) {
        String requestUrl = AppConstants.SERVER_URL + "/user/" + id;

        JsonObjectRequest groupRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String fname = response.getString("fname");
                            String lname = response.getString("lname");
                            String memberName = fname + " " + lname;
                            updateMembers(view, memberName);

                        } catch (JSONException e) {
                            Log.e("JSON Error", "Failed to parse user data: " + e.getMessage());
                            Toast.makeText(requireContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "User data retrieval failed: " + error.getMessage());
                        Toast.makeText(requireContext(), "User data retrieval failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(groupRequest);
    }
}
