/****************************
 MedicineListActivity.java
 작성 팀 : Hello World!
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class MedicineListActivity extends AppCompatActivity {
    Button delBtn;
    ListView medicationListView;
    Button btnBack;

    /* 의약품 DB를 사용하기 위한 변수들 */
    UserData userData;
    com.cookandroid.medication_helper.MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    /*스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(MedicineListActivity.this, com.cookandroid.medication_helper.MainPageActivity.class);
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_medicinelist);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.medilisttitlebar_custom); // 커스텀 사용할 파일 위치

        Button forBtn = findViewById(R.id.forbtn);//병용금기약물 항목 이동 버튼
        Button prgBtn = findViewById(R.id.prgbtn);//임부금기약물 항목 이동 버튼
        Button overBtn = findViewById(R.id.overbtn);//효능중복약물 항목 이동 버튼


        userData = (UserData) getApplicationContext();
        myHelper = new com.cookandroid.medication_helper.MedicDBHelper(this);
        sqlDB = myHelper.getReadableDatabase(); // 의약품 DB를 읽기 전용으로 불러옴
        // 현재 로그인중인 사용자가 복용중인 의약품의 DB를 읽어들임
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM medicTBL WHERE uID = '" + userData.getUserID() + "';", null);

        medicationListView=(ListView)findViewById(R.id.medicationlist);
        delBtn=(Button)findViewById(R.id.btnalldelete);

        String[] medicineArray = new String[cursor.getCount()];//DB에서 받아온 처방약 목록을 저장하는 String 배열
        int serialNo = 0;

        while (cursor.moveToNext()) { // DB에서 받아온 처방약 목록을 상단의 배열에 저장
            medicineArray[serialNo] = cursor.getString(2);
            serialNo++;
        }

        /*약 목록을 리스트뷰에 출력*/
        ArrayList<String> mediclist=new ArrayList<>(Arrays.asList(medicineArray));
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mediclist);
        medicationListView.setAdapter(adapter);

        //ScrollView 안에서 리스트뷰를 스크롤 할 수 있도록 설정
        medicationListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                medicationListView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //약 목록 삭제버튼
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                adapter.notifyDataSetChanged();
                medicationListView.setAdapter(adapter);

                sqlDB = myHelper.getWritableDatabase(); // 의약품 DB를 쓰기 가능으로 불러옴
                /* 로그인한 사용자의 정보를 담고 있는 행을 삭제 */
                String sql = "DELETE FROM medicTBL WHERE uID = '" + userData.getUserID() + "';";
                sqlDB.execSQL(sql);
            }
        });

        forBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.ComForbiddenListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        overBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.DuplicateListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        cursor.close();
        sqlDB.close();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.articleNav);
        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    //home버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //camera 버튼을 누르면 액티비티 화면을 전환시켜준다.
                    case R.id.cameraNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MedicRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //현재 화면에서 보여주는 액티비티
                    case R.id.articleNav:
                        return true;
                    //user 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                }
                return false;
            }
        });
    }
}
