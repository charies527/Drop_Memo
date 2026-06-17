package com.example.dropmemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dropmemo.geofence.GeofenceManager;
import com.example.dropmemo.geofence.NotificationHelper;
import com.example.dropmemo.gps.GpsManager;
import com.example.dropmemo.map.MapManager;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    NaverMap naverMap;
    GpsManager gpsManager;
    MapManager mapManager;
    GeofenceManager geofenceManager;
    boolean geofenceRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        int status = com.google.android.gms.common.GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(this);
        Log.d("PLAY_SERVICE", "status = " + status);

        Log.d("BACKGROUND_PERMISSION", String.valueOf(
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NotificationHelper.INSTANCE.createNotificationChannel(this);
        geofenceManager = new GeofenceManager(this);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
        } else {
            checkBackgroundAndStartUpdates();
        }
    }

    private void checkBackgroundAndStartUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
        } else {
            startGps();
        }
    }

    private void startGps() {
        gpsManager = new GpsManager(this);
        gpsManager.startGps(this, (lat, lng, accuracy) -> {
            Log.i("AAA", "lat : " + lat);
            Log.i("AAA", "lng : " + lng);
            Log.i("AAA", "accuracy : " + accuracy);

            if (accuracy > 30 || mapManager == null) {
                return;
            }

            mapManager.moveCamera(lat, lng);
            mapManager.showMarker(lat, lng);

            if (!geofenceRegistered) {
                geofenceManager.addGeofence("current_place", lat, lng, 100f);
                geofenceRegistered = true;
                Log.i("AAA", "geofence registered");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkBackgroundAndStartUpdates();
            } else {
                finish();
            }
        } else if (requestCode == 101) {
            startGps();
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setMinZoom(10.0);
        mapManager = new MapManager(naverMap);
        Log.i("AAA", "map is ready");
    }
}
