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


public class PasswordAndSecurityFragment extends Fragment {

    View rootview;
    ImageView goBack;
    TextView passwordText;

    ProfileDataManager profileDataManager;

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

        goBack = view.findViewById(R.id.backArrow2);
        goBack.setOnClickListener(c -> goBack());

        StringBuilder hiddenPassword = new StringBuilder();

        for (int i = 0; i < profileDataManager.getPassword().length(); i++){
            hiddenPassword.append("•");
        }

        passwordText = view.findViewById(R.id.tvPasswordHidden);
        passwordText.setText(String.format("Password: %s", hiddenPassword));

        setupMenuItem(R.id.editPassword);
    }

    private void setupMenuItem(int itemId) {
        View item = rootview.findViewById(itemId);
        ImageView icon = item.findViewById(R.id.icon);
        ImageView rightArrow = item.findViewById(R.id.rightArrow);
        TextView titleView = item.findViewById(R.id.title);

        titleView.setText(R.string.edit_password);

        item.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_animation));
            titleView.setTextColor(getResources().getColor(R.color.Iowa_State_Red));
            icon.setColorFilter(getResources().getColor(R.color.Iowa_State_Red));
            rightArrow.setColorFilter(getResources().getColor(R.color.Iowa_State_Red));

            if (itemId == R.id.editPassword) {
                navigateToSubFragment(new EditPasswordFragment());
            }
            else {
                Log.e("Navigation Error", "Could not navigate from password & security page!");
            }
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
                .replace(R.id.container, profileFragment)
                .addToBackStack(null)
                .commit();
    }
}