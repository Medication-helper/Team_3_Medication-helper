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

public class MedicineDetailActivity extends AppCompatActivity {
    EditText selectedmName, selectedcName, selectedmEffect;
    ImageView selectedmIMG;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(MedicineDetailActivity.this, MedicineListActivity_Manager.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_medicdetail);

        Intent selectedMedicine = getIntent();
        String medicine = selectedMedicine.getStringExtra("selectedMedicine");

        selectedmName = (EditText) findViewById(R.id.SelectedmName);
        selectedcName = (EditText) findViewById(R.id.SelectedcName);
        selectedmEffect = (EditText) findViewById(R.id.SelectedmEffect);
        selectedmIMG = (ImageView) findViewById(R.id.selectedmIMG);

        selectedmName.setEnabled(false);
        selectedcName.setEnabled(false);
        selectedmEffect.setEnabled(false);

        selectedmName.setBackground(null);
        selectedcName.setBackground(null);
        selectedmEffect.setBackground(null);

        selectedmName.setText(medicine);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MedicineList").child(medicine);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    selectedcName.setText(snapshot.child("cName").getValue(String.class));
                    selectedmEffect.setText(snapshot.child("mEffect").getValue(String.class));
                    String imageURL = snapshot.child("mIMG").getValue(String.class);
                    Picasso.get().load(imageURL).into(selectedmIMG);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
