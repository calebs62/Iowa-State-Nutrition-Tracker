package com.example.a1_jubair_6_frontend.fragments.profile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;

public class PersonalInfoFragment extends Fragment {

    ProfileDataManager profileDataManager;

    TextView email;
    TextView weight;
    TextView height;

    Button editEmail;
    Button editWeight;
    Button editHeight;
    Button deleteAccount;

    ImageView backArrow;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstancesState){
        super.onViewCreated(view, savedInstancesState);

        backArrow = view.findViewById(R.id.backArrow);

        email = view.findViewById(R.id.emailText);
        weight = view.findViewById(R.id.weightText);
        height = view.findViewById(R.id.heightText);

        editEmail = view.findViewById(R.id.btnEditEmail);
        editWeight = view.findViewById(R.id.btnEditWeight);
        editHeight = view.findViewById(R.id.btnEditHeight);
        deleteAccount = view.findViewById(R.id.btnDeleteAccount);

        refreshPersonalInfoValues();
        setupEditButtons();

        backArrow.setOnClickListener(v -> goBack());
    }

    private void setupEditButtons(){
        editEmail.setOnClickListener(v -> showEditDialog("Email", "Enter new email", InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
        editWeight.setOnClickListener(v -> showEditDialog("Weight", "Enter weight in lbs", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
        editHeight.setOnClickListener(v -> showEditDialog("Height", "Enter height in ft", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));
    }

    private void showEditDialog(String title, String hint, int inputType) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        EditText editTextField = dialog.findViewById(R.id.editTextField);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        dialogTitle.setText(String.format("Edit %s", title));
        editTextField.setHint(hint);
        editTextField.setInputType(inputType);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newValue = editTextField.getText().toString();
            if (!newValue.isEmpty()) {
                saveNewValue(title, newValue);
                refreshPersonalInfoValues();
                dialog.dismiss();
            } else {
                editTextField.setError("This field cannot be empty");
            }
        });

        dialog.show();
    }

    private void saveNewValue(String field, String value) {
        switch (field) {
            case "Email":
                profileDataManager.setEmail(value);
                // Save email to server
                break;
            case "Weight":
                profileDataManager.setWeight(Integer.parseInt(value));
                // Save weight to server
                break;
            case "Height":
                profileDataManager.setHeight(Integer.parseInt(value));
                // Save height to server
                break;
        }
    }

    public void refreshPersonalInfoValues(){
        int weightVal = profileDataManager.getWeight();
        int heightVal = profileDataManager.getHeight();

        email.setText(String.format("Email - %s", profileDataManager.getEmail()));

        if ((weightVal != -1)) {
            weight.setText(String.format("Weight  %d lbs", weightVal));
        } else {
            weight.setText(R.string.weight_lbs);
        }

        if ((heightVal != -1)) {
            height.setText(String.format("Height  %d ft", heightVal));
        } else {
            height.setText(R.string.height_ft);
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