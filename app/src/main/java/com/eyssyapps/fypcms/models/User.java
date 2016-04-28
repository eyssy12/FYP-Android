package com.eyssyapps.fypcms.models;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by eyssy on 04/04/2016.
 */
public class User
{
    public static final String
        USERNAME = "username",
        USER_TYPE = "user_type",
        USER_ID = "user_id",
        STUDENT_ID = "student_id",
        LECTURER_ID = "lecturer_id";

    private String userId, username, userType;
    private TokenData tokenData;

    public User()
    {
        this("", "", "");
    }

    public User(String userId, String username, String userType)
    {
        this.username = username;
        this.userType = userType;
        this.userId = userId;
    }

    public User(Intent packagedArguments)
    {
        this(packagedArguments.getExtras());
    }

    public User(Bundle packagedArguments)
    {
        this(
            packagedArguments.getString(User.USER_ID),
            packagedArguments.getString(User.USERNAME),
            packagedArguments.getString(User.USER_TYPE));
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public TokenData getTokenData()
    {
        return tokenData;
    }

    public void setTokenData(TokenData tokenData)
    {
        this.tokenData = tokenData;
    }
}
