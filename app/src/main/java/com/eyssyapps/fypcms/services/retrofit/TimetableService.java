package com.eyssyapps.fypcms.services.retrofit;

import com.eyssyapps.fypcms.models.CancelledEvent;
import com.eyssyapps.fypcms.models.Event;
import com.eyssyapps.fypcms.models.StudentTimetable;
import com.eyssyapps.fypcms.services.RetrofitProviderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by Rob on 05/04/2016.
 */
public interface TimetableService
{
    @GET ("timetable/GetTimetableForStudent/{id}")
    Call<StudentTimetable> getTimetableForStudent(
        @Path ("id") int studentId,
        @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);

    @GET ("timetable/GetTimetableForLecturer/{id}")
    Call<List<Event>> getTimetableForLecturer(
        @Path ("id") int lecturerId,
        @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);

    @GET("timetable/GetCancelledEventsForStudent/{id}")
    Call<List<CancelledEvent>> getCancelledEventsForStudent(
        @Path ("id") int studentId,
        @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);

    @GET("timetable/GetCancelledEventById/{id}")
    Call<CancelledEvent> getCancelledEventById(
        @Path ("id") int eventId,
        @Header (RetrofitProviderService.HEADER_AUTHORIZATION) String authorizedBearer);
}