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

public class UserListActivity extends AppCompatActivity {
    /*스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현*/
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(UserListActivity.this);
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
        setContentView(R.layout.activity_userlist);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.userlistbar_custom); // 커스텀 사용할 파일 위치

        ListView userListView = findViewById(R.id.userList);

        /*사용자 목록을 리스트뷰에 출력*/
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ArrayList<String> userList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String value = ds.getKey();
                    System.out.println("Data : " + value);
                    userList.add(value);
                }

                userListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        //ScrollView 안에서 리스트뷰를 스크롤 할 수 있도록 설정
        userListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                userListView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(UserListActivity.this, com.cookandroid.medication_helper.UserDetailActivity.class);
                intent.putExtra("selectedUser", selectedItem);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav_manager);
        bottomNavigationView.setSelectedItemId(R.id.userListNav);
        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homeNav_manager:
                        startActivity(new Intent(getApplicationContext(), MainPageActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.userListNav:
                        return true;
                    case R.id.medicineListNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.sideEffectListNav:
                        startActivity(new Intent(getApplicationContext(), SideEffectListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.userNav_manager:
                        startActivity(new Intent(getApplicationContext(), MyPageActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                }
                return false;
            }
        });
    }
}
