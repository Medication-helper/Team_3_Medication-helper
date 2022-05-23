package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class medicRegister extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(medicRegister.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicationregister);
        setTitle("Medication Helper");

        ListView mediclist=(ListView)findViewById(R.id.MedicineList);
        EditText medicName=(EditText) findViewById(R.id.medicedittext);
        Button mediceditbtn = (Button) findViewById(R.id.editmedicbtn);
        Button btnBack_medicRegister = (Button) findViewById(R.id.btnBack_medicRegister);

        //약 목록 데이터를 가지는 String 배열 medicinelist
        List<String> medicinelist=new ArrayList<>();

        //어댑터 객체 생성
        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, medicinelist);

        //리스트뷰와 어댑터 연결
        mediclist.setAdapter(adpater);

        //하나의 항목을 설정할 수 있도록 설정
        mediclist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //메인메뉴 복귀 버튼
        btnBack_medicRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(BackToMain);
            }
        });
        
        //약을 목록에 추가하는 버튼
        mediceditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=medicName.getText().toString();
                medicinelist.add(name);
                adpater.notifyDataSetChanged();
                medicName.setText("");

            }
        });
    }
}
