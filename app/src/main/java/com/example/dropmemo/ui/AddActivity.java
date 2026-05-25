package com.example.dropmemo.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.TextView;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.inputmethod.EditorInfo;

import com.example.dropmemo.R;

public class AddActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 설정된 반경을 표시
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String radius = prefs.getString("radius", "100m"); // 기본값 100m
        TextView tvRadius = findViewById(R.id.tv_radius);
        tvRadius.setText("현재 설정된 반경: " + radius);

        // 검색
        EditText etPlace = findViewById(R.id.et_place);
        EditText etMemo = findViewById(R.id.et_memo);
        ImageButton btnSearch = findViewById(R.id.btn_search);
        ListView listSearch = findViewById(R.id.list_search_result);

        // 장소 검색 -> 돋보기 클릭 시 연관 검색어 결과
        btnSearch.setOnClickListener(v -> {

            String keyword = etPlace.getText().toString();

            if(keyword.isEmpty()) {
                etPlace.setError("장소를 입력하세요");
                return;
            }

            String[] results = {
                    keyword + " 테스트1",
                    keyword + " 테스트2",
                    keyword + " 테스트3"
            };

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    AddActivity.this,
                    android.R.layout.simple_list_item_1,
                    results
            );

            listSearch.setAdapter(adapter);

            listSearch.setVisibility(View.VISIBLE);
        });

        // 엔터 = 돋보기 버튼
        etPlace.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnSearch.performClick();
                return true;
            }

            return false;
        });

        // 검색 리스트 중 하나 선택 시 사라짐 -> 커서 메모 칸으로 이동
        listSearch.setOnItemClickListener((parent, view, position, id) -> {

            String selectedPlace =
                    (String) parent.getItemAtPosition(position);

            etPlace.setText(selectedPlace);
            listSearch.setVisibility(View.GONE);

            etMemo.requestFocus();
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