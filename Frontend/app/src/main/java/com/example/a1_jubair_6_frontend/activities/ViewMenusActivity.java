package com.example.a1_jubair_6_frontend.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.constants.AppConstants;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;
import com.example.a1_jubair_6_frontend.network.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ViewMenusActivity extends AppCompatActivity {

    private MaterialButton add, edit, delete;

    EditText location, mealType, date, id, updateLocation, updateMealType, updateDate, updateId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menus);

        add = findViewById(R.id.btnAddMenu);
        edit = findViewById(R.id.btnEditMenu);
        delete = findViewById(R.id.btnDeleteMenu);

        location = findViewById(R.id.inputLocationText);
        mealType = findViewById(R.id.mealTypeText);
        date = findViewById(R.id.inputDateText);
        id = findViewById(R.id.menuIdText);

        updateLocation = findViewById(R.id.locationUpdateText);
        updateMealType = findViewById(R.id.mealTypeUpdateText);
        updateDate = findViewById(R.id.dateUpdateText);
        updateId = findViewById(R.id.menuIdUpdateText);

        add.setOnClickListener(v -> {
            String locationText = location.getText().toString();
            String mealTypeText = mealType.getText().toString();
            String dateText = date.getText().toString();

            try {
                addMenuToServer(locationText, mealTypeText, dateText);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        edit.setOnClickListener(v -> {
            String menuID = updateId.getText().toString();
            String locationText = updateLocation.getText().toString();
            String mealTypeText = updateMealType.getText().toString();
            String dateText = updateDate.getText().toString();

            try{
                updateMenuToServer(menuID, locationText, mealTypeText, dateText);
            } catch(JSONException e){
                throw new RuntimeException(e);
            }
        });

        delete.setOnClickListener(v ->{
            String menuId = id.getText().toString();
            try{
                deleteMenuToServer(menuId);
            } catch (JSONException e){
                throw new RuntimeException(e);
            }
        });

    }


    private void addMenuToServer(String location, String mealType, String date) throws JSONException {
        String url = AppConstants.SERVER_URL + "/menu";

        JSONObject jsonBody = new JSONObject();

        jsonBody.put("location", location);
        jsonBody.put("meal", mealType);
        jsonBody.put("date", date.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.i("Menu Added", "Successfully added Menu");
                    Toast.makeText(this, "Successfully added Menu", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                    Toast.makeText(this, "Error making menu", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void deleteMenuToServer(String menuID) throws JSONException {
        String url = AppConstants.SERVER_URL + "/menu/" + menuID;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Log.i("Menu Deleted", "Successfully deleted Menu");
                    Toast.makeText(this, "Successfully deleted Menu", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                    Toast.makeText(this, "Error deleting menu", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void updateMenuToServer(String menuID, String location, String mealType, String date) throws JSONException {
        String url = AppConstants.SERVER_URL + "/menu/update/" + menuID;
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("location", location);
        jsonBody.put("meal", mealType);
        jsonBody.put("date", date);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                jsonBody,
                response -> {
                    Log.i("Menu Updated", "Successfully updated Menu with ID: " + menuID);
                    Toast.makeText(this, "Successfully updated Menu with ID: " + menuID, Toast.LENGTH_SHORT).show();

                },
                error -> {
                    Log.e("Request Error", String.valueOf(error.getMessage()));
                    Toast.makeText(this,"Error updating Menu with ID: " + menuID, Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}





