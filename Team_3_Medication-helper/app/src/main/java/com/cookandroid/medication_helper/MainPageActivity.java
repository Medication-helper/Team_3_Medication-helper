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

    private ViewGroup mapViewContainer;

    private TextView latView;
    private TextView lngView;


    private MapPOIItem mDefaultMarker;
    String[] permission_list={
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    Location myLocation;
    LocationManager manager;

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






        userData = (UserData) getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkPermission();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.homeNav);

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
        // 모든 권한을 허용했다면 사용자 위치를 측정한다.
        if(isGrant == true){
            getMyLocation();
        }
    }

    // 현재 위치를 가져온다.
    public void getMyLocation(){
        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
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
        // 현재 위치값을 추출한다.
        double lat=myLocation.getLatitude();
        double lng=myLocation.getLongitude();


    }



}