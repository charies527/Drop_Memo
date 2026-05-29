package com.example.dropmemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dropmemo.R;

public class AddActivity extends AppCompatActivity {

    DBHelper dbHelper;

    EditText editPlace, editContent;

    Button btnBack1, btnSave;

    Switch swAlarm;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add); // 메모 추가 화면을 보여줘라

        dbHelper = new DBHelper(this);

        editPlace = findViewById(R.id.et_place);
        editContent = findViewById(R.id.et_memo);
        btnBack1= findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        swAlarm= findViewById(R.id.switch_alarm);

        // 설정된 반경을 표시
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String radius = prefs.getString("radius", "100m"); // 기본값 100m
        TextView tvRadius = findViewById(R.id.tv_radius);
        tvRadius.setText("현재 설정된 반경: " + radius);

        // 뒤로가기 버튼
        btnBack1.setOnClickListener(v -> {
            Intent intent = new Intent(AddActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // 저장 버튼
        btnSave.setOnClickListener(v -> {
            String place = editPlace.getText().toString();
            String content = editContent.getText().toString();

            if (place.isEmpty() || content.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "장소와 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isFavorite = swAlarm.isChecked();

            dbHelper.insertMemo(place, content, isFavorite);

            Toast.makeText(getApplicationContext(),
                    "저장되었습니다.", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}
