package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    String name, email;
    TextView welcomeDisplay;
    Button chat, translate, profile, signOut, imageTranslation;

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
        imageTranslation = (Button) findViewById(R.id.menu_image_translation);

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
                Intent translator = new Intent(getApplicationContext(), TranslatorActivity.class);
                startActivity(translator);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount.signOut(getApplicationContext(), MenuActivity.this);
            }
        });

        imageTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageRecognition = new Intent(getApplicationContext(), ImageMethodActivity.class);
                startActivity(imageRecognition);
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
