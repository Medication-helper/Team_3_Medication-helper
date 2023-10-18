/****************************
 SideEffectDetailActivity.java
 작성 팀 : [02-03]
 프로그램명 : Medication Helper
 ***************************/

package com.cookandroid.medication_helper;

import static com.cookandroid.medication_helper.FirebaseUtils.updateSideCount;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    Button btnOk, btnSideDelete;
    int comCount, dupCount, pregCount;

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

        selectedmName = findViewById(R.id.SelectedmName_S);
        selectedmEffect = findViewById(R.id.SelectedmEffect_S);
        selectedsEffect = findViewById(R.id.SelectedsEffect);
        selectedpForbid = findViewById(R.id.SelectedpForbid);

        btnOk = findViewById(R.id.btnOk);
        btnSideDelete = findViewById(R.id.btnSideDelete);

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
                    if (snapshot.child("mEffect").exists()) {  // 효능이 있다면
                        selectedmEffect.setText(snapshot.child("mEffect").getValue(String.class));
                        dupCount = 1;} // 효능 출력
                    if (snapshot.child("cForbid").exists()) { // 병용금기가 있다면
                        selectedsEffect.setText(snapshot.child("cForbid").getValue(String.class));
                        comCount = 1;} // 병용금기 출력
                    if (snapshot.child("pForbid").exists()) { // 임부금기가 있다면
                        selectedpForbid.setText(snapshot.child("pForbid").getValue(String.class));
                        pregCount = 1;} // 임부금기 출력
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

        btnSideDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SideEffectDetailActivity.this)
                        .setTitle("경고")
                        .setMessage("정말 이 약품의 부작용 정보를 부작용DB에서 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() { // 예 버튼을 누를경우
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override // DB에서 부작용 삭제
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ref.removeValue();
                                        updateSideCount("효능중복", -dupCount); // 중복효능 사항이 있는 데이터가 하나 줄었음을 기록
                                        updateSideCount("병용금기", -comCount); // 병용금기 사항이 있는 데이터가 하나 줄었음을 기록
                                        updateSideCount("임부금기", -pregCount); // 임부금기 사항이 있는 데이터가 하나 줄었음을 기록
                                        Toast.makeText(getApplicationContext(), "해당 약물 부작용이 DB에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), SideEffectListActivity.class)); // 사용자 목록 화면으로 돌려보냄
                                        finish(); // Progress 완전 종료
                                    }

                                    @Override // 에러 처리
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("아니오", null) // 아니오 버튼을 누르면 아무 일도 일어나지 않음
                        .setIcon(android.R.drawable.ic_dialog_alert) // 다이어로그에 아이콘 설정
                        .show(); // 다이어로그 표시
            }
        });
    }
}
