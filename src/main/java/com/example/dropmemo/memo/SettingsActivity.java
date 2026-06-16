package com.example.dropmemo.memo;

import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.example.dropmemo.R;

public class SettingsActivity extends AppCompatActivity {

    DBHelper dbHelper;     // 현재 미사용
    Switch swGps, swPush, swSound;// 추후 기능 예정
    Button btnRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView tvRadiusSetting = findViewById(R.id.tv_radius_setting);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String radius = prefs.getString("radius", "100m");
        tvRadiusSetting.setText(radius);

        btnRadius = findViewById(R.id.btn_radius);

        btnRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            // 터치 시 반경 설정 창 띄움
            public void onClick(View v) {
                String[] options = {"20m", "30m", "50m", "100m", "200m"};

                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("알림 반경 설정")
                        .setItems(options, (dialog, which) -> {
                            String selected = options[which];
                            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
                            prefs.edit().putString("radius", selected).apply();

                            tvRadiusSetting.setText(selected);
                        })
                        .show();
            }
        });

        dbHelper = new DBHelper(this);

        swGps = findViewById(R.id.switch_gps);
        swPush = findViewById(R.id.switch_push);
        swSound = findViewById(R.id.switch_sound);
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

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*
        btnRadius.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SettingRadius.class);
            startActivity(intent);
        });
        */
    }
}