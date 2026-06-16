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

// 네이버 지도
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

// GPS
import com.example.dropmemo.gps.GpsManager;

// 지도
import com.example.dropmemo.map.MapManager;

// 지오펜싱
import com.example.dropmemo.geofence.GeofenceManager;
import com.example.dropmemo.geofence.NotificationHelper;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    NaverMap naverMap;

    // GPS
    GpsManager gpsManager;

    // 지도
    MapManager mapManager;

    // 지오펜싱
    GeofenceManager geofenceManager;

    // 중복 등록 방지
    boolean geofenceRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        int status =
                com.google.android.gms.common.GoogleApiAvailability
                        .getInstance()
                        .isGooglePlayServicesAvailable(this);

        Log.d("PLAY_SERVICE", "status = " + status);

        // ==========================
        // 백그라운드 위치 권한 확인
        // ==========================
        Log.d(
                "BACKGROUND_PERMISSION",
                String.valueOf(
                        ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                )
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // 알림 채널 생성
        NotificationHelper.INSTANCE
                .createNotificationChannel(this);

        // 지오펜스 매니저 생성
        geofenceManager = new GeofenceManager(this);

        // 네이버 지도 Fragment 가져오기
        MapFragment mapFragment =
                (MapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        // Fragment 없으면 생성
        if (mapFragment == null) {

            mapFragment = MapFragment.newInstance();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.map, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(this);

        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||

                ActivityCompat.checkSelfPermission(
                        MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            // 일반 위치 권한 요청
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    100
            );

        } else {

            // 백그라운드 권한 확인
            checkBackgroundAndStartUpdates();
        }
    }

    // 백그라운드 위치 권한 확인
    private void checkBackgroundAndStartUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            // 백그라운드 권한 요청
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    101
            );

        } else {

            // GPS 시작
            startGps();
        }
    }

    // GPS 시작
    private void startGps() {

        gpsManager =
                new GpsManager(this);

        gpsManager.startGps(
                this,
                (lat, lng, accuracy) -> {

                    Log.i("AAA", "위도 : " + lat);

                    Log.i("AAA", "경도 : " + lng);

                    Log.i("AAA", "정확도 : " + accuracy);

                    // 정확도 체크
                    if (accuracy > 30) {

                        Log.i("AAA", "정확도 낮음");

                        return;
                    }

                    // 카메라 이동
                    mapManager.moveCamera(
                            lat,
                            lng
                    );

                    // 마커 표시
                    mapManager.showMarker(
                            lat,
                            lng
                    );

                    // ---------------------------
                    // 지오펜스 등록
                    // ---------------------------
                    if (!geofenceRegistered) {

                        geofenceManager.addGeofence(
                                "current_place",
                                lat,
                                lng,
                                100f
                        );

                        geofenceRegistered = true;

                        Log.i(
                                "AAA",
                                "지오펜스 등록 완료"
                        );
                    }
                }
        );
    }

    // 권한 요청 결과
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == 100) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkBackgroundAndStartUpdates();

            } else {

                finish();
            }

        } else if (requestCode == 101) {

            startGps();
        }
    }

    // 네이버 지도 준비 완료
    @Override
    public void onMapReady(
            @NonNull NaverMap naverMap
    ) {

        this.naverMap = naverMap;

        naverMap.setMinZoom(10.0);

        // 지도 매니저 생성
        mapManager =
                new MapManager(naverMap);

        Log.i(
                "AAA",
                "지도가 준비되었습니다."
        );
    }
}