package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDetailActivity extends AppCompatActivity {
    EditText selectedUserID, selectedUserPW, selectedUserName, selectedUserBirth, selectedUserGender, selectedUsertag;

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

        Intent selectedUser = getIntent();
        String userID = selectedUser.getStringExtra("selectedUser");

        selectedUserID = (EditText) findViewById(R.id.SelectedUserID);
        selectedUserPW = (EditText) findViewById(R.id.SelectedUserPW);
        selectedUserName = (EditText) findViewById(R.id.SelectedUserName);
        selectedUserBirth = (EditText) findViewById(R.id.SelectedUserBirth);
        selectedUserGender = (EditText) findViewById(R.id.SelectedUserGender);
        selectedUsertag = (EditText) findViewById(R.id.SelectedUsertag);

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
    }
}
