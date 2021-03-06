package com.example.linguabox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle displaying messages
 */
public class MessageAdapter extends BaseAdapter {

        List<Message> messages = new ArrayList<>();
        String difficulty;
        Context context;

        public MessageAdapter(Context context, String difficulty) {
            this.context = context;
            this.difficulty = difficulty;
        }

        public void add(Message message) {
            this.messages.add(message);
            notifyDataSetChanged(); // to render the list we need to notify
        }

        public void set(int pos, Message message) {
            this.messages.set(pos, message);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            MessageViewHolder holder = new MessageViewHolder();
            LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            Message message = messages.get(i);
            int anger;
            if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getText());
            } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.avatar = (View) convertView.findViewById(R.id.avatar);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                if(difficulty.equals("Basic")){
                    holder.name.setText("Rose");
                } else if (difficulty.equals("Intermediate")) {
                    holder.name.setText("Lingua");
                } else {
                    holder.name.setText("Franca");
                }

                holder.messageBody.setText(message.getText());
                GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
                drawable.setColor(Color.parseColor("blue"));
            }

            return convertView;
        }

    }

/**
 * This object holds the message. Includes user avatar, user name, and the message itself.
 */
class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
    }
