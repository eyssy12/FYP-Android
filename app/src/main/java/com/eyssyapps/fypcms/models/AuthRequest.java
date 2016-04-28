package com.eyssyapps.fypcms.models;

/**
 * Created by eyssy on 04/04/2016.
 */
public class AuthRequest
{
    public String Username,
        Password;

    public AuthRequest(String username, String password)
    {
        this.Username = username;
        this.Password = password;
    }
}