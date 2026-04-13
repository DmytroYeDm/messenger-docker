package org.messenger.messenger.messages;

import org.messenger.messenger.models.Message;

import java.util.List;

public class ServerGetChatMessagesMessage extends BaseMessage {
    private List<Message> chatMessages;

    public ServerGetChatMessagesMessage(List<Message> chatMessages) {
        super("sendChatMessages");
        this.chatMessages = chatMessages;
    }

    public List<Message> getChatMessages() {
        return chatMessages;
    }
}
