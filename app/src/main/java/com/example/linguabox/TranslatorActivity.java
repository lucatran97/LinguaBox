package com.example.linguabox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class TranslatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translator_machine);
        Spinner lan_list_1 = findViewById(R.id.language_list_1);
        Spinner lan_list_2 = findViewById(R.id.language_list_2);

        ArrayAdapter lan_Adapter = ArrayAdapter.createFromResource(this, R.array.language_list, android.R.layout.simple_spinner_item);
        lan_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        lan_list_1.setAdapter(lan_Adapter);
        lan_list_1.setOnItemSelectedListener(this);
        lan_list_2.setAdapter(lan_Adapter);
        lan_list_2.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
