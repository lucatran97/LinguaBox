package com.example.linguabox;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] languageNames;
    TextView instructionText;
    LinearLayout progressView;
    ArrayList<LanguageProgress> userProgress = new ArrayList<>();
    private ExecutorService es;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Spinner spin = (Spinner) findViewById(R.id.profileSpinner);
        instructionText = (TextView) findViewById(R.id.profile_instruction);
        progressView = (LinearLayout) findViewById(R.id.language_view);
        es = Executors.newSingleThreadExecutor();
        userProgress = UserAccount.getProgress();
        if(userProgress.size()==0){
            spin.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            instructionText.setText("You have not chatted in any language.");
        } else {
            languageNames = new String[userProgress.size()];
            for (int i = 0; i < userProgress.size(); i++){
                languageNames[i] = userProgress.get(i).getLanguageName();
            }
            spin.setOnItemSelectedListener(this);
            LanguageSelectAdapter customAdapter = new LanguageSelectAdapter(getApplicationContext(), languageNames);
            spin.setAdapter(customAdapter);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(userProgress!=null&&userProgress.size()>0) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView e = new TextView(this);
            LanguageProgress res = userProgress.get(position);
            e.setLayoutParams(lParams);
            e.setText("Last session: " + res.getLastSession().substring(0,10)
            + "\n\nLevel: " + res.getLevel()
            + "\n\nNumber of messages sent: " + res.getMessagesSent()
            + "\n\nCurrent streak (consecutive days of chatting): " + res.getCurrentStreak()
            + "\n\nLongest streak: " + res.getLongestStreak());
            Log.e("Text", (String) res.getLastSession());
            progressView.removeAllViews();
            progressView.addView(e);
        }
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
        case R.id.option_refresh:
            Future<String> result = es.submit(new MongodbLog(UserAccount.getUserEmail()));
            try {
                result.get();
                Toast.makeText(ProfileActivity.this, "Successfully retrieve new data!", Toast.LENGTH_LONG).show();
                recreate();
            } catch (Exception e) {
                Toast.makeText(ProfileActivity.this, "Refresh failed. Cannot fetch your progress.", Toast.LENGTH_LONG).show();
            }
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}
