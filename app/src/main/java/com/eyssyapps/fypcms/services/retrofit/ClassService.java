package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.StudentPerson;
import com.eyssyapps.fypcms.services.RetrofitProviderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by eyssy on 07/04/2016.
 */
public interface ClassService
{
    @GET("class/ClassmatesForStudent/{id}")
    Call<List<StudentPerson>> getClassmatesForStudent(@Path ("id") int studentId, @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);

    @GET("class/ClassForStudent/{id}")
    Call<Class> getClassforStudent(@Path ("id") int studentId, @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);
}
