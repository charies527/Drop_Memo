package com.example.dropmemo.memo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchApiManager {

    // 검색 API
    private final String CLIENT_ID =
            "1si5yo6Sb5y2lQ8qwUNb";

    private final String CLIENT_SECRET =
            "T4gcgFqRNT";

    // 지도 API
    private final String MAP_CLIENT_ID =
            "xry5ysz97e";

    private final String MAP_CLIENT_SECRET =
            "itBkXhWj3ORg94GslEWhD9Is0QFKhJ8W5HLGqW6y";

    // 결과 반환 인터페이스
    public interface SearchResultListener {

        void onSuccess(
                String place,
                String address,
                double lat,
                double lng
        );

        void onFail();
    }

    // 장소 검색 메서드
    public void searchPlace(
            String keyword,
            SearchResultListener listener
    ) {

        try {

            String encodedKeyword =
                    URLEncoder.encode(
                            keyword,
                            "UTF-8"
                    );

            String searchUrl =
                    "https://openapi.naver.com/v1/search/local.json?query="
                            + encodedKeyword
                            + "&display=1";

            OkHttpClient client =
                    new OkHttpClient();

            Request request =
                    new Request.Builder()
                            .url(searchUrl)
                            .addHeader(
                                    "X-Naver-Client-Id",
                                    CLIENT_ID
                            )
                            .addHeader(
                                    "X-Naver-Client-Secret",
                                    CLIENT_SECRET
                            )
                            .build();

            client.newCall(request)
                    .enqueue(new Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e
                        ) {

                            e.printStackTrace();

                            listener.onFail();
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) throws IOException {

                            String result =
                                    response.body().string();

                            try {

                                JSONObject jsonObject =
                                        new JSONObject(result);

                                JSONArray items =
                                        jsonObject.getJSONArray("items");

                                if(items.length() > 0){

                                    JSONObject item =
                                            items.getJSONObject(0);

                                    String title =
                                            item.getString("title");

                                    title =
                                            title.replaceAll(
                                                    "<[^>]*>",
                                                    ""
                                            );

                                    String address =
                                            item.getString(
                                                    "roadAddress"
                                            );

                                    // 좌표 변환
                                    getCoordinate(
                                            title,
                                            address,
                                            listener
                                    );

                                } else {

                                    listener.onFail();
                                }

                            } catch (Exception e){

                                e.printStackTrace();

                                listener.onFail();
                            }
                        }
                    });

        } catch (Exception e){

            e.printStackTrace();

            listener.onFail();
        }
    }

    // 주소 -> 좌표 변환
    private void getCoordinate(
            String title,
            String address,
            SearchResultListener listener
    ){

        try {

            String encodedAddress =
                    URLEncoder.encode(
                            address,
                            "UTF-8"
                    );

            String geoUrl =
                    "https://maps.apigw.ntruss.com/map-geocode/v2/geocode?query="
                            + encodedAddress;

            OkHttpClient client =
                    new OkHttpClient();

            Request request =
                    new Request.Builder()
                            .url(geoUrl)
                            .addHeader(
                                    "X-NCP-APIGW-API-KEY-ID",
                                    MAP_CLIENT_ID
                            )
                            .addHeader(
                                    "X-NCP-APIGW-API-KEY",
                                    MAP_CLIENT_SECRET
                            )
                            .build();

            client.newCall(request)
                    .enqueue(new Callback() {

                        @Override
                        public void onFailure(
                                Call call,
                                IOException e
                        ) {

                            e.printStackTrace();

                            listener.onFail();
                        }

                        @Override
                        public void onResponse(
                                Call call,
                                Response response
                        ) throws IOException {

                            String result =
                                    response.body().string();

                            try {

                                JSONObject jsonObject =
                                        new JSONObject(result);

                                JSONArray addresses =
                                        jsonObject.getJSONArray(
                                                "addresses"
                                        );

                                if(addresses.length() > 0){

                                    JSONObject addressObject =
                                            addresses.getJSONObject(0);

                                    double lat =
                                            Double.parseDouble(
                                                    addressObject.getString("y")
                                            );

                                    double lng =
                                            Double.parseDouble(
                                                    addressObject.getString("x")
                                            );

                                    listener.onSuccess(
                                            title,
                                            address,
                                            lat,
                                            lng
                                    );

                                } else {

                                    listener.onFail();
                                }

                            } catch (Exception e){

                                e.printStackTrace();

                                listener.onFail();
                            }
                        }
                    });

        } catch (Exception e){

            e.printStackTrace();

            listener.onFail();
        }
    }
}

