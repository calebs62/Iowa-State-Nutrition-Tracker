package com.example.a1_jubair_6_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GoalsFragment extends Fragment {

    private ProfileDataManager profileDataManager;
    private ProgressBar pbGoal1, pbGoal2, pbGoal3;
    private TextView tvGoal1, tvGoal2, tvGoal3;

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
        int id = profileDataManager.getId();
        pbGoal1 = view.findViewById(R.id.pbConsecutiveLogins);
        pbGoal2 = view.findViewById(R.id.pbProteinGoal);
        pbGoal3 = view.findViewById(R.id.pbFoodPlans);
        tvGoal1 = view.findViewById(R.id.tvProgress);
        tvGoal2 = view.findViewById(R.id.tvProteinProgress);
        tvGoal3 = view.findViewById(R.id.tvFoodPlansProgress);

        getGoals();
    }

    private void getGoals() {
        String url = "https://1a56c054-2e8f-4d72-9b60-22ca8b114808.mock.pstmn.io/goals";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray goals = response.getJSONArray("goals");

                        for (int i = 0; i < goals.length(); i++) {
                            JSONObject goal = goals.getJSONObject(i);
                            String name = goal.getString("name");
                            int progress = goal.getInt("progress");
                            String complete = goal.getString("complete");

                            if(name.equals("goal1")) {
                                pbGoal1.setProgress(progress);
                                tvGoal1.setText(String.valueOf(progress) + "/7");
                            }
                            else if(name.equals("goal2")) {
                                pbGoal2.setProgress(progress);
                                tvGoal2.setText(String.valueOf(progress) + "/5");
                            }
                            else if(name.equals("goal3")) {
                                pbGoal3.setProgress(progress);
                                tvGoal3.setText(String.valueOf(progress) + "/7");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("getGoals", "Error parsing JSON response: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("getGoals", "Volley Error: " + error.getMessage());
                }
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }


}