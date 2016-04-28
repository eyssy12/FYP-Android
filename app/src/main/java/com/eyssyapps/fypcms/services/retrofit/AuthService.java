package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.AuthRequest;
import com.eyssyapps.fypcms.models.IdTokenResponse;
import com.eyssyapps.fypcms.models.RefreshTokenRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by eyssy on 04/04/2016.
 */
public interface AuthService
{
    @POST("auth")
    Call<ResponseBody> authenticate(@Body AuthRequest request);

    @POST("auth/newidtoken")
    Call<IdTokenResponse> refreshIdtoken(@Body RefreshTokenRequest request);
}