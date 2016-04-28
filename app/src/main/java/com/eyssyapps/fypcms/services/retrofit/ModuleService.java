package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.Class;
import com.eyssyapps.fypcms.models.Module;
import com.eyssyapps.fypcms.services.RetrofitProviderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by eyssy on 07/04/2016.
 */
public interface ModuleService
{
    @GET("module/ForStudent/{id}")
    Call<Class> getModulesForStudent(@Path ("id") int studentId, @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);

    @GET("module/ForLecturer/{id}")
    Call<List<Module>> getModulesForLecturer(@Path ("id") int studentId, @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);
}