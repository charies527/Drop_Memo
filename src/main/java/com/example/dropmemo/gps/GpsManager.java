package com.example.dropmemo.gps;

// 위치 권한 관련
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

// 위치 관련
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class GpsManager {

    LocationManager locationManager;

    LocationListener locationListener;

    // 위치 결과 반환 인터페이스
    public interface GpsListener {

        void onLocationChanged(
                double lat,
                double lng,
                float accuracy
        );
    }

    public GpsManager(Context context) {

        // 위치 서비스 가져오기
        locationManager =
                (LocationManager)
                        context.getSystemService(
                                Context.LOCATION_SERVICE
                        );
    }

    // GPS 시작
    public void startGps(
            Context context,
            GpsListener listener
    ) {

        // 위치 변경 리스너
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(
                    @NonNull Location location
            ) {

                double lat =
                        location.getLatitude();

                double lng =
                        location.getLongitude();

                float accuracy =
                        location.getAccuracy();

                listener.onLocationChanged(
                        lat,
                        lng,
                        accuracy
                );
            }
        };

        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||

                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

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