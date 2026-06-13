package com.example.dropmemo.memo;

import android.content.Intent;
import android.widget.Switch;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.TextView;

import android.util.Log;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.inputmethod.EditorInfo;

import com.example.dropmemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {

    // 위도, 경도 변수
    private String selectedLat;
    private String selectedLng;

    // 네이버 API 정보
    private final String CLIENT_ID = "xry5ysz97e";
    private final String CLIENT_SECRET = "itBkXhWj3ORg94GslEWhD9Is0QFKhJ8W5HLGqW6y";

    DBHelper dbHelper;
    Switch swAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        // 설정된 반경 표시
        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

        String radius =
                prefs.getString("radius", "100m");

        TextView tvRadius =
                findViewById(R.id.tv_radius);

        tvRadius.setText("현재 설정된 반경: " + radius);

        // 검색 관련 UI 연결
        EditText etPlace =
                findViewById(R.id.et_place);

        EditText etMemo =
                findViewById(R.id.et_memo);

        ImageButton btnSearch =
                findViewById(R.id.btn_search);

        ListView listSearch =
                findViewById(R.id.list_search_result);

        swAlarm = findViewById(R.id.switch_alarm);
        Button btnSave =
                findViewById(R.id.btn_save);

        // 장소 검색 버튼
        btnSearch.setOnClickListener(v -> {

            String keyword =
                    etPlace.getText().toString();

            if(keyword.isEmpty()) {

                etPlace.setError("장소를 입력하세요");
                return;
            }

            try {

                String encodedKeyword =
                        URLEncoder.encode(keyword, "UTF-8");

                String url =
                        "https://maps.apigw.ntruss.com/map-geocode/v2/geocode?query="
                                + encodedKeyword;

                OkHttpClient client =
                        new OkHttpClient();

                Request request =
                        new Request.Builder()
                                .url(url)
                                .addHeader(
                                        "X-NCP-APIGW-API-KEY-ID",
                                        CLIENT_ID)
                                .addHeader(
                                        "X-NCP-APIGW-API-KEY",
                                        CLIENT_SECRET)
                                .build();

                client.newCall(request)
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(
                                    Call call,
                                    IOException e) {

                                e.printStackTrace();

                                Log.i("AAA",
                                        "API 요청 실패");
                            }

                            @Override
                            public void onResponse(
                                    Call call,
                                    Response response)
                                    throws IOException {

                                String result =
                                        response.body().string();

                                try {

                                    JSONObject jsonObject =
                                            new JSONObject(result);

                                    JSONArray addresses =
                                            jsonObject.getJSONArray(
                                                    "addresses");

                                    runOnUiThread(() -> {

                                        try {

                                            if(addresses.length() > 0){

                                                JSONObject address =
                                                        addresses.getJSONObject(0);

                                                selectedLat = address.getString("y");
                                                selectedLng = address.getString("x");;

                                                String[] results = {

                                                        keyword
                                                };

                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<>(
                                                                AddActivity.this,
                                                                android.R.layout.simple_list_item_1,
                                                                results
                                                        );

                                                listSearch.setAdapter(adapter);

                                            } else {

                                                String[] results = {
                                                        "검색 결과 없음"
                                                };

                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<>(
                                                                AddActivity.this,
                                                                android.R.layout.simple_list_item_1,
                                                                results
                                                        );

                                                listSearch.setAdapter(adapter);
                                            }

                                            listSearch.setVisibility(View.VISIBLE);

                                        } catch (Exception e){

                                            e.printStackTrace();
                                        }
                                    });

                                } catch (Exception e){

                                    e.printStackTrace();
                                }
                            }
                        });

            } catch (Exception e){

                e.printStackTrace();
            }
        });

        // 엔터 = 검색
        etPlace.setOnEditorActionListener(
                (v, actionId, event) -> {

                    if(actionId == EditorInfo.IME_ACTION_SEARCH){

                        btnSearch.performClick();

                        return true;
                    }

                    return false;
                });

        // 검색 결과 선택
        listSearch.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String selectedPlace =
                            (String) parent.getItemAtPosition(position);

                    etPlace.setText(selectedPlace);

                    listSearch.setVisibility(View.GONE);

                    etMemo.requestFocus();
                });

        // 뒤로가기
        Button btnBack =
                findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        // 저장
        btnSave.setOnClickListener(v -> {

            String place = etPlace.getText().toString();
            String content = etMemo.getText().toString();

            if (place.isEmpty() || content.isEmpty()) {

                Toast.makeText(
                        getApplicationContext(),
                        "장소와 내용을 모두 입력하세요.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean isFavorite =
                    swAlarm.isChecked();

            dbHelper.insertMemo(
                    place,
                    content,
                    isFavorite
            );

            Toast.makeText(
                    getApplicationContext(),
                    "저장되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });
    }
}