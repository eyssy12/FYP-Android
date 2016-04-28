package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 06/04/2016.
 */
public class IdTokenResponse
{
    private String id_token,
            id_token_expiry;

    public IdTokenResponse(String id_token, String id_token_expiry)
    {
        this.id_token = id_token;
        this.id_token_expiry = id_token_expiry;
    }

    public String getIdToken()
    {
        return id_token;
    }

    public String getIdTokenExpiry()
    {
        return id_token_expiry;
    }
}
