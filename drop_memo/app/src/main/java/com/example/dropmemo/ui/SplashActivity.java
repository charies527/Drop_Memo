package com.example.dropmemo.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dropmemo.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // 홈 화면으로 이동
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        finish();
    }
}