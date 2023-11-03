/****************************
 MainPageActivity_Manager.java
 작성 팀 : [02-03]
 프로그램명 : Medication Helper
 설명 : 관리자용 메인 액티비티. DB에 등록되어있는 약품들에 대한 부작용 통게 시각화 화면을 제공합니다.
 ***************************/

package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity_Manager extends AppCompatActivity{
    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        /* 화면에 나타낼 다이어로그 지정 */
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainPageActivity_Manager.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        /* 네를 누르면 앱 종료 */
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
                /* 아니오를 누르면 다이어로그를 종료 */
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog exitDialog = exitDialogBuilder.create();
        exitDialog.show(); // 다이어로그 출력
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_manager);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.managermain_titlebar); // 커스텀 사용할 파일 위치

        BarChart chart = findViewById(R.id.chart);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SideCount"); // Firebase의 금기 약품 갯수 DB와 연동

        /* DB에서 값을 가져올 2차원 동적 배열 선언 */
        ArrayList<ArrayList<String>> usageList = new ArrayList<>();
        usageList.add(new ArrayList<>());
        usageList.add(new ArrayList<>());

        /* 차트의 값으로 사용될 동적 배열 선언 */
        ArrayList<BarEntry> entries = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String sideName = ds.getKey(); // 부작용 목록을 가져옴
                    usageList.get(0).add(sideName); // 배열에 저장
                    Long usagelong = ds.getValue(Long.class); // 해당 부작용이 있는 약품의 개수를 가져옴
                    String usage = String.valueOf(usagelong); // 배열에 저장하고자 String 배열로 변환
                    usageList.get(1).add(usage); // 배열에 저장
                }

                for (int i = 0; i < usageList.get(1).size(); i++) { // usageList에 저장된 값들을
                    float value = Float.parseFloat(usageList.get(1).get(i));  // 차트에 사용하고자 float로 변환
                    entries.add(new BarEntry(i, value)); // 차트 값 추가
                }

                BarDataSet dataSet = new BarDataSet(entries, "부작용 약품 수"); // 데이터셋 및 이름 설정
                BarData barData = new BarData(dataSet); // 데이터셋 지정
                chart.setData(barData); // 차트 데이터를 barData로 지정

                /* X축 제목 설정 */
                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(usageList.get(0)));
                xAxis.setGranularity(1);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                chart.invalidate(); // 표시
            }

            /* 에러 처리 */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav_manager);
        bottomNavigationView.setSelectedItemId(R.id.homeNav_manager);
        /* 바텀 네비게이션을 나타나게 해주는 함수 */
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    /* 현재 화면에서 보여주는 액티비티 */
                    case R.id.homeNav_manager:
                        return true;
                    /* 사용자 목록 화면으로 전환 */
                    case R.id.userListNav:
                        startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 약품 목록 화면으로 전환 */
                    case R.id.medicineListNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 부작용 목록 화면으로 전환 */
                    case R.id.sideEffectListNav:
                        startActivity(new Intent(getApplicationContext(), SideEffectListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 마이페이지 화면으로 전환 */
                    case R.id.userNav_manager:
                        startActivity(new Intent(getApplicationContext(), MyPageActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
}