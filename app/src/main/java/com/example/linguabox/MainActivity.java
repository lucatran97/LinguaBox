package com.example.linguabox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    // Set Up Constants
    int SIGNED_IN = 0;
    GoogleSignInClient client;
    SignInButton signInButton;
    private ExecutorService es;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_google);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        // Initialize Sign-In Button
        signInButton = findViewById(R.id.sign_in_button);
        es = Executors.newSingleThreadExecutor();

        // Setting Up Google to Require Email & Basic Info

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build Google Client, options specified by gso
        client = GoogleSignIn.getClient(this, gso);

        Intent intent = this.getIntent();
        if(intent.getBooleanExtra("Sign Out", false)){
            client.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "SIGN OUT SUCCESSFUL!", Toast.LENGTH_LONG).show();
                }
            });
        }

        signInButton.setOnClickListener((view)-> {signIn();});

    }


    /**
     * This function starts automatically when users open the app to get the users to sign in.
     */
    private void signIn() {
        spinner.setVisibility(View.VISIBLE);
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, SIGNED_IN);
    }

    /**
     * This function sets the user up for an Activity Result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGNED_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * This function handles Sign In results.
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.w("Sign In Success", "Sign In Successful !!");
            assert account != null;
            Future<String> result = es.submit(new MongodbLog(account.getEmail()));
            try {
                result.get();
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "SIGN IN SUCCESSFUL!", Toast.LENGTH_LONG).show();
                UserAccount.setAccount(account.getEmail(), account.getGivenName(), account.getFamilyName());
                Intent mainMenu = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(mainMenu);
            } catch (Exception e) {
                spinner.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Server is booting or not available. Please try again in a moment.", Toast.LENGTH_LONG).show();
                client.signOut();
            }
        } catch (ApiException e) {
            e.printStackTrace();
            Log.w("Sign In Error", "Sign In Failed. Failed Code =" + e.getStatusCode());
            Toast.makeText(MainActivity.this, "LOG IN FAILED", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        // This is to disable users' pressing the back button after signing out
    }
}

class MongodbLog implements Callable<String> {
    String email;

    public MongodbLog(String email) {
        this.email = email;
    }

    @Override
    public String call() throws Exception {
        try {
            return HttpRequest.signIn(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
