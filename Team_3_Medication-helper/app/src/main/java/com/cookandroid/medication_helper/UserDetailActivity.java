package com.cookandroid.medication_helper;

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

public class UserDetailActivity extends AppCompatActivity {
    EditText selectedUserID, selectedUserPW, selectedUserName, selectedUserBirth, selectedUserGender, selectedUsertag;
    Button btnDetailModify, btnDetailDelete, btnOk;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(UserDetailActivity.this, UserListActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_userdetail);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent selectedUser = getIntent();
        String userID = selectedUser.getStringExtra("selectedUser");

        selectedUserID = (EditText) findViewById(R.id.SelectedmName);
        selectedUserPW = (EditText) findViewById(R.id.SelectedcName);
        selectedUserName = (EditText) findViewById(R.id.SelectedmEffect);
        selectedUserBirth = (EditText) findViewById(R.id.SelectedUserBirth);
        selectedUserGender = (EditText) findViewById(R.id.SelectedUserGender);
        selectedUsertag = (EditText) findViewById(R.id.SelectedUsertag);
        btnDetailModify = (Button) findViewById(R.id.btnDetailModify);
        btnDetailDelete = (Button) findViewById(R.id.btnDetailDelete);
        btnOk = (Button) findViewById(R.id.btnOk);

        selectedUserID.setEnabled(false);
        selectedUserPW.setEnabled(false);
        selectedUserName.setEnabled(false);
        selectedUserBirth.setEnabled(false);
        selectedUserGender.setEnabled(false);
        selectedUsertag.setEnabled(false);

        selectedUserID.setBackground(null);
        selectedUserPW.setBackground(null);
        selectedUserName.setBackground(null);
        selectedUserBirth.setBackground(null);
        selectedUserGender.setBackground(null);
        selectedUsertag.setBackground(null);

        selectedUserID.setText(userID);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    selectedUserPW.setText(snapshot.child("uPW").getValue(String.class));
                    selectedUserName.setText(snapshot.child("uName").getValue(String.class));
                    selectedUserBirth.setText(snapshot.child("birthDate").getValue(String.class));
                    selectedUserGender.setText(snapshot.child("uGender").getValue(String.class));
                    int tag = Integer.parseInt(snapshot.child("tag").getValue(String.class));
                    if (tag == 0) {
                        selectedUsertag.setText("사용자");
                    } else if (tag == 1) {
                        selectedUsertag.setText("관리자");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        btnDetailModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (UserDetailActivity.this, UserModifyActivity.class);
                intent.putExtra("tag", 3);
                intent.putExtra("selectedUser", userID);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnDetailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(UserDetailActivity.this)
                        .setTitle("경고")
                        .setMessage("정말 이 사용자의 정보를 회원DB에서 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ref.removeValue();
                                        Toast.makeText(getApplicationContext(), "해당 사용자가 DB에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), UserListActivity.class)); // 사용자 목록 화면으로 돌려보냄
                                        finish(); // Progress 완전 종료
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailActivity.this, UserListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent); // 실행
                finish(); // Progress 완전 종료
            }
        });
    }
}
