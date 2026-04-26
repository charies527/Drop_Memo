package com.cookandroid.real_memo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Home extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnSetting = findViewById(R.id.btn_settings);
        Button btnList = findViewById(R.id.btn_list);

        dbHelper = new DBHelper(this);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddingMemo.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Setting.class);
            startActivity(intent);
        });

        btnList.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, MemoList.class);
            startActivity(intent);
        });
    }
}