package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SideEffectDetailActivity extends AppCompatActivity {
    EditText selectedmName, selectedcomponent, selectedmEffect, selectedsEffect, selectedpForbid;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(SideEffectDetailActivity.this, SideEffectListActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sidedetail);

        Intent selectedMedicine = getIntent();
        String medicine = selectedMedicine.getStringExtra("selectedMedicine");

        selectedmName = (EditText) findViewById(R.id.SelectedmName_S);
        selectedcomponent = (EditText) findViewById(R.id.Selectedcomponent);
        selectedmEffect = (EditText) findViewById(R.id.SelectedmEffect_S);
        selectedsEffect = (EditText) findViewById(R.id.SelectedsEffect);
        selectedpForbid = (EditText) findViewById(R.id.SelectedpForbid);

        selectedmName.setEnabled(false);
        selectedcomponent.setEnabled(false);
        selectedmEffect.setEnabled(false);
        selectedsEffect.setEnabled(false);
        selectedpForbid.setEnabled(false);

        selectedmName.setBackground(null);
        selectedcomponent.setBackground(null);
        selectedmEffect.setBackground(null);
        selectedsEffect.setBackground(null);
        selectedpForbid.setBackground(null);

        selectedmName.setText(medicine);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SideEffect").child(medicine);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("cName").exists())
                        selectedcomponent.setText(snapshot.child("cName").getValue(String.class));
                    if (snapshot.child("mEffect").exists())
                        selectedmEffect.setText(snapshot.child("mEffect").getValue(String.class));
                    if (snapshot.child("cForbid").exists())
                        selectedsEffect.setText(snapshot.child("cForbid").getValue(String.class));
                    if (snapshot.child("pForbid").exists())
                        selectedpForbid.setText(snapshot.child("pForbid").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
