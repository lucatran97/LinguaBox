package com.example.linguabox;

// Message.java
public class Message {
    private String text; // message body
    private String translation;
    private boolean belongsToCurrentUser; // is this message sent by us?

    /**
     * Constructor
     * @param text the message in String format
     * @param belongsToCurrentUser to determine UI placement
     */
    public Message(String text, String translation, boolean belongsToCurrentUser) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.translation = translation;
    }

    /**
     * Text getter method
     * @return the text message
     */
    public String getText() {
        return text;
    }

    public void swapDisplay() {
        String temp = this.text;
        this.text = this.translation;
        this.translation = temp;
    }

    /**
     * Boolean getter
     * @return the boolean variable used for determining the source of the message (client or server)
     */
    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}