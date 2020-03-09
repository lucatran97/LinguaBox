package com.example.linguabox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChatActivity extends Activity {
    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private ExecutorService es;

    /**
     * First function called on activity creation
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
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
            Future<String> result = es.submit(new SendRun(message));
            try {
                displayMessage(result.get(),false);
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
        Message parseMess = new Message(message, belongsToCurrentUser);
        messageAdapter.add(parseMess);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    private class SendRun implements Callable<String> {
        String message;
        public SendRun(String message){
            this.message = message;
        }

        @Override
        public String call() throws Exception {
            return HttpRequest.sendMessage("1", message);
        }
    }
}