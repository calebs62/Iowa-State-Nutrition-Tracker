package com.example.a1_jubair_6_frontend.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.fragments.HomePageFragment;
import com.example.a1_jubair_6_frontend.managers.EngagementNotificationManager;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginSignupActivity extends AppCompatActivity {
    private EditText emailText;
    private EditText passwordText;
    private TextView loginError;

    private String email;
    private String password;

    private ProfileDataManager profileDataManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        profileDataManager = new ProfileDataManager(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setContentView(R.layout.activity_login_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginError = findViewById(R.id.tvLoginError);

        loadSavedCredentials();

        Button exploreButton = findViewById(R.id.btnExplore);
        exploreButton.setOnClickListener(view -> {
            Log.i("Explore Button", "Explore button was clicked!");
            email = emailText.getText().toString();
            password = passwordText.getText().toString();

            Log.i("Email", email);
            Log.i("Password", password);

            if(email.isEmpty() || password.isEmpty()){
                loginError.setText(R.string.invalid_email_or_password_please_try_again);
                loginError.setVisibility(TextView.VISIBLE);
            }
            else {
                profileDataManager.saveEmailAndPassword(email, password);
                try {
                    getCredentialsFromServer(email, password);
                } catch (JSONException e) {
                    Log.e("JSON Exception", e.getMessage());
                }
            }
        });

        TextView registerView = findViewById(R.id.tvRegister);
        registerView.setOnClickListener(view -> {
             Log.i("Register Button", "Register button clicked!");
             Intent intent = new Intent(LoginSignupActivity.this, RegisterActivity.class);
             startActivity(intent);
        });

        TextView forgotPasswordView = findViewById(R.id.tvForgotPassword);
        forgotPasswordView.setOnClickListener(view -> {
            Log.i("Forgot Password Button", "Forgot password button clicked!");
            Intent intent = new Intent(LoginSignupActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EngagementNotificationManager engagementManager =
                new EngagementNotificationManager(this);
        engagementManager.updateLastAccessTime();
    }

    public void getCredentialsFromServer(String email, String password) throws JSONException {
        String requestUrl = AppConstants.SERVER_URL + "/login";

        JSONObject credentials = new JSONObject();

        int uid = profileDataManager.getId();

        credentials.put("username", email);
        credentials.put("password", password);

        Log.i("Starting Login Request", "Searching for user " + email + " on server [" + requestUrl + "]");
        JsonObjectRequest getCreds = new JsonObjectRequest(
            Request.Method.PUT,
            requestUrl,
            credentials,
            response -> {
                try {
                    int id = response.getInt("uid");
                    String username = response.getString("username");
                    String userPass = response.getString("password");
                    String fname = response.getString("fname");
                    String lname = response.getString("lname");
                    int height = response.getInt("height");
                    int weight = response.getInt("weight");
                    String accountType = response.getString("accounttype");
                    Log.i("User Info", "Logged in user: " + username + ", " + fname + " " + lname);

                    if(response.has("img") && !response.isNull("img") && !response.getString("img").isEmpty()) {
                        String base64Image = response.getString("img");
                        profileDataManager.saveBase64Image(base64Image);
                    }

                    User user = new User(id, username, userPass, fname, lname, height, weight, User.Account.valueOf(accountType));
                    profileDataManager.saveUserData(user);

                    if(profileDataManager.getWeight() == -1 || profileDataManager.getHeight() == -1){
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent exploreIntent = new Intent(LoginSignupActivity.this, ProfileInitializeActivity.class);
                        exploreIntent.putExtra("id", profileDataManager.getId());
                        startActivity(exploreIntent);
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent exploreIntent = new Intent(LoginSignupActivity.this, BaseActivity.class);
                        exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
                        startActivity(exploreIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                    loginError.setText(R.string.unexpected_error_occurred);
                    loginError.setVisibility(TextView.VISIBLE);
                }
        }, error -> {
            String errorMessage = error.getMessage();
            if(errorMessage == null){
                errorMessage = "An unknown error occurred";
            }
            Log.e("Login Error", errorMessage);
            loginError.setText(R.string.invalid_email_or_password_please_try_again);
            loginError.setVisibility(TextView.VISIBLE);
        });

        VolleySingleton.getInstance(this).addToRequestQueue(getCreds);
    }

    private void loadSavedCredentials() {
        String savedEmail = profileDataManager.getEmail();
        String savedPassword = profileDataManager.getPassword();

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            emailText.setText(savedEmail);
            passwordText.setText(savedPassword);
        }
    }
}
