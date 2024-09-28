package com.example.a1_jubair_6_frontend;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_checked}
                },
                new int[]{
                        getColor(R.color.Iowa_State_Red),
                        getColor(R.color.black)
                }
        );

        bottomNavigationView.setItemIconTintList(colorStateList);
        bottomNavigationView.setItemTextColor(colorStateList);

        setupBottomNavigation();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, getInitialFragment())
                .commit();
    }

    // Sets Home Page as starting view
    protected Fragment getInitialFragment() {
        return new HomePageFragment();
    }

    private void setupBottomNavigation() {
        // Your color state list code...
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home){
                selectedFragment = new HomePageFragment();
            }
            else if(item.getItemId() == R.id.nav_menus){
                selectedFragment = new MenuFragment();
            }
            else if(item.getItemId() == R.id.nav_tracker){
                selectedFragment = new TrackerFragment();
            }
            else if((item.getItemId() == R.id.nav_notifications)){
                selectedFragment = new NotificationsFragment();
            }
            else if(item.getItemId() == R.id.nav_profile){
                selectedFragment = new ProfileFragment();
            }
            else{
                return false;
            }
            loadFragment(selectedFragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}
