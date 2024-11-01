package com.example.a1_jubair_6_frontend.activities;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.User;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.example.a1_jubair_6_frontend.network.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ProfileInitialize extends AppCompatActivity {

    private ProfileDataManager profileDataManager;
    private EditText userWeight, userHeight;
    private Button loseWeight, gainWeight, gainMuscle;
    private Button confirm;
    private WebSocketClient webSocketClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        profileDataManager = new ProfileDataManager(this);

        setContentView(R.layout.activity_profile_initialize);

        userWeight = findViewById(R.id.etWeight);
        userHeight = findViewById(R.id.etHeight);

        confirm = findViewById(R.id.btnConfirm);

        confirm.setOnClickListener(v -> {
            calculateBMI();
        });
    }

    private void saveNewValue(String weight, String height) {
        int usrWeight = Integer.parseInt(weight);
        int usrHeight = Integer.parseInt(height);
        profileDataManager.setWeight(usrWeight);
        profileDataManager.setHeight(usrHeight);
        updateWeightToServer(usrWeight);
        updateHeightToServer(usrHeight);
    }

    public void updateWeightToServer(int weight){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("weight", weight);

            String url = AppConstants.SERVER_URL + "/user/update/13";

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

    public void updateHeightToServer(int height){
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("height", height);

            String url = AppConstants.SERVER_URL + "/user/update/13";

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

    public void calculateBMI(){
        if(!isEmpty(userWeight.getText().toString()) && !isEmpty(userHeight.getText().toString())){
            saveNewValue(userWeight.getText().toString(), userHeight.getText().toString());

            Double BMI = (703*(Integer.parseInt(userWeight.getText().toString()))/(Math.pow(Integer.parseInt(userHeight.getText().toString()), 2)));
            TextView BMIValue = findViewById(R.id.tvBMIValue);
            if(BMI < 18.5){BMIValue.setText(String.format("", BMI));}
            else if(18.5 <= BMI && BMI <= 24.9){BMIValue.setText(String.format("%.2f You are at a healthy weight.", BMI));}
            else if(25 <= BMI && BMI <= 29.9){BMIValue.setText(String.format("%.2f You are overweight.", BMI));}
            else if(30 <= BMI && BMI <= 34.9){BMIValue.setText(String.format("%.2f You are obese.", BMI));}
            else if(35 <= BMI && BMI <= 39.9){BMIValue.setText(String.format("%.2f You are severely obese.", BMI));}
            else if(BMI >= 40){BMIValue.setText(String.format("%.2f You are morbidly obese.", BMI));}
            else{
                BMIValue.setText(String.format("Was not able to accurately calculate your BMI"));
            }
        }
    }

    //TODO Get new user information from server and put into profileDataManager
}
