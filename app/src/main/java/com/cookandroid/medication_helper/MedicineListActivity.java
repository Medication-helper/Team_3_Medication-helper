/****************************
 MedicineListActivity.java
 작성 팀 : 3분카레
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicineListActivity extends AppCompatActivity {
    Button delBtn;
    ListView medicationListView;
    Button btnBack;

    /* 의약품 DB를 사용하기 위한 변수들 */
    UserData userData;
    MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    /*스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(MedicineListActivity.this, MedicCheckActivity.class);
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinelist);
        setTitle("Medication Helper");

        userData = (UserData) getApplicationContext();
        myHelper = new MedicDBHelper(this);
        sqlDB = myHelper.getReadableDatabase(); // 의약품 DB를 읽기 전용으로 불러옴
        // 현재 로그인중인 사용자가 복용중인 의약품의 DB를 읽어들임
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM medicTBL WHERE uID = '" + userData.getUserID() + "';", null);

        medicationListView=(ListView)findViewById(R.id.medicationlist);
        delBtn=(Button)findViewById(R.id.btnalldelete);
        btnBack=(Button)findViewById(R.id.back);

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

        // 뒤로 가기 버튼을 눌렀을 경우
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicineListActivity.this, MedicCheckActivity.class); // 이전 화면으로 돌아가는 기능
                startActivity(intent); // 실행
                finish(); // Progress 완전 종료
            }
        });

        cursor.close();
        sqlDB.close();
    }
}
