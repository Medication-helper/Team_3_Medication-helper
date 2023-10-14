/****************************
 DuplicateListActivity.java
 작성 팀 : Hello World!
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class DuplicateListActivity extends AppCompatActivity {

    /* 의약품DB를 사용하기 위한 변수들 */
    UserData userData;
    String data;
    int listSize;

    @Override // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(DuplicateListActivity.this, MedicineListActivity.class); // 메인화면으로 돌아가는 기능
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 효능중복 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(Back); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_duplicatelist);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.duptitlebar_custom); // 커스텀 사용할 파일 위치

        userData = (UserData) getApplicationContext();

        Button btnBack = findViewById(R.id.btnback_duplicate);
        ListView DuplicateList = findViewById(R.id.duplicateList);

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
                        for(int i=0;i<listSize;i++){
                            data=getXmlData(mediclist[i]);

                            String []dataSplit=new String[3];

                            if(TextUtils.isEmpty(data)==false){
                                dataSplit= data.split("\n");
                            }

                            medicNameINGList[i][0]=mediclist[i];
                            System.out.println("약품명 : "+medicNameINGList[i][0]);
                            medicNameINGList[i][1]=dataSplit[0];
                            System.out.println("효능 : "+medicNameINGList[i][1]);
                            medicNameINGList[i][2]=dataSplit[1];
                            System.out.println("금기명 : "+medicNameINGList[i][2]);
                            medicNameINGList[i][3]=dataSplit[2];
                            System.out.println("성분 : "+medicNameINGList[i][3]);

                        }

                        forbiddenlistSize=0;


                        for(int i=0;i<listSize;i++){
                            String str=medicNameINGList[i][1];
                            if(TextUtils.isEmpty(str)==false){
                                forbiddenlistSize++;
                            }
                        }

                        System.out.println("부작용 있는 약물 개수 : "+forbiddenlistSize);


                        //효능중복약물에 해당하는 약물들의 약물명만 따로 저장하는 리스트
                        String[] DupXnameList = new String[forbiddenlistSize];

                        index=0;

                        for(int i=0;i<listSize;i++){
                            String str=medicNameINGList[i][1];
                            if(TextUtils.isEmpty(str)==false){
                                DupXnameList[index]=medicNameINGList[i][0];//1열 : 약품명
                                index++;
                            }
                        }

                        //효능중복약물에 해당하는 약물들의 약물명, 효능, 금기명, 성분 저장 2차원 배열
                        String[][] DupXingList = new String[forbiddenlistSize][4];

                        index=0;

                        for(int i=0;i<listSize;i++){
                            String str=medicNameINGList[i][1];
                            if(TextUtils.isEmpty(str)==false){
                                DupXingList[index][0]=medicNameINGList[i][0];//1열 : 약품명
                                DupXingList[index][1]=medicNameINGList[i][1];//2열 : 효능
                                DupXingList[index][2]=medicNameINGList[i][2];//3열 : 금기명
                                DupXingList[index][3]=medicNameINGList[i][3];//4열 : 성분
                                index++;
                            }
                        }

                        //효능중복 약물명 목록을 가지는 ArrayList
                        ArrayList<String> ComXMedication = new ArrayList<>(Arrays.asList(DupXnameList));

                        ArrayAdapter ComXNameAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice,ComXMedication);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //화면에 효능 중복 약품 이름 목록 표시
                                DuplicateList.setAdapter(ComXNameAdapter);

                                DuplicateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                        String medicineName=(String) adapterView.getAdapter().getItem(position);
                                        String forbid="";
                                        String ingr="";
                                        String sideeffect="";

                                        for(int i=0;i<forbiddenlistSize;i++){
                                            if(medicineName.equals(DupXingList[i][0])){
                                                ingr=DupXingList[i][1];
                                                sideeffect=DupXingList[i][3];
                                            }
                                        }

                                        showSideEffectDialog(medicineName, ingr, sideeffect);
                                    }
                                });

                            }
                        });

                    }

                    void showSideEffectDialog(String medicineName,String ingr, String sideeffect){
                        AlertDialog.Builder builder = new AlertDialog.Builder(DuplicateListActivity.this,R.style.AlertDialogTheme);
                        View view= LayoutInflater.from(DuplicateListActivity.this).inflate(R.layout.sideeffect_dialog2,(LinearLayout)findViewById(R.id.seDialog2));

                        builder.setView(view);
                        ((TextView)view.findViewById(R.id.medicname)).setText(medicineName);
                        ((TextView)view.findViewById(R.id.ingredient)).setText(ingr);
                        ((TextView)view.findViewById(R.id.effect)).setText(sideeffect);

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
                Intent intent = new Intent(DuplicateListActivity.this, MedicineListActivity.class); // 이전 화면으로 돌아가는 동작
                startActivity(intent); // 동작 시행
                finish(); // Progress 종료
            }
        });
    }

    //Xml 파싱으로 효능중복에 해당하는 약을 찾아내고 효과 알아내어 저장
    String getXmlData(String medicname) {
        StringBuffer buffer=new StringBuffer();
        String str=medicname;
        String MedicineName= URLEncoder.encode(str);

        String queryUrl="http://apis.data.go.kr/1471000/DURPrdlstInfoService03/getEfcyDplctInfoList03?serviceKey=RZnyfUGsOhY2tWWUv262AHpeMQYn4Idqd5cgG0rGNHPd648m5j0Pu3eiS3ewN4XhhHT%2FvuliAmF9KLJdzh1TFA%3D%3D&pageNo=1&numOfRows=1&type=xml&typeName=효능군중복&itemName="+medicname;
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

                        else if(tag.equals("EFFECT_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("TYPE_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("INGR_NAME")){
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
