package com.example.a1_jubair_6_frontend.activities;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.fragments.HomePageFragment;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity class to initialize and manage the user's profile data, including weight, height,
 * and BMI calculations. It also manages adding the user to a specific group based on the selected
 * goal (e.g., lose weight, gain weight, gain muscle).
 *
 * @author Caleb Sanchez, Alexander Svobodny
 */
public class ProfileInitializeActivity extends AppCompatActivity {

    private ProfileDataManager profileDataManager;
    private EditText userWeight, userHeight;
    private TextView userGreeting, recommendation1, recommendation2, recommendation3;
    private Button loseWeight, gainWeight, gainMuscle;
    private Button confirm;
    private WebSocketClient webSocketClient;
    private int id;
    private String sessionToken;
    private int recommendedPlan = -1;

    /**
     * Called when the activity is created. Initializes the layout, retrieves the session token,
     * and sets up the buttons for user interaction.
     *
     * @param savedInstanceState the saved state of the activity (if any).
     */
    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(this);
        id = getIntent().getIntExtra("id", -1);
        getSessionToken(id);

        setContentView(R.layout.activity_profile_initialize);

        userGreeting = findViewById(R.id.tvGreeting);
        userGreeting.setText(String.format("Hello, " + profileDataManager.getFirstname() + "!"));

        userWeight = findViewById(R.id.etWeight);
        userHeight = findViewById(R.id.etHeight);
        confirm = findViewById(R.id.btnConfirm);


        loseWeight = findViewById(R.id.btnLoseWeight);
        gainWeight = findViewById(R.id.btnGainWeight);
        gainMuscle = findViewById(R.id.btnGainMuscle);

        recommendation1 = findViewById(R.id.tvRecommendation1);
        recommendation2 = findViewById(R.id.tvRecommendation2);
        recommendation3 = findViewById(R.id.tvRecommendation3);

        confirm.setOnClickListener(v -> {
            calculateBMI();
            int color = ContextCompat.getColor(this, R.color.Iowa_State_Gold);
            int def = ContextCompat.getColor(this, R.color.Iowa_State_Red);
            if(recommendedPlan == 0) {
                gainWeight.setBackgroundColor(def);
                gainMuscle.setBackgroundColor(def);
                loseWeight.setBackgroundColor(color);

                recommendation1.setVisibility(v.VISIBLE);
                recommendation2.setVisibility(v.GONE);
                recommendation3.setVisibility(v.GONE);
            }
            else if(recommendedPlan == 1) {
                gainWeight.setBackgroundColor(color);
                gainMuscle.setBackgroundColor(def);
                loseWeight.setBackgroundColor(def);

                recommendation1.setVisibility(v.GONE);
                recommendation2.setVisibility(v.VISIBLE);
                recommendation3.setVisibility(v.GONE);
            }
            else if(recommendedPlan == 2) {
                gainWeight.setBackgroundColor(def);
                gainMuscle.setBackgroundColor(color);
                loseWeight.setBackgroundColor(def);

                recommendation1.setVisibility(v.GONE);
                recommendation2.setVisibility(v.GONE);
                recommendation3.setVisibility(v.VISIBLE);
            }
            else {
                Toast.makeText(this, "Error recommending food plan", Toast.LENGTH_SHORT).show();
            }
        });

        loseWeight.setOnClickListener(v -> {
            addUserToGroup(id, 1);
            Intent exploreIntent = new Intent(ProfileInitializeActivity.this, BaseActivity.class);
            exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
            startActivity(exploreIntent);
            finish();
        });

        gainWeight.setOnClickListener(v -> {
            addUserToGroup(id, 2);
            Intent exploreIntent = new Intent(ProfileInitializeActivity.this, BaseActivity.class);
            exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
            startActivity(exploreIntent);
            finish();
        });

