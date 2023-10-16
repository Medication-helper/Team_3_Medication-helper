/****************************
 PregnantForbiddenListActivity.java
 작성팀 : [02-03]
 프로그램명 : Medication-Helper
 ***************************/
package com.cookandroid.medication_helper;

import static com.cookandroid.medication_helper.FirebaseUtils.updateSideCount;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PregnantForbiddenListActivity extends AppCompatActivity {

    /* 의약품DB를 사용하기 위한 변수들 */
    UserData userData;
    String data;
    int listSize;

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(PregnantForbiddenListActivity.this, MedicineListActivity.class); // 복용 약 목록으로 돌아가는 기능
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 병용금지 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(Back); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_pregnantforbiddenlist);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.pregtitlebar_custom); // 커스텀 사용할 파일 위치

        userData = (UserData) getApplicationContext();

        Button btnBack = findViewById(R.id.btnback_pregforbid);
        ListView pregXList = findViewById(R.id.pregnantXList);
        ArrayList<String> medicList = new ArrayList<>();

        Toast.makeText(getApplicationContext(),"목록을 불러오는 중입니다.\n잠시만 기다려주세요",Toast.LENGTH_LONG).show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Medicine"); // 사용자가 복용 중인 약품을 가져올 Firebase DB 경로 지정

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child(userData.getUserID()).getChildren()) {
                    String value = ds.getKey(); // 복용 중인 약품의 이름을 읽어와
                    medicList.add(value); // medicList에 저장
                }

                listSize=medicList.size(); // 복용 중인 약품 개수
                String[] mediclist = new String[listSize]; // 생성된 medicList 동적 배열을 바탕으로 medicList 정적 배열 생성

                for(int i=0;i<listSize;i++){
                    mediclist[i] = medicList.get(i); // 약품 이름 저장
                }

                String[][] medicNameINGList = new String[listSize][4]; // 파싱해온 결과를 저장할 배열

                //OpenAPI XML 파싱 스레드
                new Thread(new Runnable() {

                    int forbiddenlistSize=0;
                    int index=0;

                    @Override
                    public void run() {
                        /* 약물목록에 있는 약 이름들을 이용하여 부작용 정보 내용(주성분,부작용)을 가져와 저장한다. */
                        for(int i=0;i<listSize;i++){
                            final int index = i; // i를 DatabaseReference 과정에서 사용하고자 final int로 변경
                            data=getXmlData(mediclist[index]);

                            String []dataSplit=new String[3];

                            if(TextUtils.isEmpty(data)==false){
                                dataSplit= data.split("\n");
                            }

                            medicNameINGList[index][0]=mediclist[index]; // 약품명
                            medicNameINGList[index][1]=dataSplit[0]; // 금기명
                            medicNameINGList[index][2]=dataSplit[1]; // 유발성분명
                            medicNameINGList[index][3]=dataSplit[2]; // 부작용

                            DatabaseReference sideRef = FirebaseDatabase.getInstance().getReference("SideEffect"); // 부작용 정보를 저장할 Firebase DB 경로 지정
                            sideRef.child(medicNameINGList[index][0]).addListenerForSingleValueEvent(new ValueEventListener() { // DB의 약품이름 항목에 대해
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.child("pForbid").exists()) { // DB에 파싱한 약품의 임부금기 정보가 적혀있지 않고
                                        if (medicNameINGList[index][1] != null) { // 파싱한 약품이 임부금기 사항이 있다면
                                            updateSideCount("임부금기", 1); // 임부금기 사항이 있는 데이터가 하나 늘었음을 기록
                                            Map<String, Object> comForbidUpdate = new HashMap<>(); // DB 저장용 Map을 생성한 후
                                            if (medicNameINGList[index][2] != null) // 파싱한 데이터에 성분이 있다면
                                                comForbidUpdate.put("component", medicNameINGList[index][2]); // 성분을 Map에 저장
                                            if (medicNameINGList[index][3] != null) // 파싱한 데이터에 임부금기 사항이 있다면
                                                comForbidUpdate.put("pForbid", medicNameINGList[index][3]); // 해당 사항을 Map에 저장
                                            sideRef.child(medicNameINGList[index][0]).updateChildren(comForbidUpdate); // Map을 기반으로 DB에 저장
                                        }
                                    }
                                }

                                /* 에러 처리 */
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "알 수 없는 에러입니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        forbiddenlistSize=0;


                        for(int i=0;i<listSize;i++){
                            String str=medicNameINGList[i][1];
                            if(TextUtils.isEmpty(str)==false){
                                forbiddenlistSize++;
                            }
                        }

                        System.out.println("부작용 있는 약물 개수 : "+forbiddenlistSize);

                        //임부금기약물에 해당하는 약물들의 약물명만 따로 저장하는 리스트
                        String[] pregXnameList = new String[forbiddenlistSize];

                        index=0;

                        for(int i=0;i<listSize;i++){
                            String str=medicNameINGList[i][1];
                            if(TextUtils.isEmpty(str)==false){
                                pregXnameList[index]=medicNameINGList[i][0];//1열 : 약품명
                                index++;
                            }
                        }


                        //임부금기약물에 해당하는 약물들의 약물명, 금기명, 약물성분, 부작용 저장 2차원 배열
                        String[][] pregXingList = new String[forbiddenlistSize][4];

                        index=0;

                        for(int i=0;i<listSize;i++){
                            String str=medicNameINGList[i][1];
                            if(TextUtils.isEmpty(str)==false){
                                pregXingList[index][0]=medicNameINGList[i][0];//1열 : 약품명
                                pregXingList[index][1]=medicNameINGList[i][1];//2열 : 금기명
                                pregXingList[index][2]=medicNameINGList[i][2];//3열 : 약품 성분
                                pregXingList[index][3]=medicNameINGList[i][3];//3열 : 약품 부작용
                                index++;
                            }
                        }

                        //병용금기사항 약물명 목록을 가지는 ArrayList
                        ArrayList<String> ComXMedication = new ArrayList<>(Arrays.asList(pregXnameList));

                        ArrayAdapter ComXNameAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice,ComXMedication);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //화면에 병용 금기 대상 약품 이름 목록 표시
                                pregXList.setAdapter(ComXNameAdapter);

                                pregXList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                        String medicineName=(String) adapterView.getAdapter().getItem(position);
                                        String ingr="";
                                        String sideeffect="";

                                        for(int i=0;i<forbiddenlistSize;i++){
                                            if(medicineName.equals(pregXingList[i][0])){
                                                ingr=pregXingList[i][2];
                                                sideeffect=pregXingList[i][3];
                                            }
                                        }

                                        showSideEffectDialog(medicineName, ingr, sideeffect);
                                    }
                                });


                            }
                        });
                    }

                    void showSideEffectDialog(String medicineName,String ingr, String sideeffect){
                        AlertDialog.Builder builder = new AlertDialog.Builder(PregnantForbiddenListActivity.this,R.style.AlertDialogTheme);
                        View view= LayoutInflater.from(PregnantForbiddenListActivity.this).inflate(R.layout.sideeffect_dialog1,(LinearLayout)findViewById(R.id.seDialog1));

                        builder.setView(view);
                        ((TextView)view.findViewById(R.id.medicname)).setText(medicineName);
                        ((TextView)view.findViewById(R.id.ingredient)).setText(ingr);
                        ((TextView)view.findViewById(R.id.sideffect)).setText(sideeffect);

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
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"알 수 없는 오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
            }
        });




        btnBack.setOnClickListener(new View.OnClickListener() { // 뒤로가기 버튼을 눌렀을 경우
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PregnantForbiddenListActivity.this, MedicineListActivity.class); // 이전 화면으로 돌아가는 동작
                startActivity(intent); // 동작 시행
                finish(); // Progress 종료
            }
        });
    }

    //Xml 파싱으로 임부 금기에 해당하는 약과 부작용 원인 성분 알아내기
    String getXmlData(String medicname) {
        StringBuffer buffer=new StringBuffer();
        String str=medicname;
        String MedicineName= URLEncoder.encode(str);

        String queryUrl="https://apis.data.go.kr/1471000/DURPrdlstInfoService03/getPwnmTabooInfoList03?serviceKey=RZnyfUGsOhY2tWWUv262AHpeMQYn4Idqd5cgG0rGNHPd648m5j0Pu3eiS3ewN4XhhHT%2FvuliAmF9KLJdzh1TFA%3D%3D&pageNo=1&numOfRows=1&type=xml&typeName=임부금기&itemName="+medicname;
        try {
            URL url=new URL(queryUrl);
            InputStream is=url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new InputStreamReader(is,"UTF-8"));

            String tag;

            xpp.next();
            int eventType=xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){

                    case XmlPullParser.START_TAG:
                        tag=xpp.getName();

                        if(tag.equals("item"));

                        else if(tag.equals("TYPE_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("INGR_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag.equals("PROHBT_CONTENT")){
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        break;
                }
                eventType=xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
