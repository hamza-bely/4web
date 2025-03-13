package com.microservice.microservice_user_service.model.dto.user;

import com.microservice.microservice_user_service.model.User;

public class UserRegisterResponse {
    private User user;
    private String token;

    public UserRegisterResponse( User user, String token) {
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
