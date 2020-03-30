package com.example.linguabox;

import androidx.core.app.ActivityCompat;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognitionResult;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer;
import static android.Manifest.permission.*;

public class ChatActivity extends AppCompatActivity implements HelperDialogFragment.HelperDialogListener {
    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private ExecutorService es;
    String languageTranslator;
    String languageSpeech;
    String name;
    String email;
    int selectedMessagePos = -1;
    Set<Integer> translatedMessage;
    Message selectedMessage;
  
    private static String speechSubscriptionKey = "9d1cb6dc1aff4b6ab5ab311b84f642a5";
    private static String serviceRegion = "eastus";
    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;

    /**
     * First function called on activity creation
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = this.getIntent();
        UserAccount.verifySignIn(getApplicationContext(), this);
        languageTranslator = intent.getStringExtra("language_translator");
        languageSpeech = intent.getStringExtra("language_speech");
        email = UserAccount.getUserEmail();
        name = UserAccount.getUserGivenName();
        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        messagesView.setLongClickable(true);
        translatedMessage = new HashSet<>();


        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        //LONG CLICK FUNCTION HERE
        messagesView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                Message temp = (Message) arg0.getItemAtPosition(pos);
                if (!temp.isBelongsToCurrentUser()){
                    selectedMessage = temp;
                    selectedMessagePos = pos;
                    showCustomDialog();
                }
                return true;
            }
        });

        es = Executors.newSingleThreadExecutor();
    }

    /**
     * This is called when user press the Send button. It displays the message on the screen and sends it to server
     *
     * @param view
     */
    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            displayMessage(message, true);
            editText.getText().clear();
            //REMINDER: SHOULD ESCAPE INPUT AND OUTPUT IN SERVER
            Future<Message> result = es.submit(new SendRun(message));
            try {
                displayMessage(result.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function handles the display of messages
     *
     * @param message              the message to display
     * @param belongsToCurrentUser to change the position of the message on the screen
     */
    private void displayMessage(String message, boolean belongsToCurrentUser) {
        Message parseMess = new Message(message, null, belongsToCurrentUser);
        messageAdapter.add(parseMess);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    /**
     * This function handles the display of server responses. It is an overload of the above function, used for server response and not user input
     * @param message the message returned the server, encapsulated in a Message object
     */
    private void displayMessage(Message message) {
        messageAdapter.add(message);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    /**
     * Additional thread to handle network operation
     */
    private class SendRun implements Callable<Message> {
        String message;
        public SendRun(String message){
            this.message = message;
        }

        @Override
        public Message call() throws Exception {
            return HttpRequest.sendMessage(email, message, languageTranslator);
        }
    }

    /**
     * Displays the dialog when a message is chosen
     */
    public void showCustomDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new HelperDialogFragment();
        dialog.show(getSupportFragmentManager(), "HelperDialogFragment");
    }

    /**
     * The 'positive' option is reserved for displaying translation
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        selectedMessage.swapDisplay();
        if(translatedMessage.contains(selectedMessagePos)){
            translatedMessage.remove(selectedMessagePos);
        } else {
            translatedMessage.add(selectedMessagePos);
        }
        messageAdapter.set(selectedMessagePos,selectedMessage);
        messagesView.setSelection(selectedMessagePos);
    }

    /**
     * The 'neutral' option is reserved for speech service
     * @param dialog
     */
    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        // Initialize speech synthesizer and its dependencies
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        assert(speechConfig != null);
        if(translatedMessage.contains(selectedMessagePos)) {
            speechConfig.setSpeechSynthesisLanguage("en-US");
        } else {
            speechConfig.setSpeechSynthesisLanguage(languageSpeech);
        }
        synthesizer = new SpeechSynthesizer(speechConfig);
        assert(synthesizer != null);
        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event
            SpeechSynthesisResult result = synthesizer.SpeakText(selectedMessage.getText());
            assert(result != null);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                Log.w("STATUS", "SUCCESS");
            }
            else if (result.getReason() == ResultReason.Canceled) {
                Log.w("STATUS", "CANCELED");
            }
            result.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
        }
    }

    /**
     * The 'negative' option dismisses the dialog
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void onSpeechButtonClicked(View v) {
        //Snackbar listening = Snackbar.make(findViewById(R.id.weAreListening), "Listening...", Snackbar.LENGTH_LONG);
        //listening.show();
        TextView txt = (TextView) this.findViewById(R.id.editText); // 'hello' is the ID of your text view
        Log.w("Language Code", "Language Code = " + languageSpeech);
        try {
            SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription(speechSubscriptionKey, serviceRegion);

            assert (config != null);
            String fromLanguage = languageSpeech;
            String toLanguage = languageSpeech;
            Log.w("Language Code", "fromLanguage = " + fromLanguage);
            Log.w("Language Code", "toLanguage = " + toLanguage);
            config.setSpeechRecognitionLanguage(fromLanguage);
            config.addTargetLanguage(toLanguage);

            TranslationRecognizer reco = new TranslationRecognizer(config);
            assert (reco != null);

            Future<TranslationRecognitionResult> task = reco.recognizeOnceAsync();
            assert (task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            TranslationRecognitionResult result = task.get();
            assert (result != null);

            if (result.getReason() == ResultReason.TranslatedSpeech) {
                String rawResult = result.toString();
                String trimmedResult = rawResult.substring(rawResult.indexOf("<") + 1);
                trimmedResult.trim();
                trimmedResult = trimmedResult.split(">")[0];
                txt.setText(trimmedResult);
            } else {

                txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
            }

            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert (false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    /**
     * Menu Option Assignments
     * @param item the option chosen by the user
     * @return true if successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.option_menu:
            Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(menu);
            return(true);
        case R.id.option_language_select:
            Intent languageSelect = new Intent(getApplicationContext(), SelectLanguageActivity.class);
            startActivity(languageSelect);
            return(true);
        case R.id.option_sign_out:
            UserAccount.signOut(getApplicationContext(), ChatActivity.this);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
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
