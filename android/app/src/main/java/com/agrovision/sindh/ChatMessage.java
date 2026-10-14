package com.agrovision.sindh;

/**
 * Model representing a single message in the AI Farmer Assistant conversation.
 */
public class ChatMessage {

    public static final int TYPE_USER = 1;
    public static final int TYPE_ASSISTANT = 2;

    private final String id;
    private final String text;
    private final int type;
    private final long timestamp;
    private boolean isError;

    public ChatMessage(String text, int type) {
        this(java.util.UUID.randomUUID().toString(), text, type, false);
    }

    public ChatMessage(String text, int type, boolean isError) {
        this(java.util.UUID.randomUUID().toString(), text, type, isError);
    }

    public ChatMessage(String id, String text, int type, boolean isError) {
        this.id = id != null ? id : java.util.UUID.randomUUID().toString();
        this.text = text;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isError = isError;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}
