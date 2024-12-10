package com.example.a1_jubair_6_frontend.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.ChatActivity;
import com.example.a1_jubair_6_frontend.adapters.GroupListAdapter;
import com.example.a1_jubair_6_frontend.adapters.MemberListAdapter;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.Group;
import com.example.a1_jubair_6_frontend.models.GroupMember;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GroupFragment extends Fragment implements GroupListAdapter.OnGroupJoinClickListener {
    private static final String TAG = "GroupFragment";
    private static final int REQUEST_TIMEOUT_MS = 10000; // 10 seconds timeout

    private ProfileDataManager profileDataManager;
    private View groupMemberView;
    private View noGroupView;
    private LottieAnimationView progressIndicator;
    private GroupListAdapter groupListAdapter;
    private MaterialCardView foodPlanCard;
    private MaterialButton toggleFoodPlanButton;
    private View foodPlanDetails;
    private int currentGroupId = -1;
    private GroupMembershipCallback membershipCallback;
    private boolean isOwner = false, isContributor = false, isAdmin = false, isModerator = false;
    private String groupName;

    public interface GroupMembershipCallback {
        void onGroupMembershipChanged();
    }

    public void setGroupMembershipCallback(GroupMembershipCallback callback) {
        this.membershipCallback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());

        isAdmin = profileDataManager.getAccountType().equals("ADMINISTRATOR");
        isContributor = profileDataManager.getAccountType().equals("CONTRIBUTOR");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupListeners(view);
        setupCreateGroupButton(view);
        setupFoodPlanToggle(view);
        checkGroupMembership();
    }

    private void initializeViews(View view) {
        groupMemberView = view.findViewById(R.id.groupMemberView);
        noGroupView = view.findViewById(R.id.noGroupView);
        progressIndicator = view.findViewById(R.id.lottieAnimationView);
        foodPlanCard = view.findViewById(R.id.foodPlanCard);
        toggleFoodPlanButton = view.findViewById(R.id.toggleFoodPlanButton);
        foodPlanDetails = view.findViewById(R.id.foodPlanDetails);

        groupListAdapter = new GroupListAdapter(this);
        androidx.recyclerview.widget.RecyclerView groupsList = view.findViewById(R.id.groupsList);
        groupsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        groupsList.setAdapter(groupListAdapter);

        com.google.android.material.textfield.TextInputEditText searchInput =
                view.findViewById(R.id.searchGroupsInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchGroups(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupListeners(View view) {
        MaterialButton leaveGroupButton = view.findViewById(R.id.leaveGroupButton);
        leaveGroupButton.setOnClickListener(v -> leaveCurrentGroup());

        MaterialButton enterChatButton = view.findViewById(R.id.enterChatButton);
        enterChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("groupId", currentGroupId);
            startActivity(intent);
        });

        toggleFoodPlanButton.setOnClickListener(v -> {
            boolean isVisible = foodPlanDetails.getVisibility() == View.VISIBLE;
            foodPlanDetails.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            toggleFoodPlanButton.setIconResource(isVisible ?
                    R.drawable.ic_expand_more : R.drawable.ic_expand_less);
        });

        MaterialButton deleteButton = view.findViewById(R.id.deleteGroupButton);
        deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Group")
                    .setMessage("Are you sure you want to delete this group? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> deleteGroup())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        MaterialButton addMember = view.findViewById(R.id.addMemberButton);
        addMember.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_member_actions, null);

            EditText input = dialogView.findViewById(R.id.inputMemberEmail);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            Button btnAdd = dialogView.findViewById(R.id.btnAction);
            TextView title = dialogView.findViewById(R.id.dialogTitle);

            title.setText("Add Member");
            btnAdd.setText("Add");

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            btnCancel.setOnClickListener(vi -> dialog.dismiss());

            btnAdd.setOnClickListener(vi -> {
                String memberEmail = input.getText().toString().trim();
                if (!memberEmail.isEmpty()) {
                    findUserByEmail(memberEmail, 0);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Member Email cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.show();
        });

        MaterialButton kickMember = view.findViewById(R.id.kickMemberButton);
        kickMember.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_member_actions, null);

            EditText input = dialogView.findViewById(R.id.inputMemberEmail);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            Button btnKick = dialogView.findViewById(R.id.btnAction);
            TextView title = dialogView.findViewById(R.id.dialogTitle);

            title.setText("Kick Member");
            btnKick.setText("Kick");

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            btnCancel.setOnClickListener(vi -> dialog.dismiss());

            btnKick.setOnClickListener(vi -> {
                String memberEmail = input.getText().toString().trim();
                if (!memberEmail.isEmpty()) {
                    findUserByEmail(memberEmail, 1);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Member Email cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.show();
        });

        MaterialButton makeMod = view.findViewById(R.id.makeModButton);
        makeMod.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_member_actions, null);

            EditText input = dialogView.findViewById(R.id.inputMemberEmail);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            Button btnMod = dialogView.findViewById(R.id.btnAction);
            TextView title = dialogView.findViewById(R.id.dialogTitle);

            title.setText("Give Moderator");
            btnMod.setText("Make Mod");

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            btnCancel.setOnClickListener(vi -> dialog.dismiss());

            btnMod.setOnClickListener(vi -> {
                String memberEmail = input.getText().toString().trim();
                if (!memberEmail.isEmpty()) {
                    findUserByEmail(memberEmail, 2);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Member Email cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            dialog.show();
        });
    }

    private void checkGroupMembership() {
        showLoading();
        String requestUrl = AppConstants.SERVER_URL + "/group/user/" + profileDataManager.getId();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                null,
                response -> {
                    try {
                        if (response != null && response.length() > 0) {
                            updateGroupMemberView(response);
                            showGroupMemberView();
                        } else {
                            showNoGroupView();
                            loadAvailableGroups("");
                        }
                    } catch (JSONException e) {
                        showNoGroupView();
                        loadAvailableGroups("");
                    }
                },
                error -> {
                    showNoGroupView();
                    loadAvailableGroups("");
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void updateFoodPlanInfo(JSONObject plan) throws JSONException {
        foodPlanDetails.setVisibility(View.VISIBLE);
        toggleFoodPlanButton.setIconResource(R.drawable.ic_expand_less);

        TextView planNameView = requireView().findViewById(R.id.planName);
        TextView caloriesView = requireView().findViewById(R.id.calories);
        TextView totalFatView = requireView().findViewById(R.id.totalFat);
        TextView sodiumView = requireView().findViewById(R.id.sodium);
        TextView carbohydrateView = requireView().findViewById(R.id.carbohydrate);
        TextView proteinView = requireView().findViewById(R.id.protein);

        String planName = plan.getString("name");
        planNameView.setText("Plan: " + planName);
        caloriesView.setText(String.format("Calories: %d kcal", plan.getInt("calories")));
        totalFatView.setText(String.format("Fat: %d g", plan.getInt("totalFat")));
        sodiumView.setText(String.format("Sodium: %d mg", plan.getInt("sodium")));
        carbohydrateView.setText(String.format("Carbs: %d g", plan.getInt("carbohydrate")));
        proteinView.setText(String.format("Protein: %d g", plan.getInt("protein")));

        foodPlanCard.setVisibility(View.VISIBLE);
    }


    private void searchGroups(String keyword) {
        if (!isAdded()) return;

        String requestUrl = AppConstants.SERVER_URL + "/searchGroups?keyword=" + keyword;
        Log.d(TAG, "Searching groups with URL: " + requestUrl);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                requestUrl,
                response -> {
                    try {
                        Log.d(TAG, "Search response: " + response);
                        JSONArray groups = new JSONArray(response);
                        groupListAdapter.updateGroups(groups);

                        View noResultsText = requireView().findViewById(R.id.noResultsText);
                        noResultsText.setVisibility(groups.length() == 0 ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse search results: " + e.getMessage());
                        if (keyword.length() > 2) {
                            handleError("Failed to load search results");
                        }
                    }
                },
                error -> {
                    String errorMessage = "Unknown error";
                    if (error.networkResponse != null) {
                        try {
                            String errorStr = new String(error.networkResponse.data);
                            Log.e(TAG, "Search error details: " + errorStr);
                            JSONObject errorJson = new JSONObject(errorStr);
                            errorMessage = errorJson.optString("message",
                                    errorJson.optString("error", "Search failed"));
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        }
                    }

                    if (keyword.length() > 2) {
                        handleError(errorMessage);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    @Override
    public void onGroupJoinClicked(int groupId) {
        String sessionToken = profileDataManager.getSessionToken();
        Log.d(TAG, "Attempting to join group " + groupId + " with token: " + sessionToken);
        String requestUrl = AppConstants.SERVER_URL + "/group/" + groupId + "/join";

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                requestUrl,
                response -> {
                    Toast.makeText(requireContext(), "Successfully joined group!", Toast.LENGTH_SHORT).show();
                    if (membershipCallback != null) {
                        membershipCallback.onGroupMembershipChanged();
                    }
                    checkGroupMembership();
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error response: " + new String(error.networkResponse.data));
                    }
                    if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                        handleError("Cannot join group - you might already be in a group.");
                    } else {
                        handleError("Failed to join group: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                String token = profileDataManager.getSessionToken();
                return token.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "text/plain";
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void leaveCurrentGroup() {
        if (currentGroupId == -1) {
            handleError("No group to leave");
            return;
        }

        String sessionToken = profileDataManager.getSessionToken();
        Log.d(TAG, "Attempting to leave group " + currentGroupId + " with token: " + sessionToken);
        String requestUrl = AppConstants.SERVER_URL + "/group/" + currentGroupId + "/leave";

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                requestUrl,
                response -> {
                    Log.d(TAG, "Successfully left group");
                    Toast.makeText(requireContext(), "Successfully left group!", Toast.LENGTH_SHORT).show();

                    if (membershipCallback != null && getActivity() != null) {
                        Log.d(TAG, "Notifying callback of group membership change");
                        getActivity().runOnUiThread(() -> membershipCallback.onGroupMembershipChanged());
                    }

                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        showNoGroupView();
                        loadAvailableGroups("");
                    }, 100);
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                        handleError("Cannot leave group - you might be the owner.");
                    } else {
                        handleError("Failed to leave group: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                return sessionToken.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "text/plain";
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void showLoading() {
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.setAnimation("loading.json");
        progressIndicator.loop(true);
        progressIndicator.playAnimation();
        groupMemberView.setVisibility(View.GONE);
        noGroupView.setVisibility(View.GONE);
    }

    private void showGroupMemberView() {
        progressIndicator.setVisibility(View.GONE);
        groupMemberView.setVisibility(View.VISIBLE);
        noGroupView.setVisibility(View.GONE);
    }

    private void showNoGroupView() {
        progressIndicator.setVisibility(View.GONE);
        groupMemberView.setVisibility(View.GONE);
        noGroupView.setVisibility(View.VISIBLE);
    }

    private void handleError(String message) {
        if (!isAdded()) return;

        progressIndicator.setVisibility(View.GONE);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
    }

    private void loadAvailableGroups(String keyword) {
        String requestUrl = AppConstants.SERVER_URL + "/allGroups";
        if (!keyword.isEmpty()) {
            requestUrl += "?keyword=" + keyword;
        }

        StringRequest request = new StringRequest(
                Request.Method.GET,
                requestUrl,
                response -> {
                    try {
                        JSONArray groups = new JSONArray(response);
                        groupListAdapter.updateGroups(groups);

                        View noResultsText = requireView().findViewById(R.id.noResultsText);
                        noResultsText.setVisibility(groups.length() == 0 ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        handleError("Failed to parse groups: " + e.getMessage());
                    }
                },
                volleyErrorHandler()
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void updateGroupMemberView(JSONObject groupData) throws JSONException {
        Log.d(TAG, "Group data received: " + groupData.toString());
        if (!groupData.isNull("plan")) {
            Log.d(TAG, "Plan data: " + groupData.getJSONObject("plan").toString());
            JSONObject plan = groupData.getJSONObject("plan");
            updateFoodPlanInfo(plan);
            foodPlanCard.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "No plan data found");
            foodPlanCard.setVisibility(View.GONE);
        }

        currentGroupId = groupData.getInt("id");

        groupName = groupData.getString("groupName");
        TextView groupNameView = requireView().findViewById(R.id.groupName);
        groupNameView.setText(groupName);

        JSONArray membersArray = groupData.getJSONArray("members");
        TextView memberCountView = requireView().findViewById(R.id.memberCount);
        memberCountView.setText(getString(R.string.member_count, membersArray.length()));

        int currentUserId = profileDataManager.getId();
        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberJson = membersArray.getJSONObject(i);
            JSONObject userId = memberJson.getJSONObject("id");
            if (userId.getInt("userId") == currentUserId && memberJson.getInt("permissionLvl") == 2) {
                isOwner = true;
                break;
            }
            else if(userId.getInt("userId") == currentUserId && memberJson.getInt("permissionLvl") == 1) {
                isModerator = true;
                break;
            }
        }

        MaterialButton leaveButton = requireView().findViewById(R.id.leaveGroupButton);
        MaterialButton deleteButton = requireView().findViewById(R.id.deleteGroupButton);
        MaterialButton kickButton = requireView().findViewById(R.id.kickMemberButton);
        MaterialButton addButton = requireView().findViewById(R.id.addMemberButton);
        MaterialButton makeMod = requireView().findViewById(R.id.makeModButton);

        if (isOwner) {
            leaveButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            kickButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            makeMod.setVisibility(View.VISIBLE);
        }
        else if (isAdmin) {
            leaveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            kickButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            makeMod.setVisibility(View.GONE);
        }
        else if (isModerator) {
            leaveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            kickButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            makeMod.setVisibility(View.GONE);
        }
        else {
            leaveButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            kickButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            makeMod.setVisibility(View.GONE);
        }

        if (!groupData.isNull("plan")) {
            JSONObject plan = groupData.getJSONObject("plan");
            updateFoodPlanInfo(plan);
            foodPlanCard.setVisibility(View.VISIBLE);
        } else {
            foodPlanCard.setVisibility(View.GONE);
        }

        List<GroupMember> members = new ArrayList<>();
        for (int i = 0; i < membersArray.length(); i++) {
            JSONObject memberJson = membersArray.getJSONObject(i);
            JSONObject userId = memberJson.getJSONObject("id");
            int permissionLevel = memberJson.getInt("permissionLvl");

            Group group = new Group();
            group.setId(groupData.getInt("id"));
            group.setGroupName(groupName);

            User user = new User(userId.getInt("userId"));
            GroupMember member = new GroupMember(group, user);

            String requestUrl = AppConstants.SERVER_URL + "/user/" + member.getUser().getId();
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    requestUrl,
                    null,
                    response -> {
                        try {
                            String accountType = response.getString("accounttype");
                            if(accountType.equals("ADMINISTRATOR") && permissionLevel != 2 && permissionLevel != 1) {
                                member.setPermissionLvl(4);
                            }
                            else if(accountType.equals("CONTRIBUTOR") && permissionLevel != 2 && permissionLevel != 1) {
                                member.setPermissionLvl(3);
                            }
                            else {
                                member.setPermissionLvl(permissionLevel);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse user info: " + e.getMessage());
                        }
                    },
                    error -> Log.e(TAG, "Failed to load user info: " + error.getMessage())
            );
            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);

            members.add(member);
            loadMemberUserInfo(member);
        }

        RecyclerView memberList = requireView().findViewById(R.id.memberList);
        if (memberList.getAdapter() == null) {
            memberList.setLayoutManager(new LinearLayoutManager(requireContext()));
            memberList.setAdapter(new MemberListAdapter());
        }

        ((MemberListAdapter) memberList.getAdapter()).updateMembers(members);
    }

    private void loadMemberUserInfo(GroupMember member) {
        String requestUrl = AppConstants.SERVER_URL + "/user/" + member.getUser().getId();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                null,
                response -> {
                    try {
                        member.getUser().setFname(response.getString("fname"));
                        member.getUser().setLname(response.getString("lname"));
                        RecyclerView memberList = requireView().findViewById(R.id.memberList);
                        if (memberList != null && memberList.getAdapter() != null) {
                            memberList.getAdapter().notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse user info: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Failed to load user info: " + error.getMessage())
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void setupFoodPlanToggle(View view) {
        toggleFoodPlanButton = view.findViewById(R.id.toggleFoodPlanButton);
        foodPlanDetails = view.findViewById(R.id.foodPlanDetails);

        if (toggleFoodPlanButton != null && foodPlanDetails != null) {
            foodPlanDetails.setVisibility(View.VISIBLE);
            toggleFoodPlanButton.setIconResource(R.drawable.ic_expand_less);

            toggleFoodPlanButton.setOnClickListener(v -> {
                boolean isVisible = foodPlanDetails.getVisibility() == View.VISIBLE;
                foodPlanDetails.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                toggleFoodPlanButton.setIconResource(isVisible ?
                        R.drawable.ic_expand_more : R.drawable.ic_expand_less);
            });
        }
    }

    private Response.ErrorListener volleyErrorHandler() {
        return error -> {
            String message = "Network error";
            if (error.networkResponse != null && error.networkResponse.data != null) {
                try {
                    String errorStr = new String(error.networkResponse.data);
                    JSONObject errorJson = new JSONObject(errorStr);
                    message = errorJson.optString("message", "Unknown error occurred");
                } catch (JSONException e) {
                    message = "Error parsing server response";
                }
            } else if (error.getMessage() != null) {
                message = error.getMessage();
            }
            handleError(message);
        };
    }

    private void deleteGroup() {
        if (currentGroupId == -1) {
            handleError("No group to delete");
            return;
        }

        String sessionToken = profileDataManager.getSessionToken();
        String requestUrl = AppConstants.SERVER_URL + "/group/" + currentGroupId + "?sessionToken=" + sessionToken;

        Log.d(TAG, "Attempting to delete group " + currentGroupId + " with token: " + sessionToken);
        Log.d(TAG, "Current user ID: " + profileDataManager.getId());

        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                requestUrl,
                response -> {
                    Log.d(TAG, "Group deleted successfully");
                    Toast.makeText(requireContext(), "Successfully deleted group!",
                            Toast.LENGTH_SHORT).show();
                    if (membershipCallback != null) {
                        membershipCallback.onGroupMembershipChanged();
                    }
                    showNoGroupView();
                    loadAvailableGroups("");
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorData = new String(error.networkResponse.data);
                        Log.e(TAG, "Delete error response: " + errorData);
                        try {
                            JSONObject errorJson = new JSONObject(errorData);
                            Log.e(TAG, "Error details: " + errorJson.toString(2));
                        } catch (JSONException e) {
                            Log.e(TAG, "Raw error data: " + errorData);
                        }
                    }
                    handleError("Cannot delete group - verification failed");
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    @Override
    public void showPlanDetails(JSONObject plan) {
        try {
            @SuppressLint("DefaultLocale") String content = String.format(
                    "Plan: %s\n\n" +
                            "Calories: %d kcal\n" +
                            "Protein: %d g\n" +
                            "Carbohydrates: %d g\n" +
                            "Fat: %d g\n" +
                            "Sodium: %d mg",
                    plan.getString("name"),
                    plan.getInt("calories"),
                    plan.getInt("protein"),
                    plan.getInt("carbohydrate"),
                    plan.getInt("totalFat"),
                    plan.getInt("sodium")
            );

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Food Plan Details")
                    .setMessage(content)
                    .setPositiveButton("Close", null);

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.full_rounded_dialog_background);
            });
            dialog.show();
        } catch (JSONException e) {
            handleError("Error showing plan details");
        }
    }

    private void setupCreateGroupButton(View view) {
        MaterialButton createGroupButton = view.findViewById(R.id.createGroupButton);
        createGroupButton.setOnClickListener(v -> showCreateGroupDialog());
    }

    private void showCreateGroupDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_group, null);

        TextInputEditText groupNameInput = dialogView.findViewById(R.id.groupNameInput);
        TextInputEditText planNameInput = dialogView.findViewById(R.id.planNameInput);
        TextInputEditText caloriesInput = dialogView.findViewById(R.id.caloriesInput);
        TextInputEditText proteinInput = dialogView.findViewById(R.id.proteinInput);
        TextInputEditText carbsInput = dialogView.findViewById(R.id.carbsInput);
        TextInputEditText fatInput = dialogView.findViewById(R.id.fatInput);
        TextInputEditText sodiumInput = dialogView.findViewById(R.id.sodiumInput);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create New Group")
                .setView(dialogView)
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button createButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            createButton.setOnClickListener(view -> {
                String groupName = groupNameInput.getText().toString().trim();
                String planName = planNameInput.getText().toString().trim();

                if (groupName.isEmpty() || planName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int calories = Integer.parseInt(caloriesInput.getText().toString());
                    int protein = Integer.parseInt(proteinInput.getText().toString());
                    int carbs = Integer.parseInt(carbsInput.getText().toString());
                    int fat = Integer.parseInt(fatInput.getText().toString());
                    int sodium = Integer.parseInt(sodiumInput.getText().toString());

                    createFoodPlan(planName, calories, protein, carbs, fat, sodium,
                            planId -> createGroup(groupName, planId, dialog));

                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void createFoodPlan(String name, int calories, int protein, int carbs, int fat, int sodium,
                                Consumer<Integer> onSuccess) {
        String url = AppConstants.SERVER_URL + "/plan";

        JSONObject planData = new JSONObject();
        try {
            planData.put("name", name);
            planData.put("calories", calories);
            planData.put("protein", protein);
            planData.put("carbohydrate", carbs);
            planData.put("totalFat", fat);
            planData.put("sodium", sodium);
        } catch (JSONException e) {
            handleError("Error creating food plan data");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, planData,
                response -> {
                    try {
                        int planId = response.getInt("id");
                        onSuccess.accept(planId);
                    } catch (JSONException e) {
                        handleError("Error parsing food plan response");
                    }
                },
                error -> {
                    Log.e(TAG, "Error creating food plan: " + error.toString());
                    handleError("Failed to create food plan");
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void createGroup(String groupName, int planId, AlertDialog dialog) {
        String url = AppConstants.SERVER_URL + "/group";

        JSONObject groupData = new JSONObject();
        try {
            groupData.put("groupName", groupName);
            groupData.put("ownerId", profileDataManager.getId());
            groupData.put("planId", planId);
        } catch (JSONException e) {
            handleError("Error creating group data");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, groupData,
                response -> {
                    try {
                        int groupId = response.getInt("id");
                        dialog.dismiss();
                        onGroupJoinClicked(groupId);
                    } catch (JSONException e) {
                        handleError("Error parsing group response");
                    }
                },
                error -> handleError("Failed to create group")
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void addMemberById(String memberId) {
        String url = AppConstants.SERVER_URL + "/group/" + currentGroupId + "/addMember";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", "1:2:" + profileDataManager.getId());
            jsonObject.put("uid", Integer.parseInt(memberId));
        } catch (JSONException e) {
            handleError("Error adding member to group.");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonObject,
                response -> {
                    Toast.makeText(requireContext(), "Member with id: " + memberId + " added successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(requireContext(), "Failed to add member. Please Try again.", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        checkGroupMembership();
    }

    private void kickMemberById(String memberId) {
        String url = AppConstants.SERVER_URL + "/group/" + currentGroupId + "/removeMember";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", "1:2:" + profileDataManager.getId());
            jsonObject.put("uid", Integer.parseInt(memberId));
        } catch (JSONException e) {
            handleError(e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonObject,
                response -> {
                    Toast.makeText(getActivity(), "Member with id: " + memberId + " removed successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getActivity(), "Failed to remove member. Please Try again.", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        checkGroupMembership();
    }

    private void modMember(String memberId) {
        String url = AppConstants.SERVER_URL + "/group/" + currentGroupId + "/promoteMod";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sessionToken", "1:2:" + profileDataManager.getId());
            jsonObject.put("uid", Integer.parseInt(memberId));
        } catch (JSONException e) {
            handleError(e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonObject,
                response -> {
                    Toast.makeText(getActivity(), "Member with id: " + memberId + " given mod successfully.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getActivity(), "Failed to mod member. Please try again.", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        checkGroupMembership();
    }

    private void findUserByEmail(String memberEmail, int option) {
        String url = AppConstants.SERVER_URL + "/allusers";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);
                                String username = userObject.getString("username");

                                if (username.equals(memberEmail) && option == 0) {
                                    String userId = String.valueOf(userObject.getInt("uid"));
                                    addMemberById(userId);
                                }
                                else if(username.equals(memberEmail) && option == 1) {
                                    String userId = String.valueOf(userObject.getInt("uid"));
                                    kickMemberById(userId);
                                }
                                else if(username.equals(memberEmail) && option == 2) {
                                    String userId = String.valueOf(userObject.getInt("uid"));
                                    modMember(userId);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.getMessage());
                    }
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
}
