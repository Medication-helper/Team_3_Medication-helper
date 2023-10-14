/****************************
 MedicineListActivity.java
 작성 팀 : Hello World!
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MedicineListActivity extends AppCompatActivity {
    /* 의약품 DB를 사용하기 위한 변수들 */
    UserData userData;
    boolean isDeleteMode = false;
    Dialog dialog;

    /*스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현*/
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MedicineListActivity.this);
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
        setContentView(R.layout.activity_medicinelist);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.medilisttitlebar_custom); // 커스텀 사용할 파일 위치

        Button forBtn = findViewById(R.id.forbtn);//병용금기약물 항목 이동 버튼
        Button prgBtn = findViewById(R.id.prgbtn);//임부금기약물 항목 이동 버튼
        Button overBtn = findViewById(R.id.overbtn);//효능중복약물 항목 이동 버튼
        Button delBtn = findViewById(R.id.btnalldelete);

        userData = (UserData) getApplicationContext();

        ListView medicationListView = findViewById(R.id.medicationlist);

        /*약 목록을 리스트뷰에 출력*/
        DatabaseReference showRef = FirebaseDatabase.getInstance().getReference("Medicine");
        ArrayList<String> medicList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicList);

        showRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child(userData.getUserID()).getChildren()) {
                    String value = ds.getKey();
                    medicList.add(value);
                }

                medicationListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        //ScrollView 안에서 리스트뷰를 스크롤 할 수 있도록 설정
        medicationListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                medicationListView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        medicationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(position);
                DatabaseReference deleteRef = showRef.child(userData.getUserID()).child(selectedItem);
                DatabaseReference detailRef = FirebaseDatabase.getInstance().getReference("MedicineList").child(selectedItem);

                if (isDeleteMode) {
                    new AlertDialog.Builder(MedicineListActivity.this)
                            .setTitle("약품 삭제")
                            .setMessage(selectedItem + " 약품을 정말로 삭제하시겠습니까?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            deleteRef.removeValue();
                                            Toast.makeText(getApplicationContext(), "해당 약품이 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    medicList.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                } else {
                    detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        Toast.makeText(getApplicationContext(), "존재하지 않는 약품입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        //DB에서 선택한 약물 이름 뜯어오기
                                        String imageURL = snapshot.child("mIMG").getValue().toString();
                                        ImageView imageView = new ImageView(MedicineListActivity.this);
                                        Picasso.get().load(imageURL).into(imageView);
                                        String message = "약품명 : " + selectedItem + "\n" +
                                                "제조사명 : " + snapshot.child("cName").getValue() + "\n" +
                                                "효능 : " + snapshot.child("mEffect").getValue() + "\n";

                                        String company = snapshot.child("cName").getValue().toString();
                                        String effect = snapshot.child("mEffect").getValue().toString();

                                        showCustomDialog(selectedItem,company,effect,imageURL);

//                                        new AlertDialog.Builder(MedicineListActivity.this)
//                                                .setTitle("약품 정보")
//                                                .setView(imageView)
//                                                .setMessage(message)
//                                                .setPositiveButton(android.R.string.yes, null)
//                                                .show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //약 목록 삭제버튼
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDeleteMode = !isDeleteMode;
                if (isDeleteMode) {
                    Toast.makeText(getApplicationContext(),"삭제할 약품을 선택해주세요.",Toast.LENGTH_SHORT).show();
                    delBtn.setText("약 목록 삭제 취소");
                }
                else {
                    delBtn.setText("약 목록 삭제");
                }
            }
        });

        forBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.ComForbiddenListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        prgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.PregnantForbiddenListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        overBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.DuplicateListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.articleNav);
        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    //home버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //camera 버튼을 누르면 액티비티 화면을 전환시켜준다.
                    case R.id.cameraNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MedicRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //현재 화면에서 보여주는 액티비티
                    case R.id.articleNav:
                        return true;
                    //user 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                }
                return false;
            }
        });
    }

    private void showCustomDialog(String medicname, String company, String Effect, String ImageURL){

        AlertDialog.Builder builder = new AlertDialog.Builder(MedicineListActivity.this,R.style.AlertDialogTheme);
        View view=LayoutInflater.from(MedicineListActivity.this).inflate(R.layout.custom_diaolog,(LinearLayout)findViewById(R.id.medicinfodialog));

        builder.setView(view);
        ((TextView)view.findViewById(R.id.infoname)).setText(medicname);
        ((TextView)view.findViewById(R.id.infocompany)).setText(company);
        ((TextView)view.findViewById(R.id.infoeffect)).setText(Effect);
        ImageView medicPic=view.findViewById(R.id.infopic);
        Picasso.get().load(ImageURL).into(medicPic);


        AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        alertDialog.show();


    }
}
