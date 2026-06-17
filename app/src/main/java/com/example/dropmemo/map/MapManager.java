package com.example.dropmemo.map;

// 네이버 지도
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

public class MapManager {

    NaverMap naverMap;

    Marker currentMarker;

    public MapManager(
            NaverMap naverMap
    ) {

        this.naverMap = naverMap;
    }

    // 카메라 이동
    public void moveCamera(
            double lat,
            double lng
    ) {

        // 현재 위치
        LatLng currentLocation =
                new LatLng(lat, lng);

        // 카메라 이동
        CameraUpdate cameraUpdate =
                CameraUpdate.scrollTo(
                        currentLocation
                );

        naverMap.moveCamera(cameraUpdate);
    }

    // 마커 표시
    public void showMarker(
            double lat,
            double lng
    ) {

        // 현재 위치
        LatLng currentLocation =
                new LatLng(lat, lng);

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
    }
}