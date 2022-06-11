package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPageActivity extends AppCompatActivity {

    //뒤로가기 누르면 앱종료시키는 함수
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainPageActivity.this);
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
        setContentView(R.layout.activity_main);
        setTitle("Medication Helper");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        Button btnMediReg = findViewById(R.id.btnMediReg);
        Button btnMediCheck = findViewById(R.id.btnMediCheck);
        bottomNavigationView.setSelectedItemId(R.id.homeNav);

        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    //page버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.pageNav:
                        startActivity(new Intent(getApplicationContext(), WebActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //현재 페이지에서 보여주는 액티비티
                    case R.id.homeNav:
                        return true;
                    //user버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        //현재 액티비티에서 MedicRegisterActivity로 넘겨주는 버튼
        btnMediReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediRegIntent = new Intent(MainPageActivity.this, MedicRegisterActivity.class);
                startActivity(mediRegIntent);
            }
        });
        //현재 액티비티에서 MedicCheckActivity로 넘겨주는 버튼
        btnMediCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediCheckIntent = new Intent(MainPageActivity.this, MedicCheckActivity.class);
                startActivity(mediCheckIntent);
            }
        });
        
    }
}