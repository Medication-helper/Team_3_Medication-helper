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

    //뒤로가기 누르면 앱종료시키는 함수
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
        setContentView(R.layout.activity_login);

        userData = (com.cookandroid.medication_helper.UserData) getApplicationContext();
        EditText edtID = findViewById(R.id.editID);
        EditText edtPW = findViewById(R.id.editPW);

        Button btnlogin = findViewById(R.id.btnlogin);
        TextView btnsignin = findViewById(R.id.btnsignin);

        autoLogin = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String loginID = autoLogin.getString("id", "");
        String loginPW = autoLogin.getString("pw", "");

        if (!loginID.isEmpty() && !loginPW.isEmpty()) {
            login(loginID, loginPW);
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(edtID.getText().toString(), edtPW.getText().toString());
            }
        });

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userReIntent = new Intent(MainActivity.this, com.cookandroid.medication_helper.UserRegisterActivity.class);
                userReIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(userReIntent);
                finish();
            }
        });
    }

    private void login(String userID, String userPW) {
        CheckBox checkBox = findViewById(R.id.autoLogin);

        if (userID.isEmpty()) {
            Toast.makeText(getApplicationContext(), "ID를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(userID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if(userPW.equals(snapshot.child("uPW").getValue(String.class))) {
                            Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();

                            // 자동 로그인 구현 부분
                            if (checkBox.isChecked()) {
                                SharedPreferences.Editor editor = autoLogin.edit();
                                editor.putString("id", userID);
                                editor.putString("pw", userPW);
                                editor.apply();
                            }

                            userData.setUserID(userID);
                            userData.setUserPassWord(userPW);
                            userData.setUserNickName(snapshot.child("uName").getValue(String.class));
                            userData.setUserBirth(snapshot.child("birthDate").getValue(String.class));
                            userData.setUserGender(snapshot.child("uGender").getValue(String.class));

                            Intent intent = new Intent(MainActivity.this, com.cookandroid.medication_helper.MainPageActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "ID가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}