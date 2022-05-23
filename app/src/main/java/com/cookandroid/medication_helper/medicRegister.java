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

        ListView mediclist=(ListView)findViewById(R.id.MedicineList);//목록 출력
        EditText medicName=(EditText) findViewById(R.id.medicedittext);//약을 입력하는 부분
        Button medicaddbtn = (Button) findViewById(R.id.addmedicbtn);//입력버튼
        Button medicdelbtn =(Button) findViewById(R.id.delmedicbtn);//삭제버튼
        Button backbtn = (Button) findViewById(R.id.backtoMain);//메인메뉴버튼
        Button medicregibtn=(Button) findViewById(R.id.regimedicbtn);//약 목록 등록 버튼

        //약 목록 데이터를 가지는 String 배열 medicinelist
        List<String> medicinelist=new ArrayList<>();

        //어댑터 객체 생성
        ArrayAdapter<String> adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, medicinelist);

        //리스트뷰와 어댑터 연결
        mediclist.setAdapter(adpater);

        //하나의 항목을 선택할 수 있도록 설정
        mediclist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //메인메뉴 복귀 버튼
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(BackToMain);
            }
        });
        
        //약을 목록에 추가하는 버튼
        medicaddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=medicName.getText().toString();
                medicinelist.add(name);
                adpater.notifyDataSetChanged();
                medicName.setText("");

            }
        });
    
        //선택한 약을 목록에서 삭제하는 버튼
        medicdelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=mediclist.getCheckedItemPosition();

                medicinelist.remove(pos);
                adpater.notifyDataSetChanged();
                mediclist.clearChoices();
            }
        });
    }
}
