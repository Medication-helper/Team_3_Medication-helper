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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.impl.CameraFactory;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraAnimation;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Array;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class Hospital{
    int index;
    String roadAddressNames;
    String placeNames;
    String xValues;
    String yValues;
    String phoneNumbers;

    Hospital(int index, String roadAddressNames, String placeNames, String xValues, String yValues, String phoneNumbers){
        this.index=index;
        this.roadAddressNames=roadAddressNames;
        this.placeNames=placeNames;
        this.xValues=xValues;
        this.yValues=yValues;
        this.phoneNumbers=phoneNumbers;
    }

    public int get_index(){
        return this.index;
    }

    public String get_roadAddress(){
        return this.roadAddressNames;
    }

    public String get_placeName(){
        return this.placeNames;
    }

    public String get_xValues(){
        return this.xValues;
    }

    public String get_yValues(){
        return this.yValues;
    }

    public String get_phone(){
        return this.phoneNumbers;
    }


}


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

    TextView hname;
    TextView road;
    TextView pNum;

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

    String[] category_name_array={
            "정형외과","내과","피부과","소아과","산부인과","비뇨기과","치과","안과","이비인후과","한의원","외과","약국"
    };

    ArrayList<String> roadAddressNames = new ArrayList<>();
    ArrayList<String> placeNames = new ArrayList<>();
    ArrayList<String> xValues = new ArrayList<>();
    ArrayList<String> yValues = new ArrayList<>();
    ArrayList<String> phoneNumbers = new ArrayList<>();

    ArrayList<Hospital> searchResult = new ArrayList<>();

    private LabelLayer labelLayer;

    public int LabelCount=0;
    public int documentLength=0;

    UserData userData;

    ListView searchList;

    public ArrayAdapter<String> searchadapter;

    boolean labelExists;

    int selected=0;

    LatLng selectedpos;


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
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.main_titlebar_custom); // 커스텀 사용할 파일 위치

        hname=findViewById(R.id.tv_list_name);
        road=findViewById(R.id.tv_list_road);
        pNum=findViewById(R.id.tv_list_phone);

        mapView=findViewById(R.id.map);
        myLoc=findViewById(R.id.myloc);
        Hospital=findViewById(R.id.places);
        searchList=findViewById(R.id.hospitalList);

        userData = (UserData) getApplicationContext();

        ActionBar actionBar = getSupportActionBar();


        checkPermission();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.homeNav);

        checkPermission();

        showAlertDialog();

        searchadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placeNames);
        searchList.setAdapter(searchadapter);


        mapView.start(new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap=map;
                kakaoMap.setOnCameraMoveStartListener(MainPageActivity.this);
                kakaoMap.setOnCameraMoveEndListener(MainPageActivity.this);

                labelLayer = kakaoMap.getLabelManager().getLayer();

                centerPointLabel = kakaoMap.getLabelManager().getLayer()
                        .addLabel(LabelOptions.from(LatLng.from(lat,lng))
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
                return 13;
            }
        });

        /*내 위치를 가져오는 이미지버튼*/
        myLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();

                moveCamera(LatLng.from(lat,lng));
                searchList.setVisibility(View.INVISIBLE);
            }
        });

        /*카테고리를 선택하는 이미지버튼*/
        Hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                labelExists=labelLayer.hasLabel("selected");

                if(labelExists){
                    labelLayer.remove(labelLayer.getLabel("selected"));
                }

                LatLng myposition = LatLng.from(lat, lng); // 원하는 새로운 위치

                CameraUpdate cameraUpdate2 = CameraUpdateFactory.newCenterPosition(myposition, 13);
                kakaoMap.moveCamera(cameraUpdate2);
                searchList.setVisibility(View.VISIBLE);
                showCategoryList();
            }
        });

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedHospital = (String) adapterView.getItemAtPosition(position);

                labelExists=labelLayer.hasLabel("selected");

                if(labelExists){
                    labelLayer.remove(labelLayer.getLabel("selected"));
                    LabelStyle style=LabelStyle.from(R.drawable.blue_dot_marker).setTextStyles(15, Color.BLACK).setZoomLevel(5);

                    labelLayer.addLabel(LabelOptions.from(String.valueOf(selected),selectedpos)
                            .setStyles(style));

                }

                for (int i = 0; i < searchResult.size(); i++) {
                    if (searchResult.get(i).get_placeName().compareTo(selectedHospital) == 0) {
                        hname.setText(searchResult.get(i).get_placeName());
                        road.setText(searchResult.get(i).get_roadAddress());
                        pNum.setText(searchResult.get(i).get_phone());

                        selected=i+1;

                        labelLayer.remove(labelLayer.getLabel(String.valueOf(selected)));

                        Double latitude = Double.parseDouble(searchResult.get(i).get_yValues());
                        Double longitude = Double.parseDouble(searchResult.get(i).get_xValues());

                        LatLng pos = LatLng.from(latitude, longitude);
                        selectedpos=pos;

                        LabelStyle style = LabelStyle.from(R.drawable.green_dot_marker).setTextStyles(15, Color.BLACK).setZoomLevel(11);
                        labelLayer.addLabel(LabelOptions.from("selected", pos)
                                .setStyles(style));

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(pos, 15);
                        kakaoMap.moveCamera(cameraUpdate);
                    }
                }
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
                        searchList.setVisibility(View.INVISIBLE);
                        finish();
                        return true;
                    //article 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.articleNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity.class));
                        overridePendingTransition(0, 0);
                        searchList.setVisibility(View.INVISIBLE);
                        finish();
                        return true;
                    //user 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        searchList.setVisibility(View.INVISIBLE);
                        finish();
                        return true;
                }
                return false;
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 갱신")
                //.setMessage(userData.getUserName() + "님!")
                .setMessage("현재 위치를 가져옵니다")
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
            centerPointLabel.moveTo(LatLng.from(lat,lng));
        }


    }

    private void moveCamera(LatLng position) {

        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position),
                CameraAnimation.from(1500));

    }

    private void showCategoryList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("병원 선택");
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                this,android.R.layout.simple_list_item_1,category_name_array
        );

        DialogListener listener=new DialogListener();
        builder.setAdapter(adapter,listener);
        builder.setNegativeButton("취소",null);
        builder.show();
    }

    // 다이얼로그의 리스너
    class DialogListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // 사용자가 선택한 항목 인덱스번째의 type 값을 가져온다.
            String type=category_name_array[i];
            System.out.println("선택 항목 : "+type);

            // 주변 정보를 가져온다
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String xmldata="";
                    xmldata=coordToAddr(lat,lng,type);
                    try{
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(new InputSource(new StringReader(xmldata)));


                        // 결과를 저장할 배열
                        NodeList documents = document.getElementsByTagName("documents");
                        searchResult.clear();
                        placeNames.clear();

                        for (int i = 0; i < documents.getLength(); i++) {
                            Element element = (Element) documents.item(i);
                            String roadAddressName = element.getElementsByTagName("road_address_name").item(0).getTextContent();
                            String placeName = element.getElementsByTagName("place_name").item(0).getTextContent();
                            String x = element.getElementsByTagName("x").item(0).getTextContent();
                            String y = element.getElementsByTagName("y").item(0).getTextContent();
                            String phone = element.getElementsByTagName("phone").item(0).getTextContent();

                            Hospital hospital = new Hospital(
                                    i,
                                    roadAddressName,
                                    placeName,
                                    x,
                                    y,
                                    phone
                            );

                            searchResult.add(hospital);

                            // 추출한 데이터를 배열에 추가
                            roadAddressNames.add(roadAddressName);
                            placeNames.add(placeName);//병원 목록
                            xValues.add(x);
                            yValues.add(y);
                            phoneNumbers.add(phone);
                        }

                        documentLength=documents.getLength();

                        //여기서부터 데이터 사용 가능
                        for (int i = 0; i < documents.getLength(); i++){
                            //System.out.println("도로명주소 : "+roadAddressNames.get(i));
                            System.out.println("병원명 : "+placeNames.get(i));
                            //System.out.println("전화번호 : "+phoneNumbers.get(i));
                            //System.out.println("x : "+xValues.get(i));
                            //System.out.println("y : "+yValues.get(i));
                            //System.out.println("다음 항목");
                        }

//                        for (int i=0;i<searchResult.size();i++){
//                            System.out.println("인덱스 : "+searchResult.get(i).get_index());
//                            System.out.println("도로명주소 : "+searchResult.get(i).get_roadAddress());
//                            System.out.println("병원명 : "+searchResult.get(i).get_placeName());
//                            System.out.println("전화번호 : "+searchResult.get(i).get_phone());
//                            System.out.println("x : "+searchResult.get(i).get_xValues());
//                            System.out.println("y : "+searchResult.get(i).get_yValues());
//                            System.out.println("다음 항목");
//                        }

                        LabelStyle style=LabelStyle.from(R.drawable.blue_dot_marker).setTextStyles(15, Color.BLACK).setZoomLevel(5);


                        //진료과 선택에 따른 병원 목록은 최대 15개까지 검색
                        //각각 1~15의 번호가 Lavel들을 구분하는 id로 붙는다


                        //지도에 현재 올라와있는 모든 라벨들을 삭제한다
                        for(int i=1;i<=LabelCount;i++){
                            labelLayer.remove(labelLayer.getLabel(String.valueOf(i)));
                            System.out.println("알림 : "+i+"번째 마커 삭제 완료");
                        }

                        LabelCount=0;


//                        //좌표로 지도에 병원 위치 표시 라벨들을 추가(라벨 ID는 1~15)
//                        for(int i=0;i<roadAddressNames.size();i++){
//                            LabelCount++;
//                            Double latitude= Double.parseDouble(yValues.get(i));
//                            Double longitude = Double.parseDouble(xValues.get(i));
//
//                            LatLng pos = LatLng.from(latitude,longitude);
//                            labelLayer.addLabel(LabelOptions.from("hospital"+LabelCount,pos)
//                                    .setStyles(style)
//                                    );
//                        }

                        //좌표로 지도에 병원 위치 표시 라벨들을 추가(라벨 ID는 1~15)
                        for(int i=0;i<searchResult.size();i++){
                            LabelCount++;
                            Double latitude= Double.parseDouble(searchResult.get(i).get_yValues());
                            Double longitude = Double.parseDouble(searchResult.get(i).get_xValues());

                            LatLng pos = LatLng.from(latitude,longitude);
                            labelLayer.addLabel(LabelOptions.from(String.valueOf(LabelCount),pos)
                                    .setStyles(style)
                            );
                        }
                        System.out.println("라벨 갯수 : "+LabelCount);



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                searchadapter.notifyDataSetChanged();
                            }
                        });


                        searchList.setVisibility(View.VISIBLE);


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).start();

        }
    }

    public static String coordToAddr(double lat, double lng, String keyword) {
        String longitude=Double.toString(lng);
        String latitude=Double.toString(lat);
        String encode="";

        try{
            encode= URLEncoder.encode(keyword,"UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String url = "https://dapi.kakao.com/v2/local/search/keyword.xml?page=1&size=15&sort=accuracy&query="+encode+"&x="+longitude+"&y="+latitude+"&radius=2000";

        try{
            String xmlData = getJSONData(url);
            return xmlData;


        }catch(Exception e){
            System.out.println("주소 api 요청 에러");
            e.printStackTrace();
            return null;
        }
    }


    private static String getJSONData(String apiURL) throws Exception{
        HttpURLConnection conn = null;
        StringBuffer response = new StringBuffer();

        //인증키 - KakaoAK하고 한 칸 띄워주셔야해요!
        String auth = "KakaoAK " + "df024ece0cf2fd13374976e289fd5914";

        //URL 설정
        URL url = new URL(apiURL);

        conn = (HttpURLConnection) url.openConnection();

        //Request 형식 설정
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Requested-With", "curl");
        conn.setRequestProperty("Authorization", auth);

        //request에 JSON data 준비
        conn.setDoOutput(true);

        //보내고 결과값 받기
        int responseCode = conn.getResponseCode();
        if (responseCode == 400) {
            System.out.println("400:: 해당 명령을 실행할 수 없음");
        } else if (responseCode == 401) {
            System.out.println("401:: Authorization가 잘못됨");
        } else if (responseCode == 500) {
            System.out.println("500:: 서버 에러, 문의 필요");
        } else { // 성공 후 응답 JSON 데이터받기

            Charset charset = Charset.forName("UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
        }
        System.out.println(response.toString());

        return response.toString();
    }
}