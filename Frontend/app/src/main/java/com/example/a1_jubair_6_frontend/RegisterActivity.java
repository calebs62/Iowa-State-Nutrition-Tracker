package com.example.a1_jubair_6_frontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private String url = "coms-3090-009.class.las.iastate.edu";
    private final boolean[] isSuccess = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("RegisterActivity", "onCreate called");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button registerButton = findViewById(R.id.btnRegister);
        EditText emailView = findViewById(R.id.emailText);
        EditText password = findViewById(R.id.registerPasswordText);
        EditText confirmPassword = findViewById(R.id.registerPasswordConfirmText);

        registerButton.setOnClickListener(view -> {
            String email = emailView.getText().toString();
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if(!pass.equals(confirmPass)){
                //Creates an error dialog when the passwords entered do not match
                showPasswordMatchError();
            }
            else {
                //TODO call a method here to save account data to database and switch the view to the main page
                Log.i("New Registration","Got email: [" + email + "] Password: [" + pass + "], posting to server");
                postCredentialsToServer(email, pass);

                if(isSuccess[0]){
                    Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(RegisterActivity.this, HomePageActivity.class);
                    startActivity(homeIntent);
                }
                //If signup is not successful, create an error message
                showSignupError();
            }
        });

        TextView goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(view -> {
            Log.i("Go Back", "User clicked Go Back button, Navigating back to Login and Signup");
            Intent intent = new Intent(RegisterActivity.this, LoginSignupActivity.class);
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

        TextView confirmPassView = alert.findViewById(R.id.registerPasswordConfirm);
        if(confirmPassView != null)
            confirmPassView.setTextColor(Color.RED);
    }

    private void showSignupError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Could not get response from server for signup!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

        TextView confirmPassView = alert.findViewById(R.id.registerPasswordConfirm);
        if(confirmPassView != null)
            confirmPassView.setTextColor(Color.RED);
    }

    public void postCredentialsToServer(String email, String password) {
        //TODO change endpoint to what it is on server
        String requestUrl = url + "/signup";
        JSONObject credentialsObject = new JSONObject();

        try{
            credentialsObject.put("email", email);
            credentialsObject.put("password", password);
        }
        catch(JSONException ex){
            Log.e("JSONException", Objects.requireNonNull(ex.getMessage()));
        }

        JsonObjectRequest credentialsPostRequest = new JsonObjectRequest(
                Request.Method.POST,
                requestUrl,
                credentialsObject,
                response -> {
                    isSuccess[0] = true;
                    Log.i("VolleyResponse", "Response: " + response);
                },
                error -> {
                    isSuccess[0] = false;
                    Log.e("VolleyError", "Error: " + error);
                }
        );
        // Adding request to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(credentialsPostRequest);
    }
}