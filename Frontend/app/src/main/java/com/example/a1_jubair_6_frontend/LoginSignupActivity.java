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

public class LoginSignupActivity extends AppCompatActivity {
    //TODO when we have the actual server change this url
    private final String url = "coms-3090-009.class.las.iastate.edu";

    private EditText emailText;
    private EditText passwordText;

    // Variables to store email and password
    private String email;
    private String password;

    final boolean[] isSuccess = {false};

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
            isSuccess[0] = true;

            //Go to home page if successful
            if(isSuccess[0]){
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
}
