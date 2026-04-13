package org.messenger.messenger.messages;

public class ServerMessage extends BaseMessage {
    private String details;
    public ServerMessage() {
        super("serverMessage");
    }
    public ServerMessage(String type, String details) {
        super(type);
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
