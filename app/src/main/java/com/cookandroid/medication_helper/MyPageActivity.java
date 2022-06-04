package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPageActivity extends AppCompatActivity{
    TextView TvName;
    TextView TvBirth;
    TextView TvGender;
    Button BtnLogout;
    Button BtnDel;

    UserData userData;
    UserDBHelper userDBHelper;
    MedicDBHelper medicDBHelper;
    SQLiteDatabase sqlUserDB, sqlMedicDB;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MyPageActivity.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
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
        setContentView(R.layout.activity_my_page);
        setTitle("Medication Helper");

        TvName = (TextView) findViewById(R.id.tvName);
        TvBirth = (TextView) findViewById(R.id.tvBirth);
        TvGender = (TextView) findViewById(R.id.tvGender);
        BtnLogout = (Button) findViewById(R.id.btnLogout);
        BtnDel = (Button) findViewById(R.id.btnDel);

        userData = (UserData) getApplicationContext();
        userDBHelper = new UserDBHelper(this);
        medicDBHelper = new MedicDBHelper(this);
        sqlUserDB = userDBHelper.getWritableDatabase();
        sqlMedicDB = medicDBHelper.getWritableDatabase();

        TvName.setText("아이디 : " + userData.getUserNickName());
        TvBirth.setText("생년월일 : " + userData.getUserBirth());
        TvGender.setText("성별 : " + userData.getUserGender());

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData.setUserID("");
                userData.setUserPassWord("");
                userData.setUserNickName("");
                userData.setUserBirth("");
                userData.setUserGender("");
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        BtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlUserDB.execSQL("DELETE FROM userTBL WHERE uID = '" + userData.getUserID() + "'");
                sqlMedicDB.execSQL("DELETE FROM medicTBL WHERE uID = '" + userData.getUserID() + "'");
                userData.setUserID("");
                userData.setUserPassWord("");
                userData.setUserNickName("");
                userData.setUserBirth("");
                userData.setUserGender("");
                Toast.makeText(getApplicationContext(), "회원탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.userNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.pageNav:
                        startActivity(new Intent(getApplicationContext(), WebActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.userNav:
                        return true;
                }
                return false;
            }
        });
    }
}
