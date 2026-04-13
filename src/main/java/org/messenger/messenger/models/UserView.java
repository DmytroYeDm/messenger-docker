package org.messenger.messenger.models;

import javafx.scene.image.Image;

import java.util.Objects;

public class UserView {
    private final String username;
    private final Image avatar =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/userIcon.png")));

    public UserView(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Image getAvatar() {
        return avatar;
    }
}
