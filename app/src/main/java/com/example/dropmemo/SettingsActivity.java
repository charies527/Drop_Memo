package com.example.dropmemo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btnRadius = findViewById(R.id.btn_radius);
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

                            TextView tvRadiusSetting = findViewById(R.id.tv_radius_setting);
                            tvRadiusSetting.setText(selected);
                        })
                        .show();
            }
        });

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}