package org.messenger.messenger.messages;

public class NewUserMessage extends BaseMessage {
    private String newUserUsername;

    public NewUserMessage(String newUserUsername) {
        super("newUserMessage");
        this.newUserUsername = newUserUsername;
    }

    public String getNewUserUsername() {
        return newUserUsername;
    }
}
