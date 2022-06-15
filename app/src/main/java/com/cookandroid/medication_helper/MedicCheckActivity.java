package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("deprecation")
public class MedicCheckActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {  // 하단의 뒤로가기 버튼을 눌렀을 시 동작
        super.onBackPressed();
        Intent BackToMain = new Intent(MedicCheckActivity.this, MainPageActivity.class); // 메인화면으로 돌아가는 기능
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 복약 정보 조회 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediccheck);
        setTitle("Medication Helper");

        Button btnMedilist = findViewById(R.id.btnMedilist);
        Button btnFBcom = findViewById(R.id.btnFBcom);
        Button btnFBpreg = findViewById(R.id.btnFBpreg);
        Button btnFBage = findViewById(R.id.btnFBage);
        Button btnMainPage= findViewById(R.id.btnback5);

        btnMedilist.setOnClickListener(new View.OnClickListener() { // 클릭 시 약 목록 조회 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, MedicineListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFBcom.setOnClickListener(new View.OnClickListener() { // 클릭 시 병용 금지 약물 조회 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, ComForbiddenListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFBpreg.setOnClickListener(new View.OnClickListener() { // 클릭 시 임부 금지 약물 조회 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, PregnantForbiddenListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFBage.setOnClickListener(new View.OnClickListener() { // 클릭 시 효능 중복 약물 조회 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicCheckActivity.this, DuplicateListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMainPage.setOnClickListener(new View.OnClickListener() { // 클릭 시 메인 페이지로 이동
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(MedicCheckActivity.this, MainPageActivity.class);
                BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BackToMain);
            }
        });

    }
}