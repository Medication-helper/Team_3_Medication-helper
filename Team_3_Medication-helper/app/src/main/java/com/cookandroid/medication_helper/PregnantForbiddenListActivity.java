/****************************
 PregnantForbiddenListActivity.java
 작성팀 : Hello World!
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    @Override // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(PregnantForbiddenListActivity.this, MedicineListActivity.class); // 메인화면으로 돌아가는 기능
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Medicine");
        ArrayList<String> medicList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicList);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.child(userData.getUserID()).getChildren()) {
                    String value = ds.getKey();
                    System.out.println("Data : " + value);
                    medicList.add(value);
                }

                listSize=medicList.size();
                String[] mediclist = new String[listSize];

                for(int i=0;i<listSize;i++){
                    mediclist[i] = medicList.get(i);
                }

                String[][] medicNameINGList = new String[listSize][4];

                //OpenAPI XML 파싱 스레드
                new Thread(new Runnable() {

                    int forbiddenlistSize=0;
                    int index=0;

                    @Override
                    public void run() {
                        //약물목록에 있는 약 이름들을 이용하여 부작용 정보 내용(주성분,부작용)을 가져와 저장한다.
                        for(int i=0;i<listSize;i++){
                            data=getXmlData(mediclist[i]);

                            String []dataSplit=new String[3];

                            if(TextUtils.isEmpty(data)==false){
                                dataSplit= data.split("\n");
                            }

                            medicNameINGList[i][0]=mediclist[i];
                            System.out.println("약품명 : "+medicNameINGList[i][0]);
                            medicNameINGList[i][1]=dataSplit[0];
                            System.out.println("금기명 : "+medicNameINGList[i][1]);
                            medicNameINGList[i][2]=dataSplit[1];
                            System.out.println("유발성분명 : "+medicNameINGList[i][2]);
                            medicNameINGList[i][3]=dataSplit[2];
                            System.out.println("부작용 : "+medicNameINGList[i][3]);

                            DatabaseReference sideRef = FirebaseDatabase.getInstance().getReference("SideEffect");
                            Map<String, Object> comForbidUpdate = new HashMap<>();
                            if (medicNameINGList[i][2] != null)
                                comForbidUpdate.put("component", medicNameINGList[i][2]);
                            if (medicNameINGList[i][3] != null)
                                comForbidUpdate.put("pForbid", medicNameINGList[i][3]);
                            sideRef.child(medicNameINGList[i][0]).updateChildren(comForbidUpdate);
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

                                        String message = "약품명 : " + medicineName + "\n" +
                                                "성분 : " + ingr + "\n" +
                                                "부작용 : " + sideeffect + "\n";

                                        AlertDialog.Builder builder = new AlertDialog.Builder(PregnantForbiddenListActivity.this);

                                        builder.setTitle("부작용 정보")
                                                .setMessage(message)
                                                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                })
                                                .show();
                                    }
                                });

                            }
                        });

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
