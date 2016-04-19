package com.example.sondrehj.familymedicinereminderclient.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.example.sondrehj.familymedicinereminderclient.api.MyCyFAPPServiceAPI;
import com.example.sondrehj.familymedicinereminderclient.api.RestService;
import com.example.sondrehj.familymedicinereminderclient.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nikolai on 07/04/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * This function is ran when requestSync is called from anywhere in the
     * android system with the right authority. The functions performs a sync of the data.
     *
     * @param account
     * @param extras
     * @param authority
     * @param provider
     * @param syncResult
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        Log.d("Sync", "Sync is performing");

        /**
         * This is an Rest Api example call - keep outside of UI threads.
         */
        MyCyFAPPServiceAPI apiService = RestService.createRestService();

        User user = new User("Sondre", "Pelle11");
        Call<User> call = apiService.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    int statusCode = response.code();
                    User user = response.body();
                    Log.d("api", statusCode + " : " + user.toString());
                } else {
                    Log.d("api", "error");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("api", "failure");
            }
        });
    }
}
