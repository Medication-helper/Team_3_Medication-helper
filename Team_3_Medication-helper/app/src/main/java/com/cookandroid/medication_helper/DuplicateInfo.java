package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class DuplicateInfo extends AppCompatActivity {
    /* 의약품DB를 사용하기 위한 변수들 */
    UserData userData;
    String data;
    int listSize;

    /* 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(DuplicateInfo.this, MedicineListActivity.class); // 복용 약 목록으로 돌아가는 기능
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 병용금지 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(Back); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comforbidinfo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Intent에서 데이터 추출
        String medicname = getIntent().getStringExtra("medicname");
        System.out.println(medicname);

        // medicname을 사용하여 필요한 작업 수행
        TextView name=findViewById(R.id.medicname);
        TextView sideeffect=findViewById(R.id.sideeffect);
        Button btnOK=findViewById(R.id.btnOk);

        String[] medicNameINGList = new String[4]; // 파싱해온 결과를 저장할 배열

        new Thread(new Runnable() {
            @Override
            public void run() {
                data=getXmlData(medicname);

                String []dataSplit=new String[3];

                if(TextUtils.isEmpty(data)==false){
                    dataSplit= data.split("\n");
                }

                medicNameINGList[0]=medicname; // 약품명
                medicNameINGList[1]=dataSplit[0]; // 금기명
                medicNameINGList[2]=dataSplit[1]; // 유발성분명
                medicNameINGList[3]=dataSplit[2]; // 부작용

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(medicname);
                        if(medicNameINGList[1]!=null)
                            sideeffect.setText(medicNameINGList[1]);
                    }
                });
            }
        }).start();



        btnOK.setOnClickListener(new View.OnClickListener() { // 뒤로가기 버튼을 눌렀을 경우
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DuplicateInfo.this, MedicineListActivity.class); // 이전 화면으로 돌아가는 동작
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
