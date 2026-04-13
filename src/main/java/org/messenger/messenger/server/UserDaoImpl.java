package org.messenger.messenger.server;

import org.messenger.messenger.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public void saveUser(User user) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction trans = session.beginTransaction();
        session.save(user);
        trans.commit();
        session.close();
    }

    @Override
    public void updateUser(User user) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction trans = session.beginTransaction();
        session.update(user);
        trans.commit();
        session.close();
    }

    @Override
    public User findUserByUsername(String username) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        User user = session.createQuery("FROM User u WHERE u.username = :username", User.class).
                setParameter("username", username).uniqueResult();
        session.close();
        return user;
    }

    @Override
    public User findUserByUsernameAndPassword(String username, String password) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        User user = session.createQuery("FROM User u WHERE u.username = :username AND u.password = :password", User.class).
                setParameter("username", username).setParameter("password", password).uniqueResult();
        session.close();
        return user;
    }
    public List<String> findAllUsernames() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<String> users = session.createQuery("SELECT u.username FROM User u", String.class).list();
        session.close();
        return users;
    }

    @Override
    public List<String> findAllUsernamesExceptCurrent(String username) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<String> users = session.createQuery("SELECT u.username FROM User u WHERE u.username != :username", String.class)
                .setParameter("username", username).list();
        session.close();
        return users;
    }
}
