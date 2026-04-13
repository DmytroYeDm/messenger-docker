package org.messenger.messenger.messages;

public abstract class BaseMessage {
    protected String type;

    public BaseMessage() {}

    public BaseMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
