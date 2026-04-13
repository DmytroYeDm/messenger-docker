package org.messenger.messenger.server;

import org.messenger.messenger.models.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class MessageDaoImpl implements MessageDao{
    @Override
    public void saveMessage(Message message) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction trans = session.beginTransaction();
        session.save(message);
        trans.commit();
        session.close();
    }

    @Override
    public List<Message> findMessagesBetweenUsers(String user1, String user2) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Message> messages = session.createQuery("SELECT m FROM Message m WHERE (m.userFrom = :user1 AND " +
                        "m.userTo = :user2) OR (m.userFrom = :user2 AND m.userTo = :user1) ORDER BY m.id ASC", Message.class)
                .setParameter("user1", user1)
                .setParameter("user2", user2)
                .getResultList();
        session.close();
        return messages;
    }
}