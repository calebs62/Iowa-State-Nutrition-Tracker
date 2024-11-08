package com.example.a1_jubair_6_frontend.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ActivityFeedTestDialog extends DialogFragment {
    private TextInputEditText groupIdInput;
    private MaterialButton testActivityButton;
    private ProfileDataManager profileDataManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);

        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int dialogWidth = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_activity_feed_test, container, false);

        groupIdInput = view.findViewById(R.id.groupId);
        testActivityButton = view.findViewById(R.id.testActivityButton);

        setupTestButton();

        return view;
    }

    private void setupTestButton() {
        testActivityButton.setOnClickListener(v -> {
            String groupId = groupIdInput.getText().toString();
            if (groupId.isEmpty()) {
                groupIdInput.setError("Please enter a group ID");
                return;
            }

            int userId = profileDataManager.getId();
            if (userId == -1) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String activityType = getSelectedActivityType();
            if (activityType == null) {
                Toast.makeText(requireContext(), "Please select an activity type", Toast.LENGTH_SHORT).show();
                return;
            }

            createTestActivity(userId, Integer.parseInt(groupId), activityType);
        });
    }

    private String getSelectedActivityType() {
        int selectedId = ((android.widget.RadioGroup) requireView().findViewById(R.id.activityTypeGroup))
                .getCheckedRadioButtonId();

        if (selectedId == R.id.radioFood) return "food";
        if (selectedId == R.id.radioGoal) return "goal";
        if (selectedId == R.id.radioAchievement) return "achievement";
        if (selectedId == R.id.radioGroup) return "group";
        return null;
    }

    private void createTestActivity(int userId, int groupId, String type) {
        String url = String.format("%s/test/activity/%d/%d/%s", AppConstants.SERVER_URL, userId, groupId, type);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(requireContext(),
                            "Test activity created successfully",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                },
                error -> {
                    String errorMessage = error.getMessage();
                    if (errorMessage == null) {
                        errorMessage = "Unknown error occurred";
                    }
                    Toast.makeText(requireContext(),
                            "Failed to create test activity: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
