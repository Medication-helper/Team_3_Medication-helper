/****************************
 UserModifyActivity.java
 작성 팀 : [02-03]
 프로그램명 : Medication Helper
 설명 : 사용자 정보 수정 기능입니다.
 ***************************/

package com.cookandroid.medication_helper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserModifyActivity extends AppCompatActivity{
    EditText M_Pass, M_PassCheck, M_Name;
    RadioGroup RG;
    RadioButton RB_Man, RB_Woman;
    Button btnModify_Back, btnBirthChoose, btnComplete;
    TextView M_Birth;
    int dateCheckCounter = 0;
    UserData userData;

    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_usermodify);

        Intent tagintent = getIntent();
        tag = tagintent.getIntExtra("tag", 0); // 어느 화면에서 넘어왔는지를 저장
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.modify_titlebar_custom); // 커스텀 사용할 파일 위치

        M_Pass = (EditText) findViewById(R.id.M_Pass);
        M_PassCheck = (EditText) findViewById(R.id.M_PassCheck);
        M_Name = (EditText) findViewById(R.id.M_name);
        RG = (RadioGroup) findViewById(R.id.RG);
        RB_Man = (RadioButton) findViewById(R.id.RB_man);
        RB_Woman = (RadioButton) findViewById(R.id.RB_woman);
        btnModify_Back = (Button) findViewById(R.id.btnModify_Back);
        btnBirthChoose = (Button) findViewById(R.id.BtnBirthChoose);
        btnComplete = (Button) findViewById(R.id.BtnComplete);
        M_Birth = (TextView) findViewById(R.id.M_Birth);

        userData = (UserData) getApplicationContext();

        /* 오늘 날짜 계산 */
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        int todayYear = Integer.parseInt(yearFormat.format(todayDate));
        int todayMonth = Integer.parseInt(monthFormat.format(todayDate));
        int todayDay = Integer.parseInt(dayFormat.format(todayDate));

        /* 생년월일 선택용 변수 */
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        /* 뒤로 가기 버튼 동작 */
        btnModify_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tag) { // 어느 화면에서 넘어왔는지를 판별
                    case 1: // 일반 사용자 마이페이지에서 넘어옴
                        Intent BackToMain_User = new Intent(UserModifyActivity.this, MyPageActivity.class);
                        BackToMain_User.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(BackToMain_User); // 실행
                        finish(); // Progress 완전 종료
                        break;
                    case 2: // 관리자 마이페이지에서 넘어옴
                        Intent BackToMain_Manager = new Intent(UserModifyActivity.this, MyPageActivity_Manager.class);
                        BackToMain_Manager.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(BackToMain_Manager); // 실행
                        finish(); // Progress 완전 종료
                        break;
                    case 3: // 사용자 상세정보에서 넘어옴
                        Intent userintent = getIntent();
                        Intent BackToMain_Detail = new Intent(UserModifyActivity.this, UserDetailActivity.class);
                        BackToMain_Detail.putExtra("selectedUser", userintent.getStringExtra("selectedUser")); // 어느 사용자인지 전달
                        BackToMain_Detail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(BackToMain_Detail); // 실행
                        finish(); // Progress 완전 종료
                        break;
                }
            }
        });

        /* 생년월일 선택 */
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) { // DatePicker에서 월은 0부터 시작되기 때문에 1을 더해줘야 함
                if (year > todayYear) {
                    dateCheckCounter = 1;
                }
                else if (year == todayYear) {
                    if ((month+1) > todayMonth) {
                        dateCheckCounter = 1;
                    }
                    else if ((month+1) == todayMonth) {
                        if (dayOfMonth > todayDay) {
                            dateCheckCounter = 1;
                        }
                    }
                }

                if (dateCheckCounter == 1) { // 생년월일이 현재 일자보다 미래일 경우
                    Toast.makeText(getApplicationContext(), "미래시입니다.", Toast.LENGTH_SHORT).show();
                    dateCheckCounter = 0; // 카운터 초기화
                }
                else // 정상적으로 생년월일을 입력받은 경우
                    M_Birth.setText(dayOfMonth +"/" + (month + 1) + "/" + year); // 생년월일 선택 버튼에 생년월일을 표시함
            }
        }, mYear, mMonth, mDay);

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnBirthChoose.isClickable()) {
                    datePickerDialog.show(); // 상단의 생년월일 선택 함수 작동
                }
            }
        });

        /* 회원수정 완료 */
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = M_Pass.getText().toString();
                String passwordCheck = M_PassCheck.getText().toString();
                String name = M_Name.getText().toString();
                String birth = M_Birth.getText().toString();
                String gender = "";

                DatabaseReference ref;
                if (tag == 3) { // 사용자 상세정보에서 넘어왔을 경우
                    Intent userintent = getIntent();
                    ref = FirebaseDatabase.getInstance().getReference("User").child(userintent.getStringExtra("selectedUser")); // 선택했던 사용자의 DB를 수정
                } else
                    ref = FirebaseDatabase.getInstance().getReference("User").child(userData.getUserID()); // 로그인한 사용자를 수정
                Map<String, Object> modifys = new HashMap<>();
                int PWCheckCounter = 1;

                switch (RG.getCheckedRadioButtonId()) {
                    case R.id.RB_man:
                        gender = "man";
                        break;
                    case R.id.RB_woman:
                        gender = "woman";
                        break;
                    default:
                        break;
                }

                if (!password.isEmpty()) { // 비밀번호가 수정되었을 경우
                    if (password.equals(passwordCheck) == false) { // 비밀번호랑 확인이 다를 경우
                        PWCheckCounter = 0;
                    }
                    else {
                        PWCheckCounter = 1;
                        modifys.put("uPW", password);
                    }
                }
                if (!name.isEmpty()) {
                    modifys.put("uName", name);
                }
                if (!birth.isEmpty()) {
                    modifys.put("birthDate", birth);
                }
                if (!gender.isEmpty()) {
                    modifys.put("uGender", gender);
                }
                
                if (PWCheckCounter == 0) {
                    Toast.makeText(getApplicationContext(), "비밀번호 확인란에 입력된 내용이 비밀번호와 다릅니다.", Toast.LENGTH_SHORT).show();
                } else {
                    ref.updateChildren(modifys); // DB 수정

                    Toast.makeText(getApplicationContext(), "회원수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    switch (tag) {
                        case 1: // 일반 사용자 마이페이지에서 넘어옴
                            Intent BackToMain_User = new Intent(UserModifyActivity.this, MyPageActivity.class); // 일반 사용자 마이페이지로 돌아가는 기능
                            BackToMain_User.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(BackToMain_User); // 실행
                            finish(); // Progress 완전 종료
                        case 2: // 관리자 마이페이지에서 넘어옴
                            Intent BackToMain_Manager = new Intent(UserModifyActivity.this, MyPageActivity_Manager.class); // 관리자 마이페이지로 돌아가는 기능
                            BackToMain_Manager.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(BackToMain_Manager); // 실행
                            finish(); // Progress 완전 종료
                        case 3: // 사용자 상세정보에서 넘어옴
                            Intent userintent = getIntent();
                            Intent BackToMain_Detail = new Intent(UserModifyActivity.this, UserDetailActivity.class); // 사용자 상세정보로 돌아가는 기능
                            BackToMain_Detail.putExtra("selectedUser", userintent.getStringExtra("selectedUser")); // 어떤 사용자인지 전달
                            BackToMain_Detail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(BackToMain_Detail); // 실행
                            finish(); // Progress 완전 종료
                    }
                }
            }
        });
    }

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (tag) {
            case 1: // 일반 사용자 마이페이지에서 넘어옴
                Intent BackToMain_User = new Intent(UserModifyActivity.this, MyPageActivity.class); // 일반 사용자 마이페이지로 돌아가는 기능
                BackToMain_User.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BackToMain_User); // 실행
                finish(); // Progress 완전 종료
                break;
            case 2: // 관리자 마이페이지에서 넘어옴
                Intent BackToMain_Manager = new Intent(UserModifyActivity.this, MyPageActivity_Manager.class); // 관리자 마이페이지로 돌아가는 기능
                BackToMain_Manager.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BackToMain_Manager); // 실행
                finish(); // Progress 완전 종료
                break;
            case 3: // 사용자 상세정보에서 넘어옴
                Intent userintent = getIntent();
                Intent BackToMain_Detail = new Intent(UserModifyActivity.this, UserDetailActivity.class); // 사용자 상세정보로 돌아가는 기능
                BackToMain_Detail.putExtra("selectedUser", userintent.getStringExtra("selectedUser")); // 어떤 사용자인지 전달
                BackToMain_Detail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(BackToMain_Detail); // 실행
                finish(); // Progress 완전 종료
                break;
        }
    }
}
