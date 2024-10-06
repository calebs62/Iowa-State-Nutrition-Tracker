package com.example.a1_jubair_6_frontend.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.BaseActivity;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.google.android.material.button.MaterialButton;


public class PasswordAndSecurityFragment extends Fragment {

    private View rootview;
    private ImageView goBack;
    private TextView passwordText;
    private MaterialButton btnChangePassword;
    private MaterialButton btnSetup2FA;
    private ProfileDataManager profileDataManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_password_and_security, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState) {
        super.onViewCreated(view, savedInstancesState);

        initializeViews(view);
        setupClickListeners();
        displayPassword();
    }

    private void initializeViews(View view) {
        goBack = view.findViewById(R.id.backArrow);
        passwordText = view.findViewById(R.id.tvPasswordHidden);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSetup2FA = view.findViewById(R.id.btnSetup2FA);
    }

    private void displayPassword() {
        String password = profileDataManager.getPassword();

        StringBuilder hiddenPassword = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            hiddenPassword.append("•");
        }
        passwordText.setText(hiddenPassword.toString());
    }

    private void setupClickListeners() {
        goBack.setOnClickListener(v -> goBack());

        btnChangePassword.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_animation));
            navigateToSubFragment(new EditPasswordFragment());
        });

        btnSetup2FA.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_animation));
            // TODO: Implement 2FA setup navigation
        });
    }


    private void navigateToSubFragment(Fragment fragment){
        if(getActivity() instanceof BaseActivity){
            ((BaseActivity) getActivity()).loadFragment(fragment, true);  // True to add to back stack
        }
    }

    public void goBack(){
        Fragment profileFragment = new ProfileFragment();

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_left)
                .replace(R.id.container, profileFragment)
                .addToBackStack(null)
                .commit();
    }
}