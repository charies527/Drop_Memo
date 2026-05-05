package com.example.myapplication;

//위치 권한 관련
import android.Manifest;
import android.content.pm.PackageManager;

//위치 관련 클래스들
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    LocationManager locationManager;     // 위치 정보를 관리하는 관리자역할
    LocationListener locationListener;   //위치가 바뀔 때마다 실행되는 리스너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //위치 서비스 가져오기(GPS 사용할 준비)
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //위치가 바뀔 때 실행되는 코드 정의
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 위도, 경도 값을 추출, 여기에서 솰용할 코드 작성
                //로그로 위치 학인

                double lat = location.getLatitude(); //위도
                double lng = location.getLongitude(); // 경도

                Log.i("AAA","위도 : " + lat );
                Log.i("AAA","경도 : " + lng );
                Log.i("AAA", "" );
            }
        };

        //위치 권한이 있는지 확인
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){


            //권한 없을 때 상용자에게 팝업으로 뜸
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},
            100);//요첨 코드
            return;//권한 결과 기다리기
        }
        //GPS 기반으로 위치 요청
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, //GPS 사용
                3000,10,     //3초마다, 10m 마다
                locationListener       //위치 변경 시 실행할 코드
        );

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,0,     //안 움직일 때
                locationListener
        );
    }
    //권한 요청 결과를 받는 함수
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //우리가 요청한 코드(100)인지 확인
        if(requestCode == 100){

            //권한이 없으면?
            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                //앱 종료
                finish();
                return;
            }

            //권한이 허용되면 위치 업데이트 시작
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,0,   // 첫 위치를 받기 위해 이동거리를 0으로 설정
                    locationListener
            );
        }

    }
}