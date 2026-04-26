package com.cookandroid.real_memo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    DBHelper dbHelper;

    Switch swGps, swPush, swSound;

    Button btnBack2, btnRadius;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DBHelper(this);

        swGps = findViewById(R.id.switch_gps);
        swPush = findViewById(R.id.switch_push);
        swSound = findViewById(R.id.switch_sound);
        btnBack2 = findViewById(R.id.btn_back);
        btnRadius = findViewById(R.id.btn_radius);

        swGps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {

            } else {

            }
        });

        swPush.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {

            } else {

            }
        });

        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {

            } else {

            }
        });

        btnBack2.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, Home.class);
            startActivity(intent);
        });

        btnRadius.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, SettingRadius.class);
            startActivity(intent);
        });
    }
}
