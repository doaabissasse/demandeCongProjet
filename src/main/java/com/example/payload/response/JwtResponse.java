// JwtResponse.java
package com.example.payload.response;

import java.util.List;

public class JwtResponse {
    private String token;
    private String id;
    private String username;

    public JwtResponse(String token, String id, String username) {
        this.token = token;
        this.id = id;
        this.username = username;
    }

    // Getters and Setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username= username;
    }

}
