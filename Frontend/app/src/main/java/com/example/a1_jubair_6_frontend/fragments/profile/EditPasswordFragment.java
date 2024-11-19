package com.example.a1_jubair_6_frontend.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;

public class EditPasswordFragment extends Fragment {

    EditText oldPassword;
    EditText newPassword;
    EditText newPasswordConfirm;
    Button savePassword;
    TextView passError;
    TextView invalidPassError;
    ImageView goBack;



    ProfileDataManager profileDataManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        oldPassword = view.findViewById(R.id.oldPasswordText);
        newPassword = view.findViewById(R.id.newPasswordText);
        newPasswordConfirm = view.findViewById(R.id.newPasswordTextConfirm);
        savePassword = view.findViewById(R.id.btnSave);
        goBack = view.findViewById(R.id.backArrow);

        passError = view.findViewById(R.id.tvPasswordMismatch);
        invalidPassError = view.findViewById(R.id.tvInvalidPassword);

        goBack.setOnClickListener(c -> goBack());

        savePassword.setOnClickListener(click -> {
            passError.setVisibility(View.GONE);
            invalidPassError.setVisibility(View.GONE);

            String newPasswordText = newPassword.getText().toString();
            String newPasswordConfirmText = newPasswordConfirm.getText().toString();

            if(!newPasswordText.equals(newPasswordConfirmText)){
                passError.setVisibility(View.VISIBLE);
            }
            else if(!profileDataManager.getPassword().equals(oldPassword.getText().toString())){
                invalidPassError.setVisibility(View.VISIBLE);
            }
            else{
                profileDataManager.setPassword(newPasswordText);
                profileDataManager.updateUserToServer();
                goBack();
            }
        });

    }

    public void goBack(){
        Fragment passwordAndSecurityFragment = new PasswordAndSecurityFragment();

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_right,
                        R.anim.slide_in_left,
                        R.anim.slide_out_left)
                .replace(R.id.container, passwordAndSecurityFragment)
                .addToBackStack(null)
                .commit();
    }
}