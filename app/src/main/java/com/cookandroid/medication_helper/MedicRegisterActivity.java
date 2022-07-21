/****************************
 MedicRegisterActivity.java
 작성 팀 : 3분카레
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MedicRegisterActivity extends AppCompatActivity {

    Bitmap image;//사용되는 이미지
    private TessBaseAPI mTess;//Tess API Reference
    String datapath="";//언어데이터가 있는 경로

    Button btnCamera;//카메라버튼
    ImageView pictureImage;//사진 표시하는 이미지뷰

    Button btnOCR;//OCR버튼
    TextView OCRTextView;//OCR한 EDI 코드 목록을 표시하는 TextView

    Uri photoUri;
    String OCRresult;

    Button btnRegister;//등록버튼
    Button btnBacktoMain;//메인화면복귀버튼

    private String imageFilePath;
    static final int REQUEST_IMAGE_CAPTURE = 672;

    String[] EdiCodearray;//EDI 코드 목록을 저장하는 스트링 배열

    String[] medicList;//OpenAPI를 이용해 받아온 의약품 이름 목록을 저장하는 배열

    String data;

    /* 의약품 DB를 사용하기 위한 변수들 */
    UserData userData;
    MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    /*스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(MedicRegisterActivity.this, MainPageActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_medicregister);
        setTitle("Medication Helper");

        userData = (UserData)getApplicationContext();
        myHelper = new MedicDBHelper(this);
        btnCamera=(Button) findViewById(R.id.btnPicture);
        btnOCR=(Button)findViewById(R.id.btnOCR);
        btnRegister=(Button)findViewById(R.id.regimedicbtn);
        OCRTextView=(TextView) findViewById(R.id.OCRTextResult);
        pictureImage=(ImageView)findViewById(R.id.CameraPicture);
        btnBacktoMain=(Button)findViewById(R.id.btnback6);


        //언어 파일 경로 설정
        datapath=getFilesDir()+"/tessaract/";

        //언어 파일 존재 여부 확인
        checkFile(new File(datapath+"tessdata/"),"eng");

        String lang="eng";

        mTess=new TessBaseAPI();//TessBaseAPI 생성
        mTess.init(datapath,lang);//초기화
        
        //숫자만 인식해서 추출하도록 블랙리스트, 화이트리스트 설정
        mTess.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, ".,!?@#$%&*()<>_-+=/:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");


        //카메라에 접근해 사진 찍는 버튼
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTakePhotoIntent();
                ((ImageView)findViewById(R.id.CameraPicture)).setVisibility(View.VISIBLE);
                OCRTextView.setVisibility(View.INVISIBLE);
            }
        });


        //OCR로 EDI_Code 추출하는 버튼
        btnOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable d=(BitmapDrawable) ((ImageView)findViewById(R.id.CameraPicture)).getDrawable();
                image=d.getBitmap();

                OCRresult=null;
                mTess.setImage(image);

                OCRresult=mTess.getUTF8Text();

                OCRTextView.setText(OCRresult);

                //String array에 줄 단위로 저장 -> 이걸로 약 데이터 생성하면 됨
                EdiCodearray=OCRresult.split("\n");

                //api를 통해 받아온 약 목록을 저장
                medicList=new String[EdiCodearray.length];

                OCRTextView.setVisibility(View.VISIBLE);
                pictureImage.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), "화면의 코드와 처방전의 코드가 일치하는지 확인해주세요", Toast.LENGTH_LONG).show();

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "등록 중입니다. 잠시만 기다려주세요", Toast.LENGTH_LONG).show();

                sqlDB = myHelper.getWritableDatabase(); // 의약품 저장 DB를 쓰기 가능으로 불러옴
                Cursor cursor = sqlDB.rawQuery("SELECT * FROM medicTBL;", null);

                switch(view.getId()){
                    case R.id.regimedicbtn:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i=0;i<EdiCodearray.length;i++){
                                    data=getXmlData(EdiCodearray[i]);//줄에 EDI 코드로 약품명 받아오기
                                    medicList[i]=data;//약 품목 리스트에 저장
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        /* 읽어들인 약품을 의약품 DB에 저장 */
                                        int count = cursor.getCount() + 1;
                                        for (int i = 0; i < medicList.length; i++) {
                                            sqlDB.execSQL("INSERT INTO medicTBL VALUES ("
                                                    + count + i + ", '"
                                                    + userData.getUserID() + "', '"
                                                    + medicList[i] + "');");
                                        }

                                        Intent BackToMain = new Intent(MedicRegisterActivity.this, MainPageActivity.class); // 메인화면으로 돌아가는 기능
                                        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 백그라운드에서 실행되지 않도록 플래그 삭제
                                        startActivity(BackToMain); // 실행

                                        Toast.makeText(getApplicationContext(), "처방약이 등록되었습니다", Toast.LENGTH_LONG).show();

                                        cursor.close();
                                        sqlDB.close();
                                    }
                                });
                            }
                        }).start();
                        break;
                }
            }
        });

        //메인화면으로 복귀
        btnBacktoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicRegisterActivity.this, MainPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Xml에서 데이터 가져오기
    String getXmlData(String edicode){
        StringBuffer buffer=new StringBuffer();
        String str=edicode;
        String medicName= URLEncoder.encode(str);


        String queryUrl="http://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService02/getDrugPrdtPrmsnDtlInq01?serviceKey=RZnyfUGsOhY2tWWUv262AHpeMQYn4Idqd5cgG0rGNHPd648m5j0Pu3eiS3ewN4XhhHT%2FvuliAmF9KLJdzh1TFA%3D%3D&pageNo=1&numOfRows=3&type=xml&edi_code="+medicName;
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
                        else if(tag.equals("ITEM_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag=xpp.getName();

                        if(tag.equals("item"))buffer.append("\n");
                }
                eventType=xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

    //상수를 받아 각도를 변환
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //비트맵을 각도대로 회전시켜 결과를 반환
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //카메라로 사진 찍어 이미지 띄우기
    private void sendTakePhotoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //intent로 비트맵 이미지 자체를 불러와서 이미지뷰에 출력
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_IMAGE_CAPTURE&&resultCode==RESULT_OK){
            ((ImageView)findViewById(R.id.CameraPicture)).setImageURI(photoUri);
            ExifInterface exif=null;

            Bitmap bitmap= BitmapFactory.decodeFile(imageFilePath);
            try{
                exif = new ExifInterface(imageFilePath);
            }catch(IOException e){
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if(exif!=null){
                exifOrientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
                exifDegree=exifOrientationToDegrees(exifOrientation);
            }else{
                exifDegree=0;
            }
            pictureImage.setImageBitmap(rotate(bitmap,exifDegree));
        }
    }

    //이미지파일 생성
    private File createImageFile() throws IOException{
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="TEST_"+timeStamp+"_";
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File StorageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath=image.getAbsolutePath();
        return image;
    }

    //스마트폰에 사진 파일 복사
    private void copyFiles(String lang){
        try{
            String filepath=datapath+"/tessdata/"+lang+".traineddata";

            AssetManager assetManager=getAssets();

            InputStream inputStream=assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outputStream=new FileOutputStream(filepath);

            byte[] buffer=new byte[1024];
            int read;

            while ((read=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //스마트폰에 파일이 있는 지 확인
    private void checkFile(File dir, String lang){
        if(!dir.exists()&&dir.mkdirs()){
            copyFiles(lang);
        }
        if(dir.exists()){
            String datafilepath=datapath+"/tessdata/"+lang+".traineddata";
            File datafile=new File(datafilepath);
            if(!datafile.exists()){
                copyFiles(lang);
            }
        }
    }
}
