package org.messenger.messenger.messages;

public class InitializeUsersMessage extends BaseMessage {
    private String username;

    public InitializeUsersMessage(String username) {
        super("initializeUsers");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
