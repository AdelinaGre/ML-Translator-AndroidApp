package com.example.translator;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import android.Manifest;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translator.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {


    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV, cameraImgBtn;
    private MaterialButton translateBtn;
    private TextView translatedTV;
    private static final String TAG="MAIN_TAG";
    private Uri imageUri=null;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=101;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private ProgressDialog progressDialog;
    private Button idBtnTranslate;


    String[] fromLanguages={"From", "English","Romanian","Russian","Arabic", "Ukranian", "Bulgarian", "Czech"};
    String[] toLanguages={"To", "English","Romanian","Russian","Arabic", "Ukranian", "Bulgarian", "Czech"};

    private static final int REQUEST_PERMISSION_CODE=3;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView inputImageBtn;

    String languageCode, fromLanguageCode, toLanguagecode;

    TextInputEditText idEdtSource;

    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



        fromSpinner=findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        ImageView swapButton = findViewById(R.id.swapButton);
        sourceEdt=findViewById(R.id.idEdtSource);

        translateBtn=findViewById(R.id.idBtnTranslate);
        translatedTV=findViewById(R.id.idTVTranslatedTV);
        inputImageBtn=findViewById(R.id.inputImageBtn);
        idBtnTranslate=findViewById(R.id.idBtnTranslate);
        idEdtSource = findViewById(R.id.idEdtSource);



        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter=new ArrayAdapter(this,R.layout.spinner_item,fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguagecode=getLanguageCode(toLanguages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter=new ArrayAdapter(this, R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromPosition = fromSpinner.getSelectedItemPosition();
                int toPosition = toSpinner.getSelectedItemPosition();


                fromSpinner.setSelection(toPosition);
                toSpinner.setSelection(fromPosition);


                fromLanguageCode = getLanguageCode(fromLanguages[toPosition]);
                toLanguagecode = getLanguageCode(toLanguages[fromPosition]);
            }
        });

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    fromLanguageCode = null; // Fără selecție validă
                } else {
                    fromLanguageCode = getLanguageCode(fromLanguages[position]);
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    toLanguagecode = null; // Fără selecție validă
                } else {
                    toLanguagecode = getLanguageCode(toLanguages[position]);
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });




        idBtnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sourceEdt.getText().toString().isEmpty()) {

                    if (fromLanguageCode == null) {
                        Toast.makeText(CameraActivity.this, "Please select source language!", Toast.LENGTH_SHORT).show();
                    } else if (toLanguagecode == null) {
                        Toast.makeText(CameraActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                    } else {
                        translateText(fromLanguageCode, toLanguagecode, sourceEdt.getText().toString());
                    }
                } else if (imageUri != null) {

                    recognizeTextFromImage();

                } else {
                    Toast.makeText(CameraActivity.this, "Please enter text or pick an image!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions=new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        textRecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();

            }
        });

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_camera);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_camera) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_voice) {
                startActivity(new Intent(getApplicationContext(), VoiceActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_text) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }else if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }else if (item.getItemId() == R.id.nav_quiz) {
                startActivity(new Intent(getApplicationContext(), QuizActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });


    }

    private void recognizeTextFromImage(){
        progressDialog.setMessage("Preparing Image...");
        progressDialog.show();

        try{
            InputImage inputImage= InputImage.fromFilePath(this,imageUri);
            progressDialog.setMessage("Recognizing Text...");
            Task <Text> textTaskResult= textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    progressDialog.dismiss();
                    String recognizedText =text.getText();
                    idEdtSource.setText(recognizedText);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CameraActivity.this, "failed recognizing text due to : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
            progressDialog.dismiss();

            Toast.makeText(CameraActivity.this, "failed preparing image due to : "+e.getMessage(), Toast.LENGTH_SHORT).show();


        }

    }

    private void showInputImageDialog() {

        PopupMenu popupMenu=new PopupMenu(this, inputImageBtn);
        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id=menuItem.getItemId();
                if(id==1){
                    if(checkCameraPermission()){
                        pickImageCamera();
                    }else{
                        requestCameraPermission();
                    }
                }else if(id==2){
                    if(checkStoragePermission()){
                        pickImageGallery();
                    }else{
                        requestStoragePermission();
                    }
                }


                return true;
            }
        });

    }

    private void pickImageGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK){
                        Intent data=result.getData();
                        imageUri=data.getData();

                    }else{
                        Toast.makeText(CameraActivity.this, "Cancelled...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );


    private void pickImageCamera(){
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){

                    }else{
                        Toast.makeText(CameraActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){
        boolean result=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean cameraResult=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean storageResult=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)==(PackageManager.PERMISSION_GRANTED);
        return cameraResult&&storageResult;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=true;
                    //=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&storageAccepted){
                        pickImageCamera();
                    }else{
                        Toast.makeText(this, "Camera &Storage permisssion required", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageGallery();
                } else {
                    Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                }
            } break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(resultCode==RESULT_OK && data !=null){
                ArrayList<String> result =data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }
    }

    private void translateText(String fromLanguageCode, String toLanguageCode, String source){
        translatedTV.setText("Downloading Modal...");
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode)
                        .build();
        final Translator translator= Translation.getClient(options);
        translator.downloadModelIfNeeded()
                .addOnSuccessListener(unused -> translator.translate(source)
                        .addOnSuccessListener(translatedText -> translatedTV.setText(translatedText))
                        .addOnFailureListener(e -> {
                            translatedTV.setText("Translation failed.");
                            e.printStackTrace();
                        }))
                .addOnFailureListener(e -> {
                    translatedTV.setText("Model download failed.");
                    e.printStackTrace();
                });
    }

    public String getLanguageCode(String language){

        switch(language){
            case "English":
                return  TranslateLanguage.ENGLISH;

            case "Romanian":
                return TranslateLanguage.ROMANIAN;

            case "Russian":
                return  TranslateLanguage.RUSSIAN;

            case "Arabic":
                return TranslateLanguage.ARABIC;

            case "Ukranian":
                return TranslateLanguage.UKRAINIAN;

            case "Bulgarian":
                return  TranslateLanguage.BULGARIAN;

            case "Czech":
                return TranslateLanguage.CZECH;
            default:
                return null;
        }

    }




}