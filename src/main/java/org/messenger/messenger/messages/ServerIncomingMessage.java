package org.messenger.messenger.messages;

import java.time.LocalTime;

public class ServerIncomingMessage extends BaseMessage{
    private String userFrom;
    private String messageText;
    private LocalTime time;

    public ServerIncomingMessage(String userFrom, String messageText, LocalTime time) {
        super("incomingMessage");
        this.userFrom = userFrom;
        this.messageText = messageText;
        this.time = time;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public String getMessageText() {
        return messageText;
    }

    public LocalTime getTime() {
        return time;
    }
}
