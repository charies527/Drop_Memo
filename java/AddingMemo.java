package com.cookandroid.real_memo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class AddingMemo extends AppCompatActivity {

    DBHelper dbHelper;

    EditText editPlace, editContent;

    Button btnBack1, btnSave;

    Switch swAlarm;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        editPlace = findViewById(R.id.et_place);
        editContent = findViewById(R.id.et_memo);
        btnBack1= findViewById(R.id.btn_back);
        btnSave = findViewById(R.id.btn_save);
        swAlarm= findViewById(R.id.switch_alarm);

        btnBack1.setOnClickListener(v -> {
            Intent intent = new Intent(AddingMemo.this, Home.class);
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

            boolean isFavorite = swAlarm.isChecked();

            dbHelper.insertMemo(place, content, isFavorite);

            Toast.makeText(getApplicationContext(),
                    "저장되었습니다.", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}
