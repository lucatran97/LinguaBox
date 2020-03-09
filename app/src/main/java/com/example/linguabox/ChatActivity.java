package com.example.linguabox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Random;

public class ChatActivity extends Activity {
    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editText = findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            displayMessage(message);
            editText.getText().clear();
        }
    }

    private void displayMessage(String message){
        Message parseMess = new Message(message, true);
        messageAdapter.add(parseMess);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }
}
