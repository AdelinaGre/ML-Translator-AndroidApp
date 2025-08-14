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
import android.media.Image;
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

import com.bumptech.glide.Glide;
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

public class VoiceActivity extends AppCompatActivity {


    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV;
    private MaterialButton translateBtn;
    private TextView translatedTV;

    private Button idBtnTranslate;


    String[] fromLanguages={"From", "English","Romanian","Russian","Arabic", "Ukranian", "Bulgarian", "Czech"};
    String[] toLanguages={"To", "English","Romanian","Russian","Arabic", "Ukranian", "Bulgarian", "Czech"};

    private static final int REQUEST_PERMISSION_CODE=3;




    String languageCode, fromLanguageCode, toLanguagecode;

    TextInputEditText idEdtSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);


        ImageView gif_wave=findViewById(R.id.gif_wave);

        Glide.with(this).load(R.drawable.wave).into(gif_wave);


        fromSpinner=findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        ImageView swapButton = findViewById(R.id.swapButton);
        sourceEdt=findViewById(R.id.idEdtSource);
        micIV=findViewById(R.id.idIVMic);
        translateBtn=findViewById(R.id.idBtnTranslate);
        translatedTV=findViewById(R.id.idTVTranslatedTV);

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
                // Obține pozițiile selectate din Spinner-uri
                int fromPosition = fromSpinner.getSelectedItemPosition();
                int toPosition = toSpinner.getSelectedItemPosition();

                // Interschimbă pozițiile selectate
                fromSpinner.setSelection(toPosition);
                toSpinner.setSelection(fromPosition);

                // Actualizează codurile limbilor selectate
                fromLanguageCode = getLanguageCode(fromLanguages[toPosition]);
                toLanguagecode = getLanguageCode(toLanguages[fromPosition]);
            }
        });

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    fromLanguageCode = null;
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
                    toLanguagecode = null;
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
                        Toast.makeText(VoiceActivity.this, "Please select source language!", Toast.LENGTH_SHORT).show();
                    } else if (toLanguagecode == null) {
                        Toast.makeText(VoiceActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                    } else {
                        translateText(fromLanguageCode, toLanguagecode, sourceEdt.getText().toString());
                    }
                }
            }
        });


        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Locale selectedLocale = new Locale(fromLanguageCode);
                Toast.makeText(VoiceActivity.this,"fromLanguageCode:"+fromLanguageCode,Toast.LENGTH_SHORT).show();
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,fromLanguageCode);
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speal to convert into text");
                try{
                    startActivityForResult(i,REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(VoiceActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });






        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_voice);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_voice) {
                startActivity(new Intent(getApplicationContext(), VoiceActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_text) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_camera) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
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
                return "en-US";

            case "Romanian":
                return "ro-RO";
            case "Russian":
                return "ru-RU";

            case "Arabic":
                return "ar";

            case "Ukranian":
                return "uk-UA";

            case "Bulgarian":
                return "bg-BG";

            case "Czech":
                 return "cs-CZ";
            default:
                return null;
        }

    }




}