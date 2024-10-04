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

import com.bumptech.glide.Glide;
import com.example.a1_jubair_6_frontend.activities.LoginSignupActivity;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.BaseActivity;
import com.example.a1_jubair_6_frontend.activities.UpdateProfilePictureActivity;

public class ProfileFragment extends Fragment {

    private View rootView;
    ImageView profilePicture;
    TextView username;
    Button logout;
    TextView weight;
    TextView height;
    private Uri profilePictureUri;
    private ProfileDataManager profileDataManager;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

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
                profileDataManager.clearUserData();

                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                // Clear the back stack to prevent the user from going back to the profile screen
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

    private void updateUserInfo() {
        String fullName = profileDataManager.getFirstname() + " " + profileDataManager.getLastname();
        username.setText(fullName);

        int weightVal = profileDataManager.getWeight();
        if(weightVal > -1)
            weight.setText(String.format("Height\n%d lb", weightVal));

        int heightVal = profileDataManager.getHeight();
        if(heightVal > -1)
            height.setText(String.format("Weight\n%d ft", heightVal));
    }

    private void setupMenuItems(){
        setupMenuItem(R.id.personalInfo, R.drawable.profile_icon, "Personal Information");
        setupMenuItem(R.id.passwordSecurity, R.drawable.security_icon, "Password & Security");
        setupMenuItem(R.id.notifications, R.drawable.notifications_icon, "Notifications");
    }

    private void setupMenuItem(int itemId, int iconResId, String title) {
        View item = rootView.findViewById(itemId);
        ImageView icon = item.findViewById(R.id.icon);
        ImageView rightArrow = item.findViewById(R.id.rightArrow);
        TextView titleView = item.findViewById(R.id.title);

        icon.setImageResource(iconResId);
        titleView.setText(title);

        item.setOnClickListener(v -> {
            //TODO make it so that it goes to
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_animation));
            titleView.setTextColor(getResources().getColor(R.color.Iowa_State_Red));
            icon.setColorFilter(getResources().getColor(R.color.Iowa_State_Red));
            rightArrow.setColorFilter(getResources().getColor(R.color.Iowa_State_Red));

            if(itemId == R.id.personalInfo){
                navigateToSubFragment(new PersonalInfoFragment());
            }
            else if(itemId == R.id.passwordSecurity){
                navigateToSubFragment(new PasswordAndSecurityFragment());
            }
            else if(itemId == R.id.notifications){
                navigateToSubFragment(new NotificationsFragment());
            }
            else{
                Log.e("Navigation Error", "Could not navigate from profile page!");
            }
        });
    }

    private void navigateToSubFragment(Fragment fragment){
        if(getActivity() instanceof BaseActivity){
            ((BaseActivity) getActivity()).loadFragment(fragment, true);  // True to add to back stack
        }
    }

    private void updateProfilePicture() {
        if (profilePictureUri != null && getContext() != null) {
            Glide.with(getContext())
                    .load(profilePictureUri)
                    .circleCrop()
                    .into(profilePicture);
        }
    }
}