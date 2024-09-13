package com.example.basic_ui_testing;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {  // Ensure the class name matches the file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views by ID
        EditText editTextName = findViewById(R.id.editTextName);
        Button buttonGreet = findViewById(R.id.buttonGreet);
        TextView textView = findViewById(R.id.textView);

        // Set button click listener
        buttonGreet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input text
                String name = editTextName.getText().toString();
                // Display greeting
                textView.setText("Hello, " + name + "!");
            }
        });
    }
}