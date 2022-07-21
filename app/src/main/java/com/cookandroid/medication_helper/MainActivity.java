/****************************
 MainActivity.java
 작성 팀 : 3분카레
 주 작성자 : 신윤호
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    UserDBHelper myHelper;
    SQLiteDatabase sqlDB;
    UserData userData;

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
        setTitle("Medication Helper");

        userData = (UserData)getApplicationContext();
        EditText edtID = findViewById(R.id.editID);
        EditText edtPW = findViewById(R.id.editPW);

        Button btnlogin = findViewById(R.id.btnlogin);
        Button btnsignin = findViewById(R.id.btnsignin);

        myHelper = new UserDBHelper(this);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM userTBL;", null);
                Boolean checkID = false;
                Boolean checkPW = false;
                int position = 0;

                while (cursor.moveToNext()) {
                    if ((cursor.getString(0)).equals(edtID.getText().toString())) {
                        checkID = true;
                        if ((cursor.getString(1)).equals(edtPW.getText().toString())){
                            checkPW = true;
                            break;
                        }
                        break;
                    }
                    position++;
                }

                if (checkID == false) {
                    Toast.makeText(getApplicationContext(), "등록된 ID가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else if (checkPW == false) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "로그인 완료", Toast.LENGTH_SHORT).show();
                    cursor.moveToPosition(position);
                    userData.setUserID(cursor.getString(0));
                    userData.setUserPassWord(cursor.getString(1));
                    userData.setUserNickName(cursor.getString(2));
                    userData.setUserBirth(cursor.getString(3));
                    userData.setUserGender(cursor.getString(4));
                    Intent mainIntent = new Intent(MainActivity.this, MainPageActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userReIntent = new Intent(MainActivity.this, UserRegisterActivity.class);
                startActivity(userReIntent);
                finish();
            }
        });
    }
}