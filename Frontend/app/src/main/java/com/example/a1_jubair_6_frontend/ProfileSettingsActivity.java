package com.example.a1_jubair_6_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileSettingsActivity extends AppCompatActivity {

    Button profileBtn;
    Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileBtn = findViewById(R.id.profileHomeBtn);
        logoutBtn = findViewById(R.id.btnLogout);


        profileBtn.setOnClickListener(view -> {
            Intent homeIntent = new Intent(ProfileSettingsActivity.this, HomePageActivity.class);
            startActivity(homeIntent);
        });

        logoutBtn.setOnClickListener(view -> {
            //TODO make it so that it clears the credentials from the Shared Preferences
            Intent logoutIntent = new Intent(ProfileSettingsActivity.this, LoginSignupActivity.class);
            startActivity(logoutIntent);
        });


    }
}