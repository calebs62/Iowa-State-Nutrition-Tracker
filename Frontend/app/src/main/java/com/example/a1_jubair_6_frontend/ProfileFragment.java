package com.example.a1_jubair_6_frontend;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

public class ProfileFragment extends Fragment {

    private float weight = 0f;
    private float height = 0;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logout = rootView.findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                // Clear the back stack to prevent the user from going back to the profile screen
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        updateUserInfo();
        setupMenuItems();


    }

    private void updateUserInfo() {
        //TODO make a request to server to get weight, height, and profile picture, then change the values
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
        if(getActivity() instanceof  BaseActivity){
            ((BaseActivity) getActivity()).loadFragment(fragment, true);  // True to add to back stack
        }
    }
}