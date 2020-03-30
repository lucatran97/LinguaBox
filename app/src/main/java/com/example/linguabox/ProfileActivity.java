package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] countryNames = {"Spanish","Chinese (Simplified)","German"};
    String[] countryCodesTranslator = {"es", "zh-Hans", "de"};
    String[] countryCodesTextToSpeech = {"es-ES", "zh-CN", "de-DE"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Spinner spin = (Spinner) findViewById(R.id.profileSpinner);
        spin.setOnItemSelectedListener(this);
        LanguageSelectAdapter customAdapter = new LanguageSelectAdapter(getApplicationContext(), countryNames);
        spin.setAdapter(customAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    /**
     * Menu Option Assignments
     * @param item the option chosen by the user
     * @return true if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.option_close:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}
