package com.example.a1_jubair_6_frontend.fragments.profile;

import static java.lang.String.*;

import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.a1_jubair_6_frontend.activities.AdminPanelFragment;
import com.example.a1_jubair_6_frontend.activities.LoginSignupActivity;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.activities.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragment representing the user's profile information, including their picture, weight, height,
 * and settings options. This fragment allows users to view and update their profile data, change
 * profile picture, and navigate to different sections like personal information, security settings,
 * and notifications.
 *
 * @author Caleb Sanchez, Alexander Svobodny
 */
public class ProfileFragment extends Fragment {

    private View rootView;
    ImageView profilePicture;
    TextView username;
    Button logout;
    TextView weight;
    TextView height;
    private Uri profilePictureUri;
    private ProfileDataManager profileDataManager;
    boolean isAdmin = false;
    boolean isContributor = false;

    /**
     * Launcher for updating the profile picture.
     */
    private final ActivityResultLauncher<Intent> updateProfilePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.hasExtra("imageUri")) {
                        String uriString = data.getStringExtra("imageUri");
                        if (uriString != null) {
                            profilePictureUri = Uri.parse(uriString);
                            profileDataManager.saveProfileImageUri(profilePictureUri);
                            updateProfilePicture();
                        }
                    }
                }
            });

    /**
     * Initializes the fragment, sets up the profile data manager, and checks if the user is an admin.
     *
     * @param savedInstanceState the saved state of the fragment, if any.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
        String accountType = profileDataManager.getAccountType();
        isAdmin = accountType.equals("ADMINISTRATOR");
        isContributor = accountType.equals("CONTRIBUTOR");
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater the LayoutInflater object to inflate the layout.
     * @param container the container in which the fragment will be placed.
     * @param savedInstanceState the saved state of the fragment, if any.
     * @return the inflated view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    /**
     * Sets up the profile information and handles UI interactions for logout and profile picture updates.
     *
     * @param view the root view of the fragment.
     * @param savedInstancesState the saved state of the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState) {
        super.onViewCreated(view, savedInstancesState);

        setupMenuItems();

        profilePicture = view.findViewById(R.id.ivProfilePic);
        username = view.findViewById(R.id.tvName);
        weight = view.findViewById(R.id.tvWeight);
        height = view.findViewById(R.id.tvHeight);

        updateUserInfo();

        logout = view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sessionToken = "0:0:" + profileDataManager.getId();
                String requestUrl = AppConstants.SERVER_URL + "/logout";
                StringRequest logoutRequest = new StringRequest(
                        Request.Method.PUT,
                        requestUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

                                if (response.equals("Logout successful")) {
                                    Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                                    profileDataManager.clearUserData();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley Error", "Logout failed: " + error.getMessage());
                                Toast.makeText(getActivity(), "Logout failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {
                    @Override
                    public byte[] getBody() {
                        return sessionToken.getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "text/plain; charset=utf-8";
                    }
                };
                VolleySingleton.getInstance(getActivity()).addToRequestQueue(logoutRequest);
            }
        });

        // Load saved profile picture if it exists
        Uri savedUri = profileDataManager.getProfileImageUri();
        if (savedUri != null) {
            profilePictureUri = savedUri;
            updateProfilePicture();
        } else if (getContext() != null) {
            // Load default image if no saved profile picture
            Glide.with(getContext())
                    .load(R.drawable.circular_image_background)
                    .circleCrop()
                    .into(profilePicture);
        }

        profilePicture.setOnClickListener(profilePictureView -> {
            if (getActivity() != null) {
                Intent updateProfileIntent = new Intent(getActivity(), UpdateProfilePictureActivity.class);
                if (profilePictureUri != null) {
                    updateProfileIntent.putExtra("currentPictureUri", profilePictureUri.toString());
                }
                updateProfilePictureLauncher.launch(updateProfileIntent);
            }
        });
    }

    /**
     * Updates the user's profile information, including full name, weight, and height.
     */
    private void updateUserInfo() {
        String fullName = profileDataManager.getFirstname() + " " + profileDataManager.getLastname();
        username.setText(fullName);

        int weightVal = profileDataManager.getWeight();
        if (weightVal > -1)
            weight.setText(String.format("Height\n%d lb", weightVal));

        int heightVal = profileDataManager.getHeight();
        if (heightVal > -1)
            height.setText(String.format("Weight\n%d ft", heightVal));
    }

    /**
     * Sets up the menu items in the profile section, including personal info, security settings, and notifications.
     * If the user is an admin, the admin panel option is also added.
     */
    private void setupMenuItems() {
        setupMenuItem(R.id.personalInfo, R.drawable.profile_icon, "Personal Information");
        setupMenuItem(R.id.passwordSecurity, R.drawable.security_icon, "Password & Security");
        setupMenuItem(R.id.notifications, R.drawable.notifications_icon, "Notifications");
        if (isAdmin)
            setupMenuItem(R.id.adminPanel, R.drawable.menu_icon, "Admin Panel");
        else if(isContributor)
            setupMenuItem(R.id.adminPanel, R.drawable.menu_icon, "Contributor Panel");
    }

    /**
     * Sets up an individual menu item, including icon, title, and click event.
     *
     * @param itemId the ID of the menu item.
     * @param iconResId the resource ID of the icon.
     * @param title the title of the menu item.
     */
    private void setupMenuItem(int itemId, int iconResId, String title) {
        View item = rootView.findViewById(itemId);
        ImageView icon = item.findViewById(R.id.icon);
        ImageView rightArrow = item.findViewById(R.id.rightArrow);
        TextView titleView = item.findViewById(R.id.title);

        icon.setImageResource(iconResId);
        titleView.setText(title);
        if (itemId == R.id.adminPanel) {
            item.setVisibility(View.VISIBLE);
        }

        item.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_animation));
            titleView.setTextColor(getResources().getColor(R.color.Iowa_State_Red));
            icon.setColorFilter(getResources().getColor(R.color.Iowa_State_Red));
            rightArrow.setColorFilter(getResources().getColor(R.color.Iowa_State_Red));

            if (itemId == R.id.personalInfo) {
                navigateToSubFragment(new PersonalInfoFragment());
            } else if (itemId == R.id.passwordSecurity) {
                navigateToSubFragment(new PasswordAndSecurityFragment());
            } else if (itemId == R.id.notifications) {
                navigateToSubFragment(new NotificationsFragment());
            } else if (itemId == R.id.adminPanel) {
                navigateToSubFragment(new AdminPanelFragment());
            } else {
                Log.e("Navigation Error", "Could not navigate from profile page!");
            }
        });
    }

    /**
     * Navigates to a sub-fragment.
     *
     * @param fragment the fragment to navigate to.
     */
    private void navigateToSubFragment(Fragment fragment) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).loadFragment(fragment, true);  // True to add to back stack
        }
    }

    /**
     * Updates the profile picture displayed in the profile fragment.
     */
    private void updateProfilePicture() {
        if (getContext() == null) return;

        Glide.with(getContext())
                .load(profilePictureUri)
                .placeholder(R.drawable.circular_image_background)
                .error(R.drawable.circular_image_background)
                .circleCrop()
                .into(profilePicture);
    }
}
