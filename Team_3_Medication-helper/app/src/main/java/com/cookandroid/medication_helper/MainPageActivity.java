/****************************
 MainPageActivity.java
 작성 팀 : Hello World!
 주 작성자 : 송승우
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraAnimation;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelOptions;


public class MainPageActivity extends AppCompatActivity implements
        KakaoMap.OnCameraMoveStartListener, KakaoMap.OnCameraMoveEndListener{

    private static final String LOG_TAG = "MainActivity";

    private ViewGroup mapViewContainer;

    private final int FINE_PERMISSION_CODE = 1;

    //체크할 권한 배열
    String[] permission_list={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private MapView mapView;
    private Label centerPointLabel;

    ImageView myLoc;
    ImageView Hospital;

    private KakaoMap kakaoMap;

    Location myLocation;
    LocationManager manager;

    public double lat;
    public double lng;

    private int startZoomLevel=15;


    UserData userData;


    //뒤로가기 누르면 앱종료시키는 함수
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainPageActivity.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        //네를 누르면 앱 종료
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
                //아니오 누르면 다이어로그를 종료
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog exitDialog = exitDialogBuilder.create();
        exitDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mapView=findViewById(R.id.map);
        myLoc=findViewById(R.id.myloc);

        userData = (UserData) getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkPermission();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.homeNav);


        checkPermission();

        showAlertDialog();

        mapView.start(new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap map) {
                kakaoMap=map;
                kakaoMap.setOnCameraMoveStartListener(MainPageActivity.this);
                kakaoMap.setOnCameraMoveEndListener(MainPageActivity.this);

                centerPointLabel = kakaoMap.getLabelManager().getLayer()
                        .addLabel(LabelOptions.from(kakaoMap.getCameraPosition().getPosition())
                                .setStyles(R.drawable.red_dot_marker));

            }

            @Override
            public LatLng getPosition() {
                // 지도 시작 시 위치 좌표를 설정
                System.out.println("첫 시작 위치 : "+lat+" "+lng);
                return LatLng.from(lat, lng);
            }

            @Override
            public int getZoomLevel() {
                // 지도 시작 시 확대/축소 줌 레벨 설정
                return 15;
            }
        });

        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();

                moveCamera(LatLng.from(lat,lng));
            }
        });

        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //현재 페이지에서 보여주는 액티비티
                    case R.id.homeNav:
                        return true;
                    //camera 버튼을 누르면 화면을 전환시켜준다.
                    case R.id.cameraNav:
                        startActivity(new Intent(getApplicationContext(), MedicRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //article 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.articleNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //user 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안녕하세요!")
                .setMessage("OOO님!")
                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getMyLocation();
                        moveCamera(LatLng.from(lat,lng));
                    }
                })
                .show();
    }

    public void checkPermission(){
        boolean isGrant=false;
        for(String str : permission_list){
            if(ContextCompat.checkSelfPermission(this,str)== PackageManager.PERMISSION_GRANTED){          }
            else{
                isGrant=false;
                break;
            }
        }
        if(isGrant==false){
            ActivityCompat.requestPermissions(this,permission_list,0);
        }
    }
    // 사용자가 권한 허용/거부 버튼을 눌렀을 때 호출되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGrant = true;
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                isGrant = false;
                break;
            }
        }
    }

    public void getMyLocation(){
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 권한이 모두 허용되어 있을 때만 동작하도록 한다.
        int chk1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int chk2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED){
            myLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            showMyLocation();
        }
        // 새롭게 위치를 측정한다.
        GpsListener listener = new GpsListener();
        if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, listener);
        }
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        }
    }
    // GPS Listener
    class GpsListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // 현재 위치 값을 저장한다.
            myLocation = location;
            // 위치 측정을 중단한다.
            manager.removeUpdates(this);
            // 지도를 현재 위치로 이동시킨다.
            showMyLocation();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
    }

    public void showMyLocation(){
        // LocationManager.GPS_PROVIDER 부분에서 null 값을 가져올 경우를 대비하여 장치
        if(myLocation == null){
            return;
        }
        // 현재 위치값을 추출한다.d
        lat=myLocation.getLatitude();
        lng=myLocation.getLongitude();

        moveCamera(LatLng.from(lat,lng));

        System.out.println("위도 : "+lat);
        System.out.println("경도 : "+lng);
    }

    @Override
    public void onCameraMoveStart(KakaoMap kakaoMap, GestureType gestureType) {
        Toast.makeText(this, "onCameraMoveStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraMoveEnd(KakaoMap kakaoMap, CameraPosition cameraPosition,
                                GestureType gestureType) {

        if (centerPointLabel != null) {
            centerPointLabel.moveTo(cameraPosition.getPosition());
        }
    }

    private void moveCamera(LatLng position) {

        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position),
                CameraAnimation.from(1500));

    }
}