package com.example.dropmemo.memo;

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

    Memo memo;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        editPlace = findViewById(R.id.et_place);
        editContent = findViewById(R.id.et_memo);
        btnBack1= findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        swAlarm= findViewById(R.id.switch_alarm);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String radius = prefs.getString("radius", "100m");
        TextView tvRadius = findViewById(R.id.tv_radius);
        tvRadius.setText("현재 설정된 반경: " + radius);

        btnBack1.setOnClickListener(v -> {
            Intent intent = new Intent(AddActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        btnSave.setOnClickListener(v -> {
            String place = editPlace.getText().toString();
            String content = editContent.getText().toString();

            if (place.isEmpty() || content.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "장소와 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isAlarm = swAlarm.isChecked();

            dbHelper.insertMemo(place, content, false, isAlarm);

            Toast.makeText(getApplicationContext(),
                    "저장되었습니다.", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}
