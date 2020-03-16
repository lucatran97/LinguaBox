package com.example.linguabox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class SelectLanguageActivity extends Activity implements AdapterView.OnItemSelectedListener{
    String[] countryNames = {"Spanish","Chinese (Simplified)","German"};
    String[] countryCodes = {"es", "zh-Hans", "de"};
    String chosenCode = "es";
    int flags[] = {R.drawable.spanish, R.drawable.chinese, R.drawable.german};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
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
        Log.v("CODE", chosenCode);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
