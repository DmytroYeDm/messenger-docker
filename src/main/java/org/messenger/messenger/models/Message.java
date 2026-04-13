package org.messenger.messenger.models;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userfrom")
    private String userFrom;
    @Column(name = "userto")
    private String userTo;
    @Column(name = "message")
    private String message;
    @Column(name = "time")
    private LocalTime time;

    public Message() {
    }

    public Message(String userFrom, String userTo, String message, LocalTime time) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.message = message;
        this.time = time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
