package com.example.myapplication;

// 위치 권한 관련
import android.Manifest;
import android.content.pm.PackageManager;

// 위치 관련
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

// 지오펜싱
import com.example.myapplication.geofence.GeofenceManager;
import com.example.myapplication.geofence.NotificationHelper;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;

    NaverMap naverMap;

    Marker currentMarker;

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

        // 위치 서비스 가져오기
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        // 위치 변경 리스너
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(
                    @NonNull Location location
            ) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                Log.i("AAA", "위도 : " + lat);
                Log.i("AAA", "경도 : " + lng);

                // 정확도 체크
                if (location.hasAccuracy()) {

                    float accuracy =
                            location.getAccuracy();

                    Log.i("AAA", "정확도 : " + accuracy);

                    if (accuracy > 30) {

                        Log.i("AAA", "정확도 낮음");
                        return;
                    }
                }

                // 현재 위치
                LatLng currentLocation =
                        new LatLng(lat, lng);

                // 카메라 이동
                if (naverMap != null) {

                    CameraUpdate cameraUpdate =
                            CameraUpdate.scrollTo(
                                    currentLocation
                            );

                    naverMap.moveCamera(cameraUpdate);
                }

                // 마커 생성
                if (currentMarker == null) {

                    currentMarker = new Marker();
                }

                // 마커 위치 설정
                currentMarker.setPosition(
                        currentLocation
                );

                // 지도 표시
                currentMarker.setMap(naverMap);

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
        };

        // 위치 권한 확인 (수정됨: 백그라운드 제외하고 일반 위치만 먼저 요청)
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||

                ActivityCompat.checkSelfPermission(
                        MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            // 일반 위치 권한 2개만 먼저 요청하여 팝업이 뜨게 함
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    100
            );

        } else {
            // 이미 일반 권한이 있는 경우, 백그라운드 권한 확인 후 GPS 시작
            checkBackgroundAndStartUpdates();
        }
    }

    // 백그라운드 위치 권한 확인 및 GPS 업데이트 시작 (안드로이드 11 대응 로직)
    private void checkBackgroundAndStartUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 일반 권한은 있고 백그라운드만 없는 경우, 백그라운드 권한 별도 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
        } else {
            // 모든 권한이 있거나 버전이 낮은 경우 GPS 시작
            startGpsUpdates();
        }
    }

    // 실제 GPS 업데이트 요청 실행 (원래 코드의 중복 호출 유지)
    private void startGpsUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // GPS 위치 요청
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,
                    10,
                    locationListener
            );

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,
                    0,
                    locationListener
            );
        }
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
            // 일반 위치 권한 승인 시 백그라운드 권한 확인으로 이동
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkBackgroundAndStartUpdates();
            } else {
                finish();
            }
        } else if (requestCode == 101) {
            // 백그라운드 권한 결과와 상관없이 GPS 업데이트 시도 (일반 권한은 이미 있으므로)
            startGpsUpdates();
        }
    }

    // 네이버 지도 준비 완료
    @Override
    public void onMapReady(
            @NonNull NaverMap naverMap
    ) {

        this.naverMap = naverMap;

        naverMap.setMinZoom(10.0);

        Log.i(
                "AAA",
                "지도가 준비되었습니다."
        );
    }
}