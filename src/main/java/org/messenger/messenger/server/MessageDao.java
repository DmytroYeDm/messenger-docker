package org.messenger.messenger.server;

import org.messenger.messenger.models.Message;

import java.util.List;

public interface MessageDao {
    public void saveMessage (Message message);
    public List<Message> findMessagesBetweenUsers (String userFrom, String userTo);
}
