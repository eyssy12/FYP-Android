package com.eyssyapps.fypcms.models;

import com.eyssyapps.fypcms.services.RetrofitProviderService;

/**
 * Created by eyssy on 08/04/2016.
 */
public class WebApiArguments
{
    private int userId;
    private String token;

    public WebApiArguments(int userId, String token)
    {
        this.userId = userId;
        this.token = token;
    }

    public int getUserId()
    {
        return userId;
    }

    public String getAuthorizedBearer()
    {
        return RetrofitProviderService.buildBearerAuthorizationHeader(token);
    }
}
