package org.messenger.messenger.messages;

public class LoginMessage extends BaseMessage {
    private String username;
    private String password;

    public LoginMessage(String username, String password) {
        super("login");
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
