package com.example.linguabox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UserAccount {
    private static String userEmail = null;
    private static String userGivenName = null;
    private static String userLastName = null;

    public static void setAccount(String email, String givenName, String lastName){
        userEmail = email;
        userGivenName = givenName;
        userLastName = lastName;
    }

    public static void signOut(Context context, Activity caller){
        userEmail = null;
        userGivenName = null;
        userLastName = null;
        Intent signOut = new Intent(context, MainActivity.class);
        signOut.putExtra("Sign Out", true);
        caller.startActivity(signOut);
    }

    public static String getUserEmail(){
        return userEmail;
    }

    public static String getUserGivenName() {
        return userGivenName;
    }

    public static String getUserLastName() {
        return userLastName;
    }

    public static void verifySignIn(Context context, Activity caller){
        if (userEmail == null){
            Toast.makeText(context, "You have signed out. Please sign in again.", Toast.LENGTH_LONG).show();
            Intent back = new Intent(context, MainActivity.class);
            caller.startActivity(back);
        }
    }

    class LanguageProgress {
        String languageName;
    }
}
