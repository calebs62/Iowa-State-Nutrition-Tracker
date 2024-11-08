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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.ChatActivity;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class GoalsFragment extends Fragment {

    private ProfileDataManager profileDataManager;
    private TextView consecLoginGoal;

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

        consecLoginGoal = view.findViewById(R.id.consecLoginGoal);
        getUserInfo(view, id);

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
                            String lastLogin = response.getString("lastLogin");
                            consecLoginGoal(lastLogin);

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

    public void consecLoginGoal(String info) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        // Parse the last login date from the string
        LocalDate lastLoginDate = LocalDate.parse(info, formatter);
        LocalDate currentDate = LocalDate.now();
        String lastLog = lastLoginDate.toString();
        String curDate = currentDate.toString();

        Log.i("Dates: cur, last", currentDate.toString() + " " + lastLoginDate.toString());

        int loginCount = profileDataManager.getConsecutiveLoginCount();

        // Check if the last login date is before the current date
        if (!(lastLog.equals(curDate))) {

            loginCount += 1;

            Log.i("Login count: ", String.valueOf(loginCount));

            // Update the displayed goal count and save the new count and login date
            consecLoginGoal.setText(loginCount + "/5");
            profileDataManager.setConsecutiveLoginCount(loginCount);
            profileDataManager.setLastLoginDate(currentDate.toString());
        }
        else {
            consecLoginGoal.setText(loginCount + "/5");
        }
    }
}