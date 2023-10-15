/****************************
 MainActivity.java
 작성 팀 : [02-03]
 프로그램명 : Medication-Helper
 ***************************/

package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends FragmentActivity {
    UserData userData;
    private final String PREF_NAME = "autoLogin";
    private SharedPreferences autoLogin;

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        /* 화면에 나타낼 다이어로그 지정 */
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
        setContentView(R.layout.activity_login);

        userData = (com.cookandroid.medication_helper.UserData) getApplicationContext();
        EditText edtID = findViewById(R.id.editID);
        EditText edtPW = findViewById(R.id.editPW);

        Button btnlogin = findViewById(R.id.btnlogin);
        TextView btnsignin = findViewById(R.id.btnsignin);

        autoLogin = getSharedPreferences(PREF_NAME, MODE_PRIVATE); //  자동 로그인에 사용될 ID와 PW가 저장되어 있음
        String loginID = autoLogin.getString("id", ""); // 자동 로그인에 사용될 ID를 가져옴
        String loginPW = autoLogin.getString("pw", ""); // 자동 로그인에 사용될 PW를 가져옴

        /* 자동 로그인 관련 정보가 있다면 자동으로 로그인 수행 */
        if (!loginID.isEmpty() && !loginPW.isEmpty()) {
            login(loginID, loginPW);
        }

        /* 로그인 버튼을 누르면 입력된 ID와 PW를 바탕으로 로그인 기능 수행 */
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(edtID.getText().toString(), edtPW.getText().toString());
            }
        });

        /* 회원가입 버튼을 누를 시 작동하는 기능 */
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userReIntent = new Intent(MainActivity.this, UserRegisterActivity.class); // 회원가입 화면으로 이동하는 기능
                userReIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                startActivity(userReIntent); // 실행
                finish(); // Progress 완전 종료
            }
        });
    }

    /* 로그인 기능 */
    private void login(String userID, String userPW) {
        CheckBox checkBox = findViewById(R.id.autoLogin);

        if (userID.isEmpty()) { // ID가 공백이라면
            Toast.makeText(getApplicationContext(), "ID를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(userID); // Firebase의 사용자 정보 DB와 연동
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) { // ID가 존재하고
                        if(userPW.equals(snapshot.child("uPW").getValue(String.class))) { // 비밀번호가 일치한다면
                            Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show(); // 안내 문구 표시

                            /* 자동 로그인이 체크되어 있을 경우 자동 로그인용 autoLogin에 ID와 PW 저장 */
                            if (checkBox.isChecked()) {
                                SharedPreferences.Editor editor = autoLogin.edit();
                                editor.putString("id", userID);
                                editor.putString("pw", userPW);
                                editor.apply();
                            }

                            /* userData에 로그인한 사용자 정보 저장 */
                            userData.setUserID(userID);
                            userData.setUserPassWord(userPW);
                            userData.setUserName(snapshot.child("uName").getValue(String.class));
                            userData.setUserBirth(snapshot.child("birthDate").getValue(String.class));
                            userData.setUserGender(snapshot.child("uGender").getValue(String.class));
                            userData.setTag(Integer.parseInt(snapshot.child("tag").getValue(String.class))); // 사용자 유형

                            if (userData.getTag() == 0) { // 로그인한 사용자가 일반 사용자라면
                                Intent intent = new Intent(MainActivity.this, MainPageActivity.class); // 사용자용 메인화면으로 이동하는 기능
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                                startActivity(intent); // 실행
                                finish(); // Progress 완전 종료
                            } else { // 로그인한 사용자가 관리자라면
                                Intent intent = new Intent(MainActivity.this, MainPageActivity_Manager.class); // 관리자용 메인화면으로 이동하는 기능
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                                startActivity(intent);// 실행
                                finish();// Progress 완전 종료
                            }
                        }
                        else // 비밀번호가 틀리다면
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else // DB에 저장된 ID가 없다면
                        Toast.makeText(getApplicationContext(), "ID가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                /* 에러 처리 */
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}