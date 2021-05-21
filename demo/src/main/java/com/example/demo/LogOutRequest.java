package com.example.demo;

public class LogOutRequest {
    private String clientId;
    private String refreshToken;
    
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LogOutRequest() {
    }
    
    public LogOutRequest(String clientId, String refreshToken) {
        this.clientId = clientId;
        this.refreshToken = refreshToken;
    }

    
    
}
