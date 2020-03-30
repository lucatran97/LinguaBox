package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class SelectLanguageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String[] countryNames = {"Spanish","Chinese (Simplified)","German"};
    String[] countryCodesTranslator = {"es", "zh-Hans", "de"};
    String[] countryCodesTextToSpeech = {"es-ES", "zh-CN", "de-DE"};
    String chosenCodeTranslator = "es";
    String chosenCodeTextToSpeech = "es-ES";
    String email;
    String name;
    int flags[] = {R.drawable.spanish, R.drawable.chinese, R.drawable.german};
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Intent intent = this.getIntent();
        UserAccount.verifySignIn(getApplicationContext(), this);
        email = UserAccount.getUserEmail();
        name = UserAccount.getUserGivenName();
        continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startChat = new Intent(getApplicationContext(), ChatActivity.class);
                startChat.putExtra("language_translator", chosenCodeTranslator);
                startChat.putExtra("language_text_to_speech", chosenCodeTextToSpeech);
                startActivity(startChat);
            }
        });


        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        LanguageSelectAdapter customAdapter = new LanguageSelectAdapter(getApplicationContext(),flags,countryNames, countryCodesTranslator, countryCodesTextToSpeech);
        spin.setAdapter(customAdapter);
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        chosenCodeTranslator = countryCodesTranslator[position];
        chosenCodeTextToSpeech = countryCodesTextToSpeech[position];
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
