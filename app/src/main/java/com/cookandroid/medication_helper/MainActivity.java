package com.cookandroid.medication_helper;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
        setContentView(R.layout.main);
        setTitle("Medication Helper");

        Button btnUserReg = findViewById(R.id.btnUserReg);
        Button btnUserDel = findViewById(R.id.btnUserDel);
        Button btnMediReg = findViewById(R.id.btnMediReg);
        Button btnMediCheck = findViewById(R.id.btnMediCheck);
        Button btnPage = findViewById(R.id.btnPage);
        Button btnAlarmSet = findViewById(R.id.btnAlarmset);

        btnUserReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userRegIntent = new Intent(MainActivity.this, userRegister.class);
                startActivity(userRegIntent);
                finish();
            }
        });

        btnUserDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent delIntent = new Intent(MainActivity.this, userList.class);
                startActivity(delIntent);
                finish();
            }
        });

        btnMediReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediRegIntent = new Intent(MainActivity.this, medicRegister.class);
                startActivity(mediRegIntent);
                finish();
            }
        });

        btnMediCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediCheckIntent = new Intent(MainActivity.this, medicCheck.class);
                startActivity(mediCheckIntent);
                finish();
            }
        });

        btnAlarmSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarmSetIntent=new Intent(MainActivity.this,dateRegister.class);
                startActivity(alarmSetIntent);
                finish();
            }
        });

        btnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pageIntent = new Intent(MainActivity.this, hompage.class);
                startActivity(pageIntent);
                finish();
            }
        });
    }
}