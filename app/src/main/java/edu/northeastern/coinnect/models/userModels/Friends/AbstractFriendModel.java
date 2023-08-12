package edu.northeastern.coinnect.models.userModels.Friends;

import java.util.Random;

public abstract class AbstractFriendModel {
    private final String username;
    public AbstractFriendModel(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}
