package com.cookandroid.medication_helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicineListActivity extends AppCompatActivity {
    Button delBtn;
    ListView medicationListView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(MedicineListActivity.this, MedicCheckActivity.class);
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Back);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicinelist);
        setTitle("Medication Helper");

        medicationListView=(ListView)findViewById(R.id.medicationlist);
        delBtn=(Button)findViewById(R.id.btndelete);

        ArrayList<String> medicineArraylist=new ArrayList<>();
        String[] medicineArray;//DB에서 받아온 처방약 목록을 저장하는 String 배열

        ArrayList<String> mediclist=new ArrayList<>(Arrays.asList(medicineArray));

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mediclist);

        medicationListView.setAdapter(adapter);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                adapter.notifyDataSetChanged();
                medicationListView.setAdapter(adapter);

            }
        });
    }
}