        gainMuscle.setOnClickListener(v -> {
            addUserToGroup(id, 3);
            Intent exploreIntent = new Intent(ProfileInitializeActivity.this, BaseActivity.class);
            exploreIntent.putExtra(BaseActivity.EXTRA_INITIAL_FRAGMENT, HomePageFragment.class.getName());
            startActivity(exploreIntent);
            finish();
        });
    }

    /**
     * Saves the user's weight and height to local storage and sends the data to the server.
     *
     * @param weight the user's weight.
     * @param height the user's height.
     */
    private void saveNewValue(String weight, String height) {
        int usrWeight = Integer.parseInt(weight);
        int usrHeight = Integer.parseInt(height);
        profileDataManager.setWeight(usrWeight);
        profileDataManager.setHeight(usrHeight);
        updateWeightToServer(usrWeight, id);
        updateHeightToServer(usrHeight, id);
    }

    /**
     * Sends a request to the server to update the user's weight.
     *
     * @param weight the new weight of the user.
     * @param id the ID of the user.
     */
    public void updateWeightToServer(int weight, int id){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("weight", weight);

            String url = AppConstants.SERVER_URL + "/user/update/" + id;

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    response -> {
                        Toast.makeText(this, "Weight updated successfully", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(this, "Failed to update weight", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (Exception e) {
            Toast.makeText(this, "Error preparing weight upload", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sends a request to the server to update the user's height.
     *
     * @param height the new height of the user.
     * @param id the ID of the user.
     */
    public void updateHeightToServer(int height, int id){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("height", height);

            String url = AppConstants.SERVER_URL + "/user/update/" + id;

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    requestBody,
                    response -> {
                        Toast.makeText(this, "Height updated successfully", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(this, "Failed to update height", Toast.LENGTH_SHORT).show();
                    }
            ){
                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(request);
        } catch (Exception e) {
            Toast.makeText(this, "Error preparing height upload", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calculates the user's BMI based on the input weight and height.
     * The result is displayed on the screen.
     */
    public void calculateBMI() {
        if (!isEmpty(userWeight.getText().toString()) && !isEmpty(userHeight.getText().toString())) {
            saveNewValue(userWeight.getText().toString(), userHeight.getText().toString());

            Double BMI = (703 * (Integer.parseInt(userWeight.getText().toString())) /
                    (Math.pow(Integer.parseInt(userHeight.getText().toString()), 2)));
            TextView BMIValue = findViewById(R.id.tvBMIValue);

            String formatted = String.format("%.2f", BMI);
            BMI = Double.parseDouble(formatted);
            Log.i("BMI calc", BMI.toString());

            int color;
            String message;
            if (BMI < 18.5) {
                color = getResources().getColor(R.color.blue);
                message = "You are underweight.";
                recommendedPlan = 1;
            } else if (18.5 <= BMI && BMI <= 24.9) {
                color = getResources().getColor(R.color.green);
                message = "You are at a healthy weight.";
                recommendedPlan = 2;
            } else if (25 <= BMI && BMI <= 29.9) {
                color = getResources().getColor(R.color.yellow);
                message = "You are overweight.";
                recommendedPlan = 0;
            } else if (30 <= BMI && BMI <= 34.9) {
                color = getResources().getColor(R.color.orange);
                message = "You are obese.";
                recommendedPlan = 0;
            } else if (35 <= BMI && BMI <= 39.9) {
                color = getResources().getColor(R.color.red);
                message = "You are severely obese.";
                recommendedPlan = 0;
            } else if (BMI >= 40) {
                color = getResources().getColor(R.color.dark_red);
                message = "You are morbidly obese.";
                recommendedPlan = 0;
            } else {
                BMIValue.setText("Was not able to accurately calculate your BMI");
                return;
            }

            String bmiText = String.format("%.2f", BMI);
            String fullMessage = bmiText + " " + message;

            SpannableString spannable = new SpannableString(fullMessage);
            spannable.setSpan(new ForegroundColorSpan(color), 0, bmiText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            BMIValue.setText(spannable);
        }
        else if(isEmpty(userWeight.getText().toString())) {
            Toast.makeText(this, "Weight cannot be empty.", Toast.LENGTH_SHORT).show();
        }
        else if(isEmpty(userHeight.getText().toString())) {
            Toast.makeText(this, "Height cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Retrieves the session token for the current user from the server.
     *
     * @param id the ID of the user.
     */
    public void getSessionToken(int id) {
        String requestUrl = AppConstants.SERVER_URL + "/" + id + "/sessionToken";

        StringRequest joinRequest = new StringRequest(
                Request.Method.GET,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        sessionToken = response.toString();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Session token retrieval failed: " + error.getMessage());
                        Toast.makeText(ProfileInitializeActivity.this, "Session token retrieval failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                return sessionToken.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "text/plain; charset=utf-8";
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(joinRequest);
    }

    /**
     * Adds the user to a specific group based on what the user chose as their goal.
     *
     * @param uid the user ID.
     * @param gid the group ID.
     */
    public void addUserToGroup(int uid, int gid){
        String requestUrl = AppConstants.SERVER_URL + "/group/" + gid + "/join";

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Added Successfully", response);
                        if(response.equals("true")){
                            Toast.makeText(ProfileInitializeActivity.this, "User successfully added to group: " + gid, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getMessage();
                    }
                }
        ) {
            @Override
            public byte[] getBody() { return sessionToken.getBytes(); }

            @Override
            public String getBodyContentType() { return "text/plain; charset=utf-8"; }
        };
        VolleySingleton.getInstance(ProfileInitializeActivity.this).addToRequestQueue(stringRequest);
    }
}
