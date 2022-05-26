package com.cookandroid.medication_helper;

import android.content.Intent;
import android.content.res.AssetManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class medicRegister extends AppCompatActivity {

    Bitmap image;//사용되는 이미지
    private TessBaseAPI mTess;//Tess API REference
    String datapath="";//언어데이터가 있는 경로

    Button btnCamera;
    ImageView imageView;

    Button btnOCR;
    TextView Stringcheck;

    Uri photoUri;
    String OCRresult;
    private String imageFilePath;
    static final int REQUEST_IMAGE_CAPTURE = 672;

    /*스마트폰의 뒤로가기 버튼에 대한 뒤로가기 동작 구현*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(medicRegister.this, MainActivity.class);
        BackToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicationregister);
        setTitle("Medication Helper");

        btnCamera=(Button) findViewById(R.id.btnPicture);
        btnOCR=(Button)findViewById(R.id.btnOCR);

        Button backbtn = (Button) findViewById(R.id.backtoMain);//메인메뉴버튼
        Button medicregibtn=(Button) findViewById(R.id.regimedicbtn);//약 목록 등록 버튼

        datapath=getFilesDir()+"/tessaract/";

        checkFile(new File(datapath+"tessdata/"),"kor");
        checkFile(new File(datapath+"tessdata/"),"eng");

        String lang="kor+eng";

        mTess=new TessBaseAPI();
        mTess.init(datapath,lang);

        
        //카메라에 접근해 사진 찍는 버튼
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTakePhotoIntent();
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
                TextView OCRTextView=(TextView) findViewById(R.id.OCRTextResult);
                OCRTextView.setText(OCRresult);

                /*String array에 줄 단위로 저장 -> 이걸로 약 데이터 생성하면 됨*/
                String[] array=OCRresult.split("\n");
            }
        });


        //메인메뉴 복귀 버튼
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(BackToMain);
            }
        });
    }

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

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

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
            ((ImageView)findViewById(R.id.CameraPicture)).setImageBitmap(rotate(bitmap,exifDegree));
        }
    }

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
