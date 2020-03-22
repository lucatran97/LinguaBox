package com.microsoft.cognitiveservices.speech.samples.quickstart;

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

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import static android.Manifest.permission.*;


public class MainActivity extends AppCompatActivity {

    // Set Up Constants
    int SIGNED_IN = 0;
    GoogleSignInClient client;
    SignInButton signInButton;

    // Replace below with your own subscription key
    private static String speechSubscriptionKey = "9d1cb6dc1aff4b6ab5ab311b84f642a5";
    // Replace below with your own service region (e.g., "westus").
    private static String serviceRegion = "eastus";

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Note: we need to request the permissions
        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{INTERNET}, requestCode);

        // Initialize speech synthesizer and its dependencies
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        assert(speechConfig != null);

        synthesizer = new SpeechSynthesizer(speechConfig);
        assert(synthesizer != null);
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
            assert account != null;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release speech synthesizer and its dependencies
        synthesizer.close();
        speechConfig.close();
    }

    public void onSpeechButtonClicked(View v) {
        TextView outputMessage = this.findViewById(R.id.outputMessage);
        EditText speakText = this.findViewById(R.id.speakText);

        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event
            SpeechSynthesisResult result = synthesizer.SpeakText(speakText.getText().toString());
            assert(result != null);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                outputMessage.setText("Speech synthesis succeeded.");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                String cancellationDetails =
                        SpeechSynthesisCancellationDetails.fromResult(result).toString();
                outputMessage.setText("Error synthesizing. Error detail: " +
                        System.lineSeparator() + cancellationDetails +
                        System.lineSeparator() + "Did you update the subscription info?");
            }

            result.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
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
