package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.textfield.TextInputEditText;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;


public class MainActivity extends AppCompatActivity {

    TextView userName;
    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView micIV, cameraImgBtn;
    private MaterialButton translateBtn;
    private TextView translatedTV;
    private static final String TAG="MAIN_TAG";


    private Button idBtnTranslate;


    String[] fromLanguages={"From", "English","Romanian","Russian","Arabic", "Ukranian", "Bulgarian", "Czech"};
    String[] toLanguages={"To", "English","Romanian","Russian","Arabic", "Ukranian", "Bulgarian", "Czech"};




    String fromLanguageCode, toLanguagecode;

    TextInputEditText idEdtSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        fromSpinner=findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        ImageView swapButton = findViewById(R.id.swapButton);
        sourceEdt=findViewById(R.id.idEdtSource);

        translateBtn=findViewById(R.id.idBtnTranslate);
        translatedTV=findViewById(R.id.idTVTranslatedTV);

        idBtnTranslate=findViewById(R.id.idBtnTranslate);
        idEdtSource = findViewById(R.id.idEdtSource);
        TextView textView = findViewById(R.id.idFromLang);
        TextView textView2 = findViewById(R.id.idToLang);


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
                textView.setText(fromLanguages[position]);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                textView.setText("Selectați o limbă");
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
                textView2.setText(toLanguages[position]);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                textView2.setText("Selectați o limbă");
            }
        });




        idBtnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sourceEdt.getText().toString().isEmpty()) {

                    if (fromLanguageCode == null) {
                        Toast.makeText(MainActivity.this, "Please select source language!", Toast.LENGTH_SHORT).show();
                    } else if (toLanguagecode == null) {
                        Toast.makeText(MainActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                    } else {
                        translateText(fromLanguageCode, toLanguagecode, sourceEdt.getText().toString());
                    }
                }
            }
        });









        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_text);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_text) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_voice) {
                startActivity(new Intent(getApplicationContext(), VoiceActivity.class));
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