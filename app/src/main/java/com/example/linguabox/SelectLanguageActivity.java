package com.example.linguabox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectLanguageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String[] countryNames = {"Spanish","Chinese (Simplified)","German"};
    String[] countryCodes = {"es", "zh-Hans", "de"};
    String chosenCode = "es";
    String email;
    String name;
    int flags[] = {R.drawable.spanish, R.drawable.chinese, R.drawable.german};
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Intent intent = this.getIntent();
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startChat = new Intent(getApplicationContext(), ChatActivity.class);
                startChat.putExtra("email", email);
                startChat.putExtra("language", chosenCode);
                startChat.putExtra("name", name);
                startActivity(startChat);
            }
        });


        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.simpleSpinner);
        spin.setOnItemSelectedListener(this);

        LanguageSelectAdapter customAdapter = new LanguageSelectAdapter(getApplicationContext(),flags,countryNames, countryCodes);
        spin.setAdapter(customAdapter);
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        chosenCode = countryCodes[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
