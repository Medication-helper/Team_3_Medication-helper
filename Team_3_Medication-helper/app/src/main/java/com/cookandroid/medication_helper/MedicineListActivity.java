/****************************
 MedicineListActivity.java
 작성 팀 : [02-03]
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

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        /* 화면에 나타낼 다이어로그 지정 */
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MedicineListActivity.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        /* 네를 누르면 앱 종료 */
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
                /* 아니오를 누르면 다이어로그를 종료 */
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog exitDialog = exitDialogBuilder.create();
        exitDialog.show(); // 다이어로그 출력
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_medicinelist);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.medilisttitlebar_custom); // 커스텀 사용할 파일 위치

        Button forBtn = findViewById(R.id.forbtn); // 병용금기약물 항목 이동 버튼
        Button prgBtn = findViewById(R.id.prgbtn); // 임부금기약물 항목 이동 버튼
        Button overBtn = findViewById(R.id.overbtn); // 효능중복약물 항목 이동 버튼
        Button delBtn = findViewById(R.id.btnalldelete); // 약품 삭제 기능 활성화 버튼

        userData = (UserData) getApplicationContext();

        ListView medicationListView = findViewById(R.id.medicationlist);

        /* 약 목록을 리스트뷰에 출력 */
        DatabaseReference showRef = FirebaseDatabase.getInstance().getReference("Medicine"); // Firebase의 복용 중인 약 DB와 연동
        ArrayList<String> medicList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicList);

        showRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child(userData.getUserID()).getChildren()) {
                    String value = ds.getKey(); // 사용자가 복용중인 약 이름을 가져와
                    medicList.add(value); // 리스트에 저장
                }

                medicationListView.setAdapter(adapter);
            }

            /* 에러 처리 */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        /* ScrollView 안에서 리스트뷰를 스크롤 할 수 있도록 설정 */
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
                String selectedItem = (String) adapterView.getItemAtPosition(position); // ScrollView에서 선택한 약품명
                DatabaseReference deleteRef = showRef.child(userData.getUserID()).child(selectedItem); // DB 삭제를 위한 Firebase 연동
                DatabaseReference detailRef = FirebaseDatabase.getInstance().getReference("MedicineList").child(selectedItem); // 상세정보 출력을 위한 Firebase 연동

                if (isDeleteMode) { // 삭제 기능이 동작 중일 경우
                    /* 다이어로그를 띄워 삭제할지 여부 선택 */
                    new AlertDialog.Builder(MedicineListActivity.this)
                            .setTitle("약품 삭제")
                            .setMessage(selectedItem + " 약품을 정말로 삭제하시겠습니까?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() { // 예 버튼을 누를경우
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override // DB에서 약품 삭제
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            deleteRef.removeValue();
                                            Toast.makeText(getApplicationContext(), "해당 약품이 목록에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                        @Override // 에러 처리
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    medicList.remove(position); // 리스트뷰에서 삭제한 약품 삭제
                                    adapter.notifyDataSetChanged(); // 삭제된 리스트뷰 적용
                                }
                            })
                            .setNegativeButton(android.R.string.no, null) // 아니오 버튼을 누르면 아무 일도 일어나지 않음
                            .show(); // 표시
                } else { // 삭제 기능이 동작 중이지 않을 경우 (= 상세정보 출력 기능이 동작 중일 경우)
                    detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        Toast.makeText(getApplicationContext(), "존재하지 않는 약품입니다.", Toast.LENGTH_SHORT).show(); // 약품이 DB에 없다면
                                    }
                                    else { // 약품이 DB에 있다면
                                        /* DB에서 선택한 약물 이름 가져오기 */
                                        String imageURL = snapshot.child("mIMG").getValue().toString();
                                        ImageView imageView = new ImageView(MedicineListActivity.this);
                                        Picasso.get().load(imageURL).into(imageView);

                                        String company = snapshot.child("cName").getValue().toString();
                                        String effect = snapshot.child("mEffect").getValue().toString();

                                        showCustomDialog(selectedItem,company,effect,imageURL);
                                    }
                                }

                                /* 에러 처리 */
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        /* 에러 처리 */
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        /* 약 목록 삭제버튼 */
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDeleteMode = !isDeleteMode;
                if (isDeleteMode) { // 삭제 기능이 동작중일 경우
                    Toast.makeText(getApplicationContext(),"삭제할 약품을 선택해주세요.",Toast.LENGTH_SHORT).show(); // 삭제 기능임을 안내한 후
                    delBtn.setText("약 목록 삭제 취소"); // 버튼 제목 변경
                }
                else { // 그렇지 않을 경우
                    delBtn.setText("약 목록 삭제"); // 버튼 제목 변경
                }
            }
        });

        /* 각자 버튼에 맞는 화면으로 이동 */
        /* 병용 금기 약품 조회 화면으로 이동 */
        forBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.ComForbiddenListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        /* 임부 금기 약품 조회 화면으로 이동 */
        prgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.PregnantForbiddenListActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
        /* 중복 효능 약품 조회 화면으로 이동 */
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
        /* 바텀 네비게이션을 나타나게 해주는 함수 */
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    /* 지도 화면으로 전환 */
                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 촬영 화면으로 전환 */
                    case R.id.cameraNav:
                        startActivity(new Intent(getApplicationContext(), com.cookandroid.medication_helper.MedicRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    /* 현재 페이지에서 보여주는 액티비티 */
                    case R.id.articleNav:
                        return true;
                    /* 마이페이지 화면으로 전환 */
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
