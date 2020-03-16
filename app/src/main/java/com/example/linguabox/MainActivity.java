package com.example.linguabox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    // Set Up Constants
    int SIGNED_IN = 0;
    GoogleSignInClient client;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_google);

        // Initialize Sign-In Button
        signInButton = findViewById(R.id.sign_in_button);

        // Setting Up Google to Require Email & Basic Info

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build Google Client, options specified by gso
        client = GoogleSignIn.getClient(this, gso);


        signInButton.setOnClickListener((view)-> {signIn();});

    }

    private void signIn() {
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, SIGNED_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGNED_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.w("Sign In Success", "Sign In Successful !!");
            Toast.makeText(MainActivity.this, "SIGN IN SUCCESSFUL !!", Toast.LENGTH_LONG).show();
            String name = account.getGivenName();
            String email = account.getEmail();
            Intent chooseLanguage = new Intent(getApplicationContext(), SelectLanguageActivity.class);
            chooseLanguage.putExtra("name", name);
            chooseLanguage.putExtra("email", email);
            startActivity(chooseLanguage);

        } catch (ApiException e) {
            e.printStackTrace();
            Log.w("Sign In Error", "Sign In Failed. Failed Code =" + e.getStatusCode());
            Toast.makeText(MainActivity.this, "LOG IN FAILED", Toast.LENGTH_LONG).show();
        }
    }

    //@Override
    /*protected void onStart() {

        Log.w("account logged in", "acc = " + account.getEmail());

        if (account != null) {
            //startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
        super.onStart();
    }*/
}
