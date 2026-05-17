package com.example.dropmemo.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.example.dropmemo.R;

public class AddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 설정된 반경을 표시
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String radius = prefs.getString("radius", "100m"); // 기본값 100m
        TextView tvRadius = findViewById(R.id.tv_radius);
        tvRadius.setText("현재 설정된 반경: " + radius);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}