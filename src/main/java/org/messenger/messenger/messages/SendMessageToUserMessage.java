package org.messenger.messenger.messages;

public class SendMessageToUserMessage extends BaseMessage {
    private String userFrom;
    private String userTo;
    private String message;
    private String time;

    public SendMessageToUserMessage(String userFrom, String userTo, String message, String time) {
        super("sendMessage");
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.message = message;
        this.time = time;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
