package com.example.linguabox;

import androidx.core.app.ActivityCompat;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import static android.Manifest.permission.*;

public class ChatActivity extends FragmentActivity implements HelperDialogFragment.HelperDialogListener{
    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private ExecutorService es;
    String language;
    String email;
    int selectedMessagePos;
    Message selectedMessage;

    private static String speechSubscriptionKey = "9d1cb6dc1aff4b6ab5ab311b84f642a5";
    private static String serviceRegion = "eastus";

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

        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        messagesView.setLongClickable(true);


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
                e.printStackTrace();            }
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

    public void showCustomDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new HelperDialogFragment();
        dialog.show(getSupportFragmentManager(), "HelperDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onTranslate(DialogFragment dialog) {
        selectedMessage.swapDisplay();
        messageAdapter.set(selectedMessagePos,selectedMessage);
        messagesView.setSelection(selectedMessagePos);
    }

    @Override
    public void onListen(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void onSpeechButtonClicked(View v) {
        TextView txt = (TextView) this.findViewById(R.id.editText); // 'hello' is the ID of your text view

        try {
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            assert(config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            SpeechRecognitionResult result = task.get();
            assert(result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                txt.setText(result.toString());
            }
            else {
                txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
            }

            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert(false);
        }
    }
}