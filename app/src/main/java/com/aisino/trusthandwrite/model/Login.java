package com.aisino.trusthandwrite.model;

/**
 * Created by HXQ on 2017/4/24.
 */

public class Login {
    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    private String platformName;
    private String clientName;
    private String authCode;

    public Login(String platformName, String clientName, String authCode) {
        this.platformName = platformName;
        this.clientName = clientName;
        this.authCode = authCode;
    }
}
