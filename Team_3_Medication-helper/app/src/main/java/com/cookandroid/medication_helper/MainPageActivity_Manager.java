/****************************
 MainPageActivity.java
 작성 팀 : Hello World!
 주 작성자 : 송승우
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity_Manager extends AppCompatActivity{ //implements MapView.MapViewEventListener {
    //뒤로가기 누르면 앱종료시키는 함수
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainPageActivity_Manager.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        //네를 누르면 앱 종료
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
                //아니오 누르면 다이어로그를 종료
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog exitDialog = exitDialogBuilder.create();
        exitDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main_manager);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav_manager);
        bottomNavigationView.setSelectedItemId(R.id.homeNav_manager);
        BarChart chart = (BarChart) findViewById(R.id.chart);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MedicineUsage");

        String [][] medicUsageList = new String[2][];

        ArrayList<ArrayList<String>> usageList = new ArrayList<>();
        usageList.add(new ArrayList<>());
        usageList.add(new ArrayList<>());

        ArrayList<BarEntry> entries = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String medicine = ds.getKey();
                    usageList.get(0).add(medicine);
                    Long usagelong = ds.getValue(Long.class);
                    String usage = String.valueOf(usagelong);
                    usageList.get(1).add(usage);
                    System.out.println("size : " + usageList);
                }

                for (int i = 0; i < usageList.get(1).size(); i++) {
                    float value = Float.parseFloat(usageList.get(1).get(i));
                    entries.add(new BarEntry(i, value));
                    System.out.println(entries);
                }

                BarDataSet dataSet = new BarDataSet(entries, "복용중인 사용자 수");
                BarData barData = new BarData(dataSet);
                chart.setData(barData);

                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(usageList.get(0)));
                xAxis.setGranularity(1);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeNav_manager:
                        return true;
                    case R.id.userListNav:
                        startActivity(new Intent(getApplicationContext(), UserListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.medicineListNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.userNav_manager:
                        startActivity(new Intent(getApplicationContext(), MyPageActivity_Manager.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
}