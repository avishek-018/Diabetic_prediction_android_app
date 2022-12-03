package com.example.diabetespredictionapp;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    TextView textView;
    String resultText;
    int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        textView = findViewById(R.id.text_view);

        Intent intent = getIntent();
        String resultStr = intent.getStringExtra("message_key");

        result = Integer.parseInt(resultStr);

        if (result==0.0)
            resultText = "No Diabetic";
        else
            resultText = "Diabetic";

        textView.setTextColor(Color.parseColor("#6348bd"));
        textView.setText(resultText);

    }
}