package com.example.sondrehj.familymedicinereminderclient.playservice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.MainActivity;
import com.example.sondrehj.familymedicinereminderclient.R;
import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class is used to fetch a token from the Google Play GCM services,
 * the token which identifies the android device should then be sent
 * to the back-end and associated with the same user account made by the application.
 *
 *
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // Make a call to Instance API
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // pass along this data
            sendRegistrationToServer(token);
            sharedPreferences.edit().putBoolean(PlayservicePreferences.SENT_TOKEN_TO_SERVER, true).apply();

    } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(PlayservicePreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    /**
     * TODO: Implement this
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        MyCyFAPPServiceAPI apiService = RestService.createRestService();
        Call<User> call = apiService.associateToken(MainActivity.getAccount(getApplicationContext()).name, token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    int statusCode = response.code();
                    User user = response.body();
                    Log.d(TAG, statusCode + " : " + user.toString());
                } else {
                    Log.d(TAG, "error in associateToken call.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "failure in token registration.");
            }
        });
    }
}