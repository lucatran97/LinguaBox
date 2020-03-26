package com.example.linguabox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

public class ChatActivity extends AppCompatActivity implements HelperDialogFragment.HelperDialogListener {
    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private ExecutorService es;
    String language;
    String name;
    String email;
    int selectedMessagePos;
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
        language = intent.getStringExtra("language");
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        messagesView.setLongClickable(true);

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
            return HttpRequest.sendMessage(email, message, language);
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
        speechConfig.setSpeechSynthesisLanguage("es-ES");

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
            menu.putExtra("email", email);
            menu.putExtra("name", name);
            startActivity(menu);
            return(true);
        case R.id.option_language_select:
            Intent languageSelect = new Intent(getApplicationContext(), SelectLanguageActivity.class);
            languageSelect.putExtra("name", name);
            languageSelect.putExtra("email", email);
            startActivity(languageSelect);
            return(true);
        case R.id.option_sign_out:
            Intent signOut = new Intent(getApplicationContext(), MainActivity.class);
            signOut.putExtra("Sign Out", true);
            startActivity(signOut);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }
}