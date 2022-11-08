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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MainPageActivity extends AppCompatActivity{ //implements MapView.MapViewEventListener {
    TextView TvHelloName;

    private AdView mAdview;
    private ListView mListView;
    private static final String LOG_TAG = "MainActivity";
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    //private static final MapPoint DEFAULT_MARKER_POINT1 = MapPoint.mapPointWithGeoCoord(36.627817766042234, 127.49140052015375);
    //private static final MapPoint DEFAULT_MARKER_POINT2 = MapPoint.mapPointWithGeoCoord(36.6166363294552, 127.518193782998);
    //private static final MapPoint DEFAULT_MARKER_POINT3 = MapPoint.mapPointWithGeoCoord(36.626477701126305, 127.49294825679303);

    private MapPOIItem mDefaultMarker;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION};

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

        TvHelloName = (TextView) findViewById(R.id.tvHelloName);
        userData = (UserData) getApplicationContext();

        TvHelloName.setText("안녕하세요 \n" + userData.getUserNickName() + "님!");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

//        mapView=new MapView(this);
//        mapViewContainer=(ViewGroup)findViewById(R.id.map_view) ;
//        mapViewContainer.addView(mapView);
//        mapView.setZoomLevel(6,true);
//        mapView.setMapViewEventListener(this);
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
//
//        MapPOIItem marker1=new MapPOIItem();
//        MapPOIItem marker2=new MapPOIItem();
//        MapPOIItem marker3=new MapPOIItem();
//
//        marker1.setItemName("김주영내과의원");
//        marker1.setTag(0);
//        marker1.setMapPoint(DEFAULT_MARKER_POINT1);
//        marker1.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        marker1.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//
//        marker2.setItemName("동남이비인후과의원");
//        marker2.setTag(0);
//        marker2.setMapPoint(DEFAULT_MARKER_POINT2);
//        marker2.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        marker2.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//
//        marker3.setItemName("김영태신경외과의원");
//        marker3.setTag(0);
//        marker3.setMapPoint(DEFAULT_MARKER_POINT3);
//        marker3.setMarkerType(MapPOIItem.MarkerType.BluePin);
//        marker3.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//
//        mapView.addPOIItem(marker1);
//        mapView.selectPOIItem(marker1, true);
//        mapView.setMapCenterPoint(DEFAULT_MARKER_POINT1, false);
//
//        mapView.addPOIItem(marker2);
//        mapView.selectPOIItem(marker2, true);
//        mapView.setMapCenterPoint(DEFAULT_MARKER_POINT2, false);
//
//        mapView.addPOIItem(marker3);
//        mapView.selectPOIItem(marker3, true);
//        mapView.setMapCenterPoint(DEFAULT_MARKER_POINT3, false);
//
//        if (!checkLocationServicesStatus()) {
//            showDialogForLocationServiceSetting();
//        }else {
//            checkRunTimePermission();
//        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.homeNav);

        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
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
        /*
        //현재 액티비티에서 MedicRegisterActivity로 넘겨주는 버튼
        btnPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediRegIntent = new Intent(MainPageActivity.this, MedicRegisterActivity.class);
                startActivity(mediRegIntent);
            }
        });
        //현재 액티비티에서 MedicCheckActivity로 넘겨주는 버튼
        btnJar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediCheckIntent = new Intent(MainPageActivity.this, MedicCheckActivity.class);
                startActivity(mediCheckIntent);
            }
        });

        */

        MobileAds.initialize(this, new OnInitializationCompleteListener() { //광고 초기화
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        mAdview = findViewById(R.id.adView); //배너광고 레이아웃 가져오기
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdview.loadAd(adRequest);
//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER); //광고 사이즈는 배너 사이즈로 설정
//        adView.setAdUnitId("\n" + " ca-app-pub-3940256099942544/630097811");

        //mListView = (ListView) findViewById(R.id.productlist);
        //dataSetting();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapViewContainer.removeView(mapView);
//    }


//    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
//        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
//        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
//    }
//
//    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
//    }
//
//
//    public void onCurrentLocationUpdateFailed(MapView mapView) {
//    }
//
//
//    public void onCurrentLocationUpdateCancelled(MapView mapView) {
//    }
//
//
//    private void onFinishReverseGeoCoding(String result) {
////        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
//    }
//
//    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grandResults) {
//
//        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
//
//            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
//            boolean check_result = true;
//
//            // 모든 퍼미션을 허용했는지 체크합니다.
//            for (int result : grandResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    check_result = false;
//                    break;
//                }
//            }
//
//            if ( check_result ) {
//                Log.d("@@@", "start");
//                //위치 값을 가져올 수 있음
//
//            }
//            else {
//                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있다
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
//                    Toast.makeText(this, "", Toast.LENGTH_SHORT).makeText(MainPageActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
//                    finish();
//                }else {
//                    Toast.makeText(MainPageActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//    void checkRunTimePermission(){
//
//        //런타임 퍼미션 처리
//        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
//        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainPageActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {
//            // 2. 이미 퍼미션을 가지고 있다면
//            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
//            // 3.  위치 값을 가져올 수 있음
//
//        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
//            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MainPageActivity.this, REQUIRED_PERMISSIONS[0])) {
//                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
//                Toast.makeText(MainPageActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
//                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
//                ActivityCompat.requestPermissions(MainPageActivity.this, REQUIRED_PERMISSIONS,
//                        PERMISSIONS_REQUEST_CODE);
//            } else {
//                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
//                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
//                ActivityCompat.requestPermissions(MainPageActivity.this, REQUIRED_PERMISSIONS,
//                        PERMISSIONS_REQUEST_CODE);
//            }
//        }
//    }
//
//    //여기부터는 GPS 활성화를 위한 메소드들
//    private void showDialogForLocationServiceSetting() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
//        builder.setTitle("위치 서비스 비활성화");
//        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
//                + "위치 설정을 수정하시겠습니까?");
//        builder.setCancelable(true);
//        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                Intent callGPSSettingIntent
//                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
//            }
//        });
//        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        builder.create().show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case GPS_ENABLE_REQUEST_CODE:
//                //사용자가 GPS 활성 시켰는지 검사
//                if (checkLocationServicesStatus()) {
//                    if (checkLocationServicesStatus()) {
//                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
//                        checkRunTimePermission();
//                        return;
//                    }
//                }
//                break;
//        }
//    }
//
//    public boolean checkLocationServicesStatus() {
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    @Override
//    public void onMapViewInitialized(MapView mapView) {
//
//    }
//
//    @Override
//    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
//
//    }
//
//    @Override
//    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
//
//    }
//
//    @Override
//    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
//
//    }
//    private void dataSetting(){
//        MyAdapter myAdapter = new MyAdapter();
//
//        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
//        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
//        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
//        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
//        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
//
//        //mListView.setAdapter(myAdapter);
//    }


}