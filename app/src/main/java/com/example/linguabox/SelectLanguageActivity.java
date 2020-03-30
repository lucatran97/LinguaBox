package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.cognitiveservices.speech.internal.User;

import java.util.ArrayList;

public class SelectLanguageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String[] countryNames = {"Spanish","Chinese (Simplified)","German"};
    String[] countryCodesTranslator = {"es", "zh-Hans", "de"};
    String[] countryCodesSpeech = {"es-ES", "zh-CN", "de-DE"};
    String chosenCodeTranslator = "es";
    String chosenCodeSpeech = "es-ES";
    String email;
    String name;
    String level = "Basic";
    Button continueButton;
    ArrayList<LanguageProgress> userProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        UserAccount.verifySignIn(getApplicationContext(), this);
        userProgress = UserAccount.getProgress();
        email = UserAccount.getUserEmail();
        name = UserAccount.getUserGivenName();
        continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startChat = new Intent(getApplicationContext(), ChatActivity.class);
                startChat.putExtra("language_translator", chosenCodeTranslator);
                startChat.putExtra("language_speech", chosenCodeSpeech);
                startChat.putExtra("level", level);
                startActivity(startChat);
            }
        });


        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        LanguageSelectAdapter customAdapter = new LanguageSelectAdapter(getApplicationContext(), countryNames);
        spin.setAdapter(customAdapter);
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        chosenCodeTranslator = countryCodesTranslator[position];
        chosenCodeSpeech = countryCodesSpeech[position];
        for(int i = 0; i < userProgress.size(); i++){
            if (userProgress.get(i).getLanguageName().equals(countryNames[position])){
                level = userProgress.get(i).getLevel();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        UserAccount.verifySignIn(getApplicationContext(), this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserAccount.verifySignIn(getApplicationContext(), this);
    }
}
