package com.example.dropmemo.memo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dropmemo.R;

public class SettingsActivity extends AppCompatActivity {
    DBHelper dbHelper;

    Switch swGps, swPush, swSound;

    Button btnBack2, btnRadius;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnRadius = findViewById(R.id.btn_radius);

        btnRadius.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] options = {"20m", "30m", "50m", "100m", "200m"};

                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("알림 반경 설정")
                        .setItems(options, (dialog, which) -> {
                            String selected = options[which];
                            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
                            prefs.edit().putString("radius", selected).apply();

                            TextView tvRadiusSetting = findViewById(R.id.tv_radius_setting);
                            tvRadiusSetting.setText(selected);
                        })
                        .show();
            }
        });

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
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        btnRadius.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SettingRadius.class);
            startActivity(intent);
        });
    }
}
