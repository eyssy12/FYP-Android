package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 06/04/2016.
 */
public class RefreshTokenRequest
{
    private String accessToken;

    public RefreshTokenRequest(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getAccessToken()
    {
        return accessToken;
    }
}