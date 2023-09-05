/******************
 * 회원가입 담당 코드
 * 작성 팀 : Hello World!
 * 제작자 : 안현종
 ******************/

package com.cookandroid.medication_helper;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserRegisterActivity extends AppCompatActivity{
    EditText E_ID, E_Pass, E_PassCheck, E_Name;
    RadioGroup RG;
    RadioButton RB_Man, RB_Woman;
    Button btnRegister_Back, btnIDCheck, btnBirthChoose, btnComplete;
    TextView tvBirth;
    private boolean validate = false;
    int dateCheckCounter = 0;

    private DatabaseReference mDatabase;

    /* Firebase에 사용자 정보를 등록하기 위한 클래스 */
    public class addUser {
        public String uPW, uName, birthDate, uGender, tag;

        public addUser() {} //setValue를 사용하기 위해 필요, 없으면 작동하지 않음

        public addUser(String uPW, String uName, String birthDate, String uGender, String tag) {
            this.uPW = uPW;
            this.uName = uName;
            this.birthDate = birthDate;
            this.uGender = uGender;
            this.tag = tag;
        }
    }

    @Override // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(UserRegisterActivity.this, com.cookandroid.medication_helper.MainActivity.class); // 메인화면으로 돌아가는 기능
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원가입 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(BackToMain); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_userregister);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.register_titlebar_custom); // 커스텀 사용할 파일 위치

        E_ID = (EditText) findViewById(R.id.E_ID);
        E_Pass = (EditText) findViewById(R.id.E_Pass);
        E_PassCheck = (EditText) findViewById(R.id.E_PassCheck);
        E_Name = (EditText) findViewById(R.id.E_name);
        RG = (RadioGroup) findViewById(R.id.RG);
        RB_Man = (RadioButton) findViewById(R.id.RB_man);
        RB_Woman = (RadioButton) findViewById(R.id.RB_woman);
        btnRegister_Back = (Button) findViewById(R.id.btnRegister_Back);
        btnIDCheck = (Button) findViewById(R.id.idCheck);
        btnBirthChoose = (Button) findViewById(R.id.BtnBirthChoose);
        btnComplete = (Button) findViewById(R.id.BtnComplete);
        tvBirth = (TextView) findViewById(R.id.TvBirth);

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

        btnRegister_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(UserRegisterActivity.this, com.cookandroid.medication_helper.MainActivity.class); // 메인화면으로 돌아가는 기능
                BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원가입 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                startActivity(BackToMain); // 실행
                finish(); // Progress 완전 종료
            }
        });

        /* 입력한 ID가 바뀔 때마다 중복확인 여부 초기화 */
        E_ID.addTextChangedListener(new TextWatcher() {
            @Override // 텍스트가 변경되기 전에 호출되는 메소드이므로 필요없음
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override // 텍스트가 변경될 때 호출되는 메소드
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validate = false;
            }

            @Override // 텍스트가 변경된 후 호출되는 메소드이므로 상단의 메소드로도 충분함.
            public void afterTextChanged(Editable editable) {

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
                    tvBirth.setText(dayOfMonth +"/" + (month + 1) + "/" + year); // 생년월일 선택 버튼에 생년월일을 표시함
            }
        }, mYear, mMonth, mDay);

        /* ID 중복확인 */
        btnIDCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID = E_ID.getText().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(ID);

                if (ID.isEmpty())
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                else {
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                                Toast.makeText(getApplicationContext(), "사용할 수 없는 ID입니다.", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getApplicationContext(), "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                                validate = true;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnBirthChoose.isClickable()) {
                    datePickerDialog.show(); // 상단의 생년월일 선택 함수 작동
                }
            }
        });

        /* 회원가입 완료 */
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID = E_ID.getText().toString();
                String password = E_Pass.getText().toString();
                String passwordCheck = E_PassCheck.getText().toString();
                String name = E_Name.getText().toString();
                String birth = tvBirth.getText().toString();
                String gender = "";

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

                if (ID.length() == 0) { // ID란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "ID가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else if(!validate) { // ID 중복확인 체크가 되지 않은 경우
                    Toast.makeText(getApplicationContext(), "ID 중복확인 체크를 해주세요.", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) { // 비밀번호란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "비밀번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else if (passwordCheck.length() == 0) { // 비밀번호 확인란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 한 번 입력해 주십시오.", Toast.LENGTH_SHORT).show();
                } else if (password.equals(passwordCheck) == false) { // 비밀번호랑 확인이 다를 경우
                    Toast.makeText(getApplicationContext(), "비밀번호 확인란에 입력된 내용이 비밀번호와 다릅니다.", Toast.LENGTH_SHORT).show();
                } else if (name.length() == 0) { // 이름란이 공백인 경우
                    Toast.makeText(getApplicationContext(), "사용자 이름이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    addUser addUser = new addUser(password, name, birth, gender, "0");

                    mDatabase.child("User").child(ID).setValue(addUser);

                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent BackToMain = new Intent(UserRegisterActivity.this, com.cookandroid.medication_helper.MainActivity.class); // 메인화면으로 돌아가는 기능
                    BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원가입 페이지가 백그라운드에서 돌아가지 않도록 완전종료
                    startActivity(BackToMain); // 실행
                    finish(); // Progress 완전 종료
                }
            }
        });
    }
}
