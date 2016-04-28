package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.StudentPerson;
import com.eyssyapps.fypcms.services.RetrofitProviderService;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by eyssy on 04/04/2016.
 */
public interface StudentService
{
    @GET ("student/{id}")
    Call<StudentPerson> getStudent(@Path ("id") int studentId, @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);
}