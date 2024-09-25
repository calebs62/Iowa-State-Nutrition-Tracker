package com.example.a1_jubair_6_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoginSignupActivity extends AppCompatActivity {
    //TODO when we have the actual server change this url
    private String url = "https://1e19cd18-4bd8-48bd-8f86-4538939ca9d8.mock.pstmn.io/userLogin";

    private Spinner spMethod;
    private EditText etUrl;
    private EditText etRequest;
    private TextView tvResponse;
    private Button btnSend;

    private String method;
    private String requestBody;
    private String responseBody;


    private EditText emailText;
    private EditText passwordText;

    // Variables to store email and password
    private String email;
    private String password;

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
            Log.i("Explore Button", "Exlore button was clicked!");
            email = emailText.getText().toString();
            password = passwordText.getText().toString();

            Log.i("Email", email);
            Log.i("Password", password);

            //TODO send a request out to backend to see if email and password exists otherwise throw error
            boolean isSuccess = postCredentialsToServer(email, password);

            //Go to home page if successful
            if(isSuccess){
                Intent exploreIntent = new Intent(LoginSignupActivity.this, HomePageActivity.class);
                startActivity(exploreIntent);
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

    public boolean postCredentialsToServer(String email, String password){
        // Convert input to JSONObject
        JSONObject postBody = null;
        String urlQuery = "?email=" + email + "&password=" + password;

        String requestUrl = url + urlQuery;

        try{
            // etRequest should contain a JSON object string as your POST body
            // similar to what you would have in POSTMAN-body field
            // and the fields should match with the object structure of @RequestBody on sb
            postBody = new JSONObject();

            postBody.put("email", email);
            postBody.put("password", password);
        } catch (Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                postBody,
                response -> {
                    Log.i("Response", response.toString());
                },
                error -> {
                    Log.e("Error", error.toString());
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        //TODO if request is not valid, return false, otherwise return true
        return true;
    }
}
