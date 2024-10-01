package com.example.a1_jubair_6_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginSignupActivity extends AppCompatActivity {
    //TODO when we have the actual server change this url
    private final String url = "coms-3090-009.class.las.iastate.edu";
    private final String postmanUrl = "https://a382bcf9-c472-4f17-95f4-094f6ab49a61.mock.pstmn.io";

    private EditText emailText;
    private EditText passwordText;

    // Variables to store email and password
    private String email;
    private String password;

    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        Button exploreButton = findViewById(R.id.btnExplore);
        exploreButton.setOnClickListener(view -> {
            Log.i("Explore Button", "Explore button was clicked!");
            email = emailText.getText().toString();
            password = passwordText.getText().toString();

            Log.i("Email", email);
            Log.i("Password", password);

            //TODO send a GET request out to backend to see if email and password exists otherwise throw error or refuse signin

            ///For now until endpoint is setup, go to home page
            getCredentialsFromServer(email, password);

            //Go to home page if successful
            if(isSuccess){
                Intent exploreIntent = new Intent(LoginSignupActivity.this, BaseActivity.class);
                exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
                startActivity(exploreIntent);
                finish();
            }
            Log.e("Login Error","Account did not exist!");
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
        String requestUrl = postmanUrl + "/getCreds";
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
            Log.i("Login", "Login Sucessful");
            isSuccess = true;
        }, error -> {
            Log.e("Login Error", "Could not find account!");
            isSuccess = false;
        });

        VolleySingleton.getInstance(this).addToRequestQueue(getCreds);
    }
}
