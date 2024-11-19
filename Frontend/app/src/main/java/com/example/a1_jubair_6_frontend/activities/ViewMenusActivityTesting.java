package com.example.a1_jubair_6_frontend.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.fragments.MenuFragment;

public class ViewMenusActivityTesting extends AppCompatActivity {

    private Button viewMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_menus_item);

        viewMenu = findViewById(R.id.btnViewMenu);
        viewMenu.setOnClickListener(v -> {
            Intent exploreIntent = new Intent(ViewMenusActivityTesting.this, MenuFragment.class);
            exploreIntent.putExtra("menu fragment", MenuFragment.class.getName());
            startActivity(exploreIntent);
            finish();
        });
    }
}
