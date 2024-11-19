package com.example.a1_jubair_6_frontend.activities;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.a1_jubair_6_frontend.fragments.GoalsFragment;
import com.example.a1_jubair_6_frontend.fragments.HomePageFragment;
import com.example.a1_jubair_6_frontend.fragments.MenuFragment;
import com.example.a1_jubair_6_frontend.fragments.profile.ProfileFragment;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.fragments.TrackerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    public static final String EXTRA_INITIAL_FRAGMENT = "initial_fragment";
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

        if (savedInstanceState == null) {
            Fragment initialFragment = getInitialFragmentFromIntent();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, initialFragment)
                    .commit();
        }
    }

    private Fragment getInitialFragmentFromIntent() {
        String fragmentName = getIntent().getStringExtra(EXTRA_INITIAL_FRAGMENT);
        if(fragmentName != null) {
            if(fragmentName.equals(HomePageFragment.class.getName())){
                return new HomePageFragment();
            }
            else if(fragmentName.equals(ProfileFragment.class.getName())){
                return new ProfileFragment();
            }
        }
        return getInitialFragment();
    }

    protected Fragment getInitialFragment() {
        return new HomePageFragment();
    }

    protected int getSelectedNavItem() {
        return R.id.nav_home;
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

            if (itemId == R.id.nav_home) {
                if (!(currentFragment instanceof HomePageFragment)) {
                    selectedFragment = new HomePageFragment();
                    loadFragment(selectedFragment, false);
                }
            } else if (itemId == R.id.nav_menus) {
                if (!(currentFragment instanceof MenuFragment)) {
                    selectedFragment = new MenuFragment();
                    loadFragment(selectedFragment, false);
                }
            } else if (itemId == R.id.nav_tracker) {
                if (!(currentFragment instanceof TrackerFragment)) {
                    selectedFragment = new TrackerFragment();
                    loadFragment(selectedFragment, false);
                }
            } else if (itemId == R.id.nav_goals) {
                if (!(currentFragment instanceof GoalsFragment)) {
                    selectedFragment = new GoalsFragment();
                    loadFragment(selectedFragment, false);
                }
            } else if (itemId == R.id.nav_profile) {
                if (!(currentFragment instanceof ProfileFragment)) {
                    selectedFragment = new ProfileFragment();
                    loadFragment(selectedFragment, false);
                }
            } else {
                return false;
            }

            return true;
        });
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_right,
                R.anim.slide_in_left,
                R.anim.slide_out_left
        );

        fragmentTransaction.replace(R.id.container, fragment);
        if(addToBackStack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}
