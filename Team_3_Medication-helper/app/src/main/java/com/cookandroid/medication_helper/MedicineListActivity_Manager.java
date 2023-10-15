/****************************
 MedicineListActivity_Manager.java
 작성 팀 : [02-03]
 프로그램명 : Medication Helper
 ***************************/

package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MedicineListActivity_Manager extends AppCompatActivity {
    /* 스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현 */
    @Override
    public void onBackPressed() {
        /* 화면에 나타낼 다이어로그 지정 */
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MedicineListActivity_Manager.this);
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
        setContentView(R.layout.activity_mediclist_manager);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.mediclistbar_custom); // 커스텀 사용할 파일 위치

        ListView medicineListView = findViewById(R.id.medicineList);

        /* 사용자 목록을 리스트뷰에 출력 */
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MedicineList");
        ArrayList<String> medicineList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicineList);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String value = ds.getKey(); // 약품 목록을 가져와
                    medicineList.add(value); // 리스트에 저장
                }

                medicineListView.setAdapter(adapter); // 리스트뷰 값 지정
            }

            /* 에러 처리 */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        /* ScrollView 안에서 리스트뷰를 스크롤 할 수 있도록 설정 */
        medicineListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                medicineListView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        /* 약 상세 정보 확인을 위한 함수 */
        medicineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(position); // 어떤 약의 정보를 확인할 것인지 약품 이름 저장

                Intent intent = new Intent(MedicineListActivity_Manager.this, MedicineDetailActivity.class); // 약품 상세정보 확인 화면으로 이동하는 기능
                intent.putExtra("selectedMedicine", selectedItem); // 약품 이름 전달
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                startActivity(intent); // 실행
                finish(); // Progress 완전 종료
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav_manager);
        bottomNavigationView.setSelectedItemId(R.id.medicineListNav);
        /* 바텀 네비게이션을 나타나게 해주는 함수 */
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    /* 차트 확인 화면으로 전환 */
                    case R.id.homeNav_manager:
                        startActivity(new Intent(getApplicationContext(), MainPageActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 사용자 목록 화면으로 전환 */
                    case R.id.userListNav:
                        startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 현재 화면에서 보여주는 액티비티 */
                    case R.id.medicineListNav:
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
