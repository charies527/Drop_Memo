package com.example.dropmemo.memo;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dropmemo.R;

public class SettingsActivity extends AppCompatActivity {

    DBHelper dbHelper;
    Switch swGps, swPush, swSound;
    Button btnRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        TextView tvRadiusSetting = findViewById(R.id.tv_radius_setting);
        String radius = prefs.getString("radius", "100m");
        tvRadiusSetting.setText(radius);

        swGps = findViewById(R.id.switch_gps);
        swPush = findViewById(R.id.switch_push);
        swSound = findViewById(R.id.switch_sound);
        btnRadius = findViewById(R.id.btn_radius);

        swGps.setChecked(prefs.getBoolean("gps", true));
        swPush.setChecked(prefs.getBoolean("push", true));
        swSound.setChecked(prefs.getBoolean("sound", true));

        swGps.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean("gps", isChecked).apply());
        swPush.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean("push", isChecked).apply());
        swSound.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean("sound", isChecked).apply());

        btnRadius.setOnClickListener(v -> {
            String[] options = {"20m", "30m", "50m", "100m", "200m"};
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("알림 반경 설정")
                    .setItems(options, (dialog, which) -> {
                        String selected = options[which];
                        prefs.edit().putString("radius", selected).apply();
                        tvRadiusSetting.setText(selected);
                    })
                    .show();
        });

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }
}
