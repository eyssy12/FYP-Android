package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.LecturerPerson;
import com.eyssyapps.fypcms.services.RetrofitProviderService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by eyssy on 04/04/2016.
 */
public interface LecturerService
{
    @GET ("lecturer/{id}")
    Call<LecturerPerson> getLecturer(@Path ("id") int lecturerId, @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);
}
