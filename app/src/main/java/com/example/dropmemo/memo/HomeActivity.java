package com.example.dropmemo.memo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.dropmemo.R;
import com.example.dropmemo.geofence.GeofenceManager;
import com.example.dropmemo.geofence.NotificationHelper;
import com.example.dropmemo.gps.GpsManager;
import com.example.dropmemo.map.MapManager;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 100;
    private static final int REQUEST_BACKGROUND_LOCATION = 101;
    private static final int REQUEST_NOTIFICATION = 102;

    private MapView mapView;
    private MapManager mapManager;
    private GpsManager gpsManager;
    private GeofenceManager geofenceManager;
    private DBHelper dbHelper;
    private boolean mapReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DBHelper(this);
        geofenceManager = new GeofenceManager(this);
        NotificationHelper.INSTANCE.createNotificationChannel(this);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnList = findViewById(R.id.btn_list);
        Button btnSettings = findViewById(R.id.btn_settings);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AddActivity.class)));
        btnList.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ListActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SettingsActivity.class)));

        requestNotificationPermissionIfNeeded();
        checkLocationPermission();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mapManager = new MapManager(naverMap);
        naverMap.setMinZoom(10.0);
        mapReady = true;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (!mapReady) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_LOCATION);
            return;
        }

        checkBackgroundPermissionAndStart();
    }

    private void checkBackgroundPermissionAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION);
            return;
        }

        startGps();
        registerMemoGeofences();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION);
        }
    }

    private void startGps() {
        if (gpsManager != null) {
            return;
        }

        gpsManager = new GpsManager(this);
        gpsManager.startGps(this, (lat, lng, accuracy) -> {
            if (accuracy > 50 || mapManager == null) {
                return;
            }
            mapManager.moveCamera(lat, lng);
            mapManager.showMarker(lat, lng);
        });
    }

    private void registerMemoGeofences() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        float radius = parseRadius(prefs.getString("radius", "100m"));
        ArrayList<Memo> alarmMemos = dbHelper.getAlarmMemos();

        geofenceManager.removeAllGeofences();
        for (Memo memo : alarmMemos) {
            if (memo.latitude == null || memo.longitude == null) {
                continue;
            }
            geofenceManager.addGeofence("memo_" + memo.id + "_" + memo.place, memo.latitude, memo.longitude, radius);
        }
    }

    private float parseRadius(String value) {
        try {
            return Float.parseFloat(value.replace("m", ""));
        } catch (Exception e) {
            return 100f;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkBackgroundPermissionAndStart();
            }
        } else if (requestCode == REQUEST_BACKGROUND_LOCATION) {
            startGps();
            registerMemoGeofences();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mapReady) {
            registerMemoGeofences();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
