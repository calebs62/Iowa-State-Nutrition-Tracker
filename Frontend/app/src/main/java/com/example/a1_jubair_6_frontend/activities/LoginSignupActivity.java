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
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.fragments.HomePageFragment;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginSignupActivity extends AppCompatActivity {
    private EditText emailText;
    private EditText passwordText;
    private TextView loginError;
    private CheckBox saveLogin;

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
        saveLogin = findViewById(R.id.saveLoginCheckBox);

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
            else
                if(saveLogin.isChecked()){
                    profileDataManager.saveEmailAndPassword(email, password);
                }
                getCredentialsFromServer(email, password);
        });

        TextView registerView = findViewById(R.id.tvRegister);
        registerView.setOnClickListener(view -> {
             Log.i("Register Button", "Register button clicked!");
             Intent intent = new Intent(LoginSignupActivity.this, RegisterActivity.class);
             startActivity(intent);
        });
    }

    public void getCredentialsFromServer(String email, String password){
        //TODO change endpoint to what it is on server
        String requestUrl = AppConstants.ALEX_POSTMAN_URL + "/getCreds";
        String url = requestUrl + "?email=" + email + "&password=" + password;
        JSONObject credentialsObject = new JSONObject();

        try{
            credentialsObject.put("email", email);
            credentialsObject.put("password", password);
        }
        catch(JSONException ex){
            Log.e("JSONException", Objects.requireNonNull(ex.getMessage()));
        }
        StringRequest getCreds = new StringRequest(Request.Method.GET, url,
            response -> {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent exploreIntent = new Intent(LoginSignupActivity.this, BaseActivity.class);
            exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
            startActivity(exploreIntent);
            finish();
        }, error -> {
            Log.e("Login Error", Objects.requireNonNull(error.getMessage()));
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
