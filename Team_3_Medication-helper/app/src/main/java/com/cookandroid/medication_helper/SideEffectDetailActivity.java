/****************************
 SideEffectDetailActivity.java
 작성 팀 : [02-03]
 프로그램명 : Medication Helper
 ***************************/

package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SideEffectDetailActivity extends AppCompatActivity {
    EditText selectedmName, selectedmEffect, selectedsEffect, selectedpForbid;
    Button btnOk;

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(SideEffectDetailActivity.this, SideEffectListActivity.class);  // 부작용 목록으로 돌아가는 기능
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 부작용 상세정보 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sidedetail);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /* 어떤 약품의 상세정보를 출력할지를 받아옴 */
        Intent selectedMedicine = getIntent();
        String medicine = selectedMedicine.getStringExtra("selectedMedicine");

        selectedmName = (EditText) findViewById(R.id.SelectedmName_S);
        selectedmEffect = (EditText) findViewById(R.id.SelectedmEffect_S);
        selectedsEffect = (EditText) findViewById(R.id.SelectedsEffect);
        selectedpForbid = (EditText) findViewById(R.id.SelectedpForbid);

        btnOk=findViewById(R.id.btnOk);

        selectedmName.setEnabled(false);
        selectedmEffect.setEnabled(false);
        selectedsEffect.setEnabled(false);
        selectedpForbid.setEnabled(false);

        selectedmName.setBackground(null);
        selectedmEffect.setBackground(null);
        selectedsEffect.setBackground(null);
        selectedpForbid.setBackground(null);

        selectedmName.setText(medicine);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SideEffect").child(medicine); // Firebase의 부작용 DB 연결
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("mEffect").exists())  // 효능이 있다면
                        selectedmEffect.setText(snapshot.child("mEffect").getValue(String.class)); // 효능 출력
                    if (snapshot.child("cForbid").exists()) // 병용금기가 있다면
                        selectedsEffect.setText(snapshot.child("cForbid").getValue(String.class)); // 병용금기 출력
                    if (snapshot.child("pForbid").exists()) // 임부금기가 있다면
                        selectedpForbid.setText(snapshot.child("pForbid").getValue(String.class)); // 임부금기 출력
                }
            }

            /* 에러 처리 */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SideEffectDetailActivity.this, SideEffectListActivity.class); // 부작용 목록으로 돌아가는 기능
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 부작용 상세정보 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                startActivity(intent); // 실행
                finish(); // Progress 완전 종료
            }
        });
    }
}
