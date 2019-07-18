package com.example.onlinestore.domain.models.view.users;

public class UserProfileViewModel {

    private String username;
    private String email;

    public UserProfileViewModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
