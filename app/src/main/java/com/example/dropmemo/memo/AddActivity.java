package com.example.dropmemo.memo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private Double selectedLat;
    private Double selectedLng;

    private final String CLIENT_ID = "xry5ysz97e";
    private final String CLIENT_SECRET = "itBkXhWj3ORg94GslEWhD9Is0QFKhJ8W5HLGqW6y";

    DBHelper dbHelper;
    Switch swAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        dbHelper = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String radius = prefs.getString("radius", "100m");
        TextView tvRadius = findViewById(R.id.tv_radius);
        tvRadius.setText("현재 설정된 반경: " + radius);

        EditText etPlace = findViewById(R.id.et_place);
        EditText etMemo = findViewById(R.id.et_memo);
        ImageButton btnSearch = findViewById(R.id.btn_search);
        ListView listSearch = findViewById(R.id.list_search_result);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnBack = findViewById(R.id.btn_back);
        swAlarm = findViewById(R.id.switch_alarm);

        int memoId = getIntent().getIntExtra("id", -1);
        String editPlace = getIntent().getStringExtra("place");
        String editContent = getIntent().getStringExtra("content");
        boolean editAlarm = getIntent().getBooleanExtra("isAlarm", false);

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            selectedLat = getIntent().getDoubleExtra("latitude", 0);
            selectedLng = getIntent().getDoubleExtra("longitude", 0);
        }

        if (editPlace != null) {
            etPlace.setText(editPlace);
        }
        if (editContent != null) {
            etMemo.setText(editContent);
        }
        swAlarm.setChecked(editAlarm);

        btnSearch.setOnClickListener(v -> {
            String keyword = etPlace.getText().toString().trim();
            if (keyword.isEmpty()) {
                etPlace.setError("장소를 입력하세요");
                return;
            }
            searchPlace(keyword, listSearch);
        });

        etPlace.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                btnSearch.performClick();
                return true;
            }
            return false;
        });

        listSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPlace = (String) parent.getItemAtPosition(position);
            if (!"검색 결과 없음".equals(selectedPlace)) {
                etPlace.setText(selectedPlace);
                etMemo.requestFocus();
            }
            listSearch.setVisibility(View.GONE);
        });

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String place = etPlace.getText().toString().trim();
            String content = etMemo.getText().toString().trim();

            if (place.isEmpty() || content.isEmpty()) {
                Toast.makeText(getApplicationContext(), "장소와 내용을 모두 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isAlarm = swAlarm.isChecked();
            if (isAlarm && (selectedLat == null || selectedLng == null)) {
                Toast.makeText(getApplicationContext(), "알림을 켜려면 장소 검색을 먼저 해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (memoId == -1) {
                dbHelper.insertMemo(place, content, false, isAlarm, selectedLat, selectedLng);
            } else {
                dbHelper.updateMemo(memoId, place, content, isAlarm, selectedLat, selectedLng);
            }

            Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void searchPlace(String keyword, ListView listSearch) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            String url = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode?query=" + encodedKeyword;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("X-NCP-APIGW-API-KEY-ID", CLIENT_ID)
                    .addHeader("X-NCP-APIGW-API-KEY", CLIENT_SECRET)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("AAA", "address search failed", e);
                    searchPlaceName(keyword, listSearch);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body() == null ? "" : response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray addresses = jsonObject.getJSONArray("addresses");

                        if (addresses.length() > 0) {
                            showAddressResult(keyword, addresses, listSearch);
                        } else {
                            searchPlaceName(keyword, listSearch);
                        }
                    } catch (Exception e) {
                        Log.e("AAA", "address parse failed", e);
                        searchPlaceName(keyword, listSearch);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("AAA", "search failed", e);
            searchPlaceName(keyword, listSearch);
        }
    }

    private void showAddressResult(String keyword, JSONArray addresses, ListView listSearch) {
        runOnUiThread(() -> {
            try {
                JSONObject address = addresses.getJSONObject(0);
                selectedLat = address.getDouble("y");
                selectedLng = address.getDouble("x");

                String roadAddress = address.optString("roadAddress");
                String jibunAddress = address.optString("jibunAddress");
                String resultName = roadAddress.isEmpty()
                        ? (jibunAddress.isEmpty() ? keyword : jibunAddress)
                        : roadAddress;

                showSearchResults(listSearch, new String[]{resultName});
            } catch (Exception e) {
                Log.e("AAA", "show address failed", e);
                showNoResult(listSearch);
            }
        });
    }

    private void searchPlaceName(String keyword, ListView listSearch) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword + " 대한민국", "UTF-8");
            String url = "https://nominatim.openstreetmap.org/search?format=json&limit=1&countrycodes=kr&accept-language=ko&q=" + encodedKeyword;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "DropMemo/1.0")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("AAA", "place name search failed", e);
                    runOnUiThread(() -> Toast.makeText(AddActivity.this, "장소 검색에 실패했습니다", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body() == null ? "" : response.body().string();

                    try {
                        JSONArray places = new JSONArray(result);
                        runOnUiThread(() -> {
                            try {
                                if (places.length() > 0) {
                                    JSONObject place = places.getJSONObject(0);
                                    selectedLat = place.getDouble("lat");
                                    selectedLng = place.getDouble("lon");

                                    String displayName = place.optString("display_name");
                                    String resultName = displayName.isEmpty() ? keyword : displayName;
                                    showSearchResults(listSearch, new String[]{resultName});
                                } else {
                                    showNoResult(listSearch);
                                }
                            } catch (Exception e) {
                                Log.e("AAA", "place parse failed", e);
                                showNoResult(listSearch);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("AAA", "place API parse failed", e);
                        runOnUiThread(() -> showNoResult(listSearch));
                    }
                }
            });
        } catch (Exception e) {
            Log.e("AAA", "place name search failed", e);
            showNoResult(listSearch);
        }
    }

    private void showSearchResults(ListView listSearch, String[] results) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                AddActivity.this,
                android.R.layout.simple_list_item_1,
                results
        );
        listSearch.setAdapter(adapter);
        listSearch.setVisibility(View.VISIBLE);
    }

    private void showNoResult(ListView listSearch) {
        selectedLat = null;
        selectedLng = null;
        showSearchResults(listSearch, new String[]{"검색 결과 없음"});
    }
}