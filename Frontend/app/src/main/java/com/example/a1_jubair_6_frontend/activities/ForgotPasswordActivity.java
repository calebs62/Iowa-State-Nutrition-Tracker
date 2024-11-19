package com.example.a1_jubair_6_frontend.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private User user;
    private ProfileDataManager profileDataManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("ForgotPasswordActivity", "onCreate called");

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgot_password_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button submitButton = findViewById(R.id.btnSubmit);
        EditText emailView = findViewById(R.id.emailText);
        EditText password = findViewById(R.id.passText);
        EditText confirmPassword = findViewById(R.id.confText);

        submitButton.setOnClickListener(view -> {

            String email = emailView.getText().toString();
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();
            int uid = 2;

            if(email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()){
                Log.e("Form Empty Error", "One or more of the register forms were empty!");
                showFormEmpty();
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Log.e("Invalid Email Error", "Email is not a valid email!");
                showEmailFormatError(email);
                return;
            }

            if(pass.length() < 6){
                Log.e("Password Length Error", "Password was not at least 6 characters!");
                showPasswordLengthError();
                return;
            }

            if(!pass.equals(confirmPass)){
                showPasswordMatchError();
            }
            else {
                Log.i("New Password","Got email: [" + email + "] Password: [" + pass + "], posting to server");

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("username", email);
                    jsonBody.put("password", pass);
                    jsonBody.put("newPassword", confirmPass);

                } catch (JSONException e) {
                    Log.e("JSON Exception", e.getMessage());
                }

                String url = AppConstants.SERVER_URL + "/password";

                JsonObjectRequest forgotPasswordRequest = new JsonObjectRequest(
                        Request.Method.PUT,
                        url,
                        jsonBody,
                        response -> {
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginSignupActivity.class);
                            startActivity(intent);
                        },
                        error -> {
                            Log.e("Volley Error", "Error occurred during password reset: " + error.getMessage());
                            Toast.makeText(ForgotPasswordActivity.this, "Error resetting password. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                );

                VolleySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(forgotPasswordRequest);
            }
        });

        TextView goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(view -> {
            Log.i("Go Back", "User clicked Go Back button, Navigating back to Login and Signup");
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginSignupActivity.class);
            startActivity(intent);
        });
    }

    private void showPasswordMatchError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Passwords do not match!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

        TextView confirmPassView = alert.findViewById(R.id.confPass);
        if(confirmPassView != null)
            confirmPassView.setTextColor(Color.RED);
    }

    private void showEmailFormatError(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(email + " is not a valid email!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

        TextView confirmEmailView = alert.findViewById(R.id.useEmail);
        if(confirmEmailView != null)
            confirmEmailView.setTextColor(Color.RED);
    }

    private void showPasswordLengthError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Password needs to be at least 6 characters long!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

        TextView confirmPassView = alert.findViewById(R.id.confPass);
        if(confirmPassView != null)
            confirmPassView.setTextColor(Color.RED);
    }

    private void showFormEmpty() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Passwords do not match!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

        TextView confirmPassView = alert.findViewById(R.id.useEmail);
        if(confirmPassView != null)
            confirmPassView.setTextColor(Color.RED);
    }
}