package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.NewsPost;
import com.eyssyapps.fypcms.services.RetrofitProviderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by eyssy on 06/04/2016.
 */
public interface NewsService
{
    @GET("news")
    Call<List<NewsPost>> getNews(@Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);
}