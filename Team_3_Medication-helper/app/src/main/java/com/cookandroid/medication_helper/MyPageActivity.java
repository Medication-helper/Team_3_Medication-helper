/****************************
 MyPageActivity.java
 작성 팀 : Hello World!
 프로그램명 : Medication Helper
 ***************************/

package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class MyPageActivity extends AppCompatActivity{
    TextView TvName;
    TextView TvBirth;
    TextView TvGender;
    TextView BtnLogout;
    Button BtnModify;
    Button BtnDel;

    UserData userData;
    private final String PREF_NAME = "autoLogin";
    private SharedPreferences autoLogin;

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        /* 화면에 나타낼 다이어로그 지정 */
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MyPageActivity.this);
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
        setContentView(R.layout.activity_my_page);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.mytitlebar_custom); // 커스텀 사용할 파일 위치

        TvName = (TextView) findViewById(R.id.tvName);
        TvBirth = (TextView) findViewById(R.id.tvBirth);
        TvGender = (TextView) findViewById(R.id.tvGender);
        BtnLogout = (TextView) findViewById(R.id.btnLogout);
        BtnDel = (Button) findViewById(R.id.btnDel);
        BtnModify = (Button) findViewById(R.id.btnModify);

        userData = (UserData) getApplicationContext();
        autoLogin = getSharedPreferences(PREF_NAME, MODE_PRIVATE); // 자동로그인에 사용할 정보

        /* UserData 클래스에서 현재 로그인중인 사용자의 정보를 불러옴 */
        TvName.setText(userData.getUserName());
        TvBirth.setText("생년월일 : " + userData.getUserBirth());
        TvGender.setText("성별 : " + userData.getUserGender());

        BtnLogout.setOnClickListener(new View.OnClickListener() { // 로그아웃 버튼을 눌렀을 때
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = autoLogin.edit(); // 자동로그인 데이터 초기화
                editor.putString("id", "");
                editor.putString("pw", "");
                editor.apply();
                userData.Init(); // UserData의 모든 내용 초기화
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class)); // 로그인 화면으로 돌려보냄
                finish(); // Progress 완전 종료
            }
        });

        BtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, UserModifyActivity.class); // 회원수정 화면으로 이동하는 기능
                intent.putExtra("tag", 1); // 사용자 마이페이지에서 넘어왔음을 전달하는 인텐트
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 마이페이지가 백그라운드에서 돌아가지 않도록 완전종료
                startActivity(intent); // 실행
                finish(); // Progress 완전 종료
            }
        });

        BtnDel.setOnClickListener(new View.OnClickListener() { // 회원탈퇴 버튼을 눌렀을 때
            @Override
            public void onClick(View view) {
                String userID = userData.getUserID();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userID); // Firebase의 회원 DB와 연동
                DatabaseReference medicineRef = FirebaseDatabase.getInstance().getReference("Medicine").child(userID); // Firebase의 약 DB와 연동
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userRef.removeValue(); // 사용자 제거
                        medicineRef.removeValue(); // 사용자가 복용중인 약 제거
                        userData.Init(); // UserData의 모든 내용 초기화
                        Toast.makeText(getApplicationContext(), "회원탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class)); // 로그인 화면으로 돌려보냄
                        finish(); // Progress 완전 종료
                    }

                    /* 에러 처리 */
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.userNav);
        /* 바텀 네비게이션을 나타나게 해주는 함수 */
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    /* 지도 화면으로 전환 */
                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 촬영 화면으로 전환 */
                    case R.id.cameraNav:
                        startActivity(new Intent(getApplicationContext(), MedicRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 복용 약 목록 화면으로 전환 */
                    case R.id.articleNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 현재 화면에서 보여주는 액티비티 */
                    case R.id.userNav:
                        return true;
                }
                return false;
            }
        });
    }
}
