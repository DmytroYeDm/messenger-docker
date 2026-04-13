package org.messenger.messenger.messages;

import java.util.List;

public class ServerInitUsersMessage extends BaseMessage{
    private List<String> usernames;

    public ServerInitUsersMessage() {
        super("initUsers");
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
    public List<String> getUsernames() {
        return usernames;
    }
}
