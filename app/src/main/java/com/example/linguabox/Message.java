package com.example.linguabox;

// Message.java
public class Message {
    private String text; // message body
    private boolean belongsToCurrentUser; // is this message sent by us?

    /**
     * Constructor
     * @param text the message in String format
     * @param belongsToCurrentUser to determine UI placement
     */
    public Message(String text, boolean belongsToCurrentUser) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    /**
     * Text getter method
     * @return the text message
     */
    public String getText() {
        return text;
    }

    /**
     * Boolean getter
     * @return the boolean variable used for determining the source of the message (client or server)
     */
    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}