package com.example.a1_jubair_6_frontend.fragments.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.activities.LoginSignupActivity;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PersonalInfoFragment extends Fragment {

    private static final String TAG = "PersonalInfoFragment";
    ProfileDataManager profileDataManager;

    TextView email;
    TextView phoneNumber;
    TextView weight;
    TextView height;

    Button editPhoneNumber;
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
        phoneNumber = view.findViewById(R.id.phoneNumberText);
        weight = view.findViewById(R.id.weightText);
        height = view.findViewById(R.id.heightText);

        editPhoneNumber = view.findViewById(R.id.btnEditPhoneNumber);
        editWeight = view.findViewById(R.id.btnEditWeight);
        editHeight = view.findViewById(R.id.btnEditHeight);
        deleteAccount = view.findViewById(R.id.btnDeleteAccount);

        deleteAccount.setOnClickListener(v -> {
            showDeleteAccountDialog();
        });

        refreshPersonalInfoValues();
        setupEditButtons();

        backArrow.setOnClickListener(v -> goBack());
    }

    private void setupEditButtons(){
        editPhoneNumber.setOnClickListener(v -> showEditDialog("Phone Number", "Enter phone number", InputType.TYPE_CLASS_PHONE));
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

        if (title.equals("Phone Number")) {
            editTextField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String newValue = editTextField.getText().toString();
            if (newValue.isEmpty()) {
                editTextField.setError("This field cannot be empty");
            } else if (title.equals("Phone Number") && !isValidPhoneNumber(newValue)) {
                editTextField.setError("Please enter a valid phone number");
            } else {
                saveNewValue(title, newValue);
                refreshPersonalInfoValues();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveNewValue(String field, String value) {
        switch (field) {
            case "Phone Number":
                profileDataManager.setPhoneNumber(value);
                // TODO: Update Phone Number to Server
                break;
            case "Weight":
                try {
                    int weightVal = Integer.parseInt(value);
                    profileDataManager.setWeight(weightVal);
                    updateWeightToServer(weightVal);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid number for weight", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Height":
                try {
                    int heightVal = Integer.parseInt(value);
                    profileDataManager.setHeight(heightVal);
                    updateHeightToServer(heightVal);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid number for height", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void refreshPersonalInfoValues(){
        int weightVal = profileDataManager.getWeight();
        int heightVal = profileDataManager.getHeight();

        email.setText(String.format("%s", profileDataManager.getEmail()));

        if (profileDataManager.getPhoneNumber() != null)
            phoneNumber.setText(String.format("%s", profileDataManager.getPhoneNumber()));
        else
            phoneNumber.setText(" -- ");

        if ((weightVal != -1)) {
            weight.setText(String.format("%d lbs", weightVal));
        } else {
            weight.setText(R.string.weight_lbs);
        }

        if ((heightVal != -1)) {
            height.setText(String.format("%d ft", heightVal));
        } else {
            height.setText(R.string.height_ft);
        }
    }

    public void showDeleteAccountDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_user_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);

        EditText editTextField = dialog.findViewById(R.id.editTextField);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        editTextField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String emailValue = editTextField.getText().toString();
            if (emailValue.isEmpty()) {
                editTextField.setError("This field cannot be empty");
            }
            else if(!emailValue.equals(profileDataManager.getEmail())){
                editTextField.setError("Entered email does not match account email");
            }
            else{
                deleteUserFromServer();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void  deleteUserFromServer(){
        String url = AppConstants.SERVER_URL + "/deleteUser/" + profileDataManager.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            response -> {
                profileDataManager.clearUserData();
                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                // Clear the back stack to prevent the user from going back to the personal info screen
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            },
            error -> {
                Toast.makeText(getContext(), "Error deleting account, please try again", Toast.LENGTH_SHORT).show();
                Log.e(TAG, error.toString());
        });
    }

    public void updateWeightToServer(int weight){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("weight", weight);

            String url = AppConstants.SERVER_URL + "/user/update/" + profileDataManager.getId();

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    response -> {
                        Toast.makeText(getContext(), "Weight updated successfully", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e(TAG, "Upload error: " + error.toString());
                        Toast.makeText(getContext(), "Failed to update weight", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
        } catch (Exception e) {
            Log.e(TAG, "Error preparing upload: " + e.getMessage());
            Toast.makeText(getContext(), "Error preparing weight upload", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateHeightToServer(int height){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("height", height);

            String url = AppConstants.SERVER_URL + "/user/update/" + profileDataManager.getId();

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    response -> {
                        Toast.makeText(getContext(), "Height updated successfully", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e(TAG, "Upload error: " + error.toString());
                        Toast.makeText(getContext(), "Failed to update height", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
        } catch (Exception e) {
            Log.e(TAG, "Error preparing upload: " + e.getMessage());
            Toast.makeText(getContext(), "Error preparing height upload", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String digits = phoneNumber.replaceAll("[^0-9]", "");

        return digits.length() == 10;
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