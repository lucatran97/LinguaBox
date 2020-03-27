package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.cognitiveservices.speech.internal.User;

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
        UserAccount.verifySignIn(getApplicationContext(), this);
        Intent intent = this.getIntent();
        name = UserAccount.getUserGivenName();
        email = UserAccount.getUserEmail();
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
                UserAccount.signOut(getApplicationContext(), MenuActivity.this);
            }
        });
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
