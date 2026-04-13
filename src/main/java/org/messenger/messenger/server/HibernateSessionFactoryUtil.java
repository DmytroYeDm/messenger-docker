package org.messenger.messenger.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.messenger.messenger.models.*;
import org.hibernate.SessionFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateSessionFactoryUtil {
    private static volatile SessionFactory sessionFactory;
    public HibernateSessionFactoryUtil() {

    }
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateSessionFactoryUtil.class) {
                if (sessionFactory == null) {
                    try {
                        Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
                        String dbUrl = dotenv.get("DB_URL");
                        System.out.println("DEBUG: Loaded DB_URL from .env: " + dbUrl);
                        Configuration configuration = new Configuration().configure();
                        configuration.addAnnotatedClass(User.class);
                        configuration.addAnnotatedClass(Message.class);
                        configuration.setProperty("hibernate.connection.url",
                                dotenv.get("DB_URL") != null ? dotenv.get("DB_URL") : System.getenv("DB_URL"));
                        configuration.setProperty("hibernate.connection.username",
                                dotenv.get("DB_USER") != null ? dotenv.get("DB_USER") : System.getenv("DB_USER"));
                        configuration.setProperty("hibernate.connection.password",
                                dotenv.get("DB_PASSWORD") != null ? dotenv.get("DB_PASSWORD") : System.getenv("DB_PASSWORD"));
                        StandardServiceRegistryBuilder builder =
                                new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                        sessionFactory = configuration.buildSessionFactory(builder.build());
                    } catch (Exception e) {
                        System.err.println("Помилка при створенні підключення до Hibernate: " + e.getMessage());
                    }
                }
            }

        }
        return sessionFactory;
    }
}
