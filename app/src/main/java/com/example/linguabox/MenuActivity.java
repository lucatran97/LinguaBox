package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    String name;
    String email;
    TextView welcomeDisplay;
    Button chat;
    Button translate;
    Button profile;
    Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent = this.getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        welcomeDisplay = (TextView) findViewById(R.id.menu_text_welcome);
        welcomeDisplay.setText("Hello " + name);
        chat = (Button) findViewById(R.id.menu_chat_button);
        translate = (Button) findViewById(R.id.menu_translate_button);
        profile = (Button) findViewById(R.id.menu_profile_button);
        signOut = (Button) findViewById(R.id.menu_sign_out_button);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectLanguage = new Intent(getApplicationContext(), SelectLanguageActivity.class);
                selectLanguage.putExtra("email", email);
                startActivity(selectLanguage);
            }
        });
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
