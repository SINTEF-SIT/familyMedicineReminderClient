package com.example.sondrehj.familymedicinereminderclient.api;

import com.example.sondrehj.familymedicinereminderclient.models.Medication;
import com.example.sondrehj.familymedicinereminderclient.models.Message;
import com.example.sondrehj.familymedicinereminderclient.models.Reminder;
import com.example.sondrehj.familymedicinereminderclient.models.TransportReminder;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * API endpoint definitions
 *
 * Created by nikolai on 04/04/16.
 */
public interface MyCyFAPPServiceAPI {
    /**
     *
     * REMINDER REQUESTS
     *
     **/
    @GET("user/{userID}/reminder")
    Call<List<TransportReminder>> getUserReminderList(@Path("userID") String userID);

    @POST("user/{userID}/reminder")
    Call<TransportReminder> createReminder(@Path("userID") String userID, @Body TransportReminder reminder);

    @PUT("user/{userID}/reminder/{reminderID}")
    Call<TransportReminder> updateReminder(@Path("userID") String userID, @Path("reminderID") String reminderID, @Body TransportReminder reminder);

    @DELETE("user/{userID}/reminder/{reminderID}")
    Call<TransportReminder> deleteReminder(@Path("userID") String userID, @Path("reminderID") String reminderID);

    /**
     *
     * MEDICATION REQUESTS
     *
     **/
    @GET("user/{userID}/medication")
    Call<List<Medication>>getUserMedicationList(@Path("userID") String userID);

    @POST("user/{userID}/medication")
    Call<Medication> createMedication(@Path("userID") String userID, @Body Medication medication);

    @PUT("user/{userID}/medication/{medicationID}")
    Call<Medication> updateMedication(@Path("userID") String userID, @Path("medicationID") String medicationID, @Body Medication medication);

    @DELETE("user/{userID}/medication/{medicationID}")
    Call<Medication> deleteMedication(@Path("userID") String userID, @Path("medicationID") String medicationID);

    //https://developers.google.com/cloud-messaging/network-manager#schedule_a_persistent_task

    /**
     *
     * USER REQUESTS
     *
     **/
    @GET("user/{userID}/children")
    Call<User> getChildren(@Path("userID") String userID);

    @POST("user")
    Call<User> createUser(@Body User user);

    @POST("user/{userID}/token/{token}")
    Call<User> associateToken(@Path("userID") String userID, @Path("token") String token);

    @PUT("user/{userID}/guardian/{gracePeriod}")
    Call<User> setGracePeriod(@Path("userID") String userID, @Path("gracePeriod") String gracePeriod);

    /**
     *
     * LINKING REQUESTS
     *
     */
    @POST("user/{userID}/linking/{withID}")
    Call<Message> sendLinkingRequest(@Path("userID") String userID, @Path("withID") String withID);

    @POST("user/{userID}/linkingresponse/{response}")
    Call<Message> responseToLinkingRequest(@Path("userID") String userID, @Path("response") String response);

    /**
     * POLLING REQUESTS
     */
    @HEAD("api/polling")
    Call<Void> sendPollingRequest();
}
