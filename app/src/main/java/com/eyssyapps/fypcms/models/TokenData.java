package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 05/04/2016.
 */
public class TokenData
{
    public static final String ENTITY_ID = "entity_id",
        ID_TOKEN = "id_token",
        ID_TOKEN_EXPIRY = "id_token_expiry",
        ACCESS_TOKEN = "access_token",
        ACCESS_TOKEN_EXPIRY = "access_token_expiry";

    private String entityId, idToken, idTokenExpiry, accessToken, accessTokenExpiry;

    public TokenData(String entityId, String idToken, String idTokenExpiry, String accessToken, String accessTokenExpiry)
    {
        this.entityId = entityId;
        this.idToken = idToken;
        this.idTokenExpiry = idTokenExpiry;
        this.accessToken = accessToken;
        this.accessTokenExpiry = accessTokenExpiry;
    }

    public String getEntityId()
    {
        return entityId;
    }

    public String getIdToken()
    {
        return idToken;
    }

    public String getIdTokenExpiry()
    {
        return idTokenExpiry;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public String getAccessTokenExpiry()
    {
        return accessTokenExpiry;
    }
}
