package com.example.linguabox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserAccount {
    private static String userEmail = null;
    private static String userGivenName = null;
    private static String userLastName = null;
    private static ArrayList<LanguageProgress> userProgress = new ArrayList<>();


    public static void setAccount(String email, String givenName, String lastName){
        userEmail = email;
        userGivenName = givenName;
        userLastName = lastName;
    }

    public static void setProgress(JSONArray array){
        userProgress = new ArrayList<>();
        for (int i = 0; i< array.length(); i++){
            try {
                userProgress.add(new LanguageProgress(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<LanguageProgress> getProgress(){
        return userProgress;
    }

    public static void signOut(Context context, Activity caller){
        userEmail = null;
        userGivenName = null;
        userLastName = null;
        userProgress = new ArrayList<>();
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
}

class LanguageProgress {
    private String lastSession;
    private String languageName;
    private String level;
    private int messagesSent;
    private int currentStreak;
    private int longestStreak;

    public LanguageProgress(JSONObject json){
        try {
            languageName = json.getString("language");
            lastSession = json.getString("last_session");
            messagesSent = json.getInt("messages_sent");
            switch(json.getInt("level")){
                case 1:
                    level = "Basic";
                    break;
                case 2:
                    level = "Intermediate";
                    break;
                case 3:
                    level = "Advanced";
                    break;
            }
            currentStreak = json.getInt("current_streak");
            longestStreak = json.getInt("longest_streak");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLevel(){
        return this.level;
    }
    public String getLanguageName(){
        return this.languageName;
    }

    public String getLastSession(){
        return this.lastSession;
    }

    public int getMessagesSent(){
        return messagesSent;
    }

    public int getCurrentStreak(){
        return this.currentStreak;
    }

    public int getLongestStreak(){
        return this.longestStreak;
    }
}
