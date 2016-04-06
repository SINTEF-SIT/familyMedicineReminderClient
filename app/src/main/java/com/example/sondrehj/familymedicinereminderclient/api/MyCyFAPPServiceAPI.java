package com.example.sondrehj.familymedicinereminderclient.api;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by nikolai on 04/04/16.
 */
public interface MyCyFAPPServiceAPI {

    //REMINDERS
    @GET("user/{id}/reminder")
    Call<List<Reminder>> getUserReminderList(@Path("id") String userID);

    @POST("user/{userID}/reminder")
    Call<Reminder> createReminder(@Path("userID") String userID, @Body Reminder reminder);

    @PUT("user/{userID}/reminder/{reminderID}")
    Call<Reminder> updateReminder(@Path("userID") String userID, @Path("reminderID") String reminderID, @Body Reminder reminder);

    @DELETE("user/{userID}/reminder/{reminderID}")
    Call<Reminder> deleteReminder(@Path("userID") String userID, @Path("reminderID") String reminderID);


    //MEDICATIONS

    //USER
    @GET("user/{id}/children")
    Call<User> getChildren(@Path("id") String userID);

    @POST("user/{id}/children")
    Call<User> addChild(@Path("id") String userID);

    @POST("user")
    Call<User> createUser(@Body User user);



}
