package org.messenger.messenger.messages;

public class RegisterMessage extends BaseMessage {
    private String username;
    private String password;
    private String repeatPassword;

    public RegisterMessage(String username, String password, String repeatPassword) {
        super("registration");
        this.username = username;
        this.password = password;
        this.repeatPassword = repeatPassword;
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

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
