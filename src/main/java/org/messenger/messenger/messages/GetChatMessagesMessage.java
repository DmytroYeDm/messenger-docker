package org.messenger.messenger.messages;

public class GetChatMessagesMessage extends BaseMessage {
    private String userFrom;
    private String userTo;

    public GetChatMessagesMessage(String userFrom, String userTo) {
        super("getChatMessages");
        this.userFrom = userFrom;
        this.userTo = userTo;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public String getUserTo() {
        return userTo;
    }
}
