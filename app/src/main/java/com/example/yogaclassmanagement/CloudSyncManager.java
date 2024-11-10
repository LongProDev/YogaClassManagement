package com.example.yogaclassmanagement;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

public class CloudSyncManager {
    private static final String BASE_URL = "https://yogaclassmanagement.onrender.com/";
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final ApiService apiService;

    public CloudSyncManager(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(ApiService.class);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void syncAllData(SyncCallback callback) {
        if (!isNetworkAvailable()) {
            callback.onError("No network connection available");
            return;
        }

        new Thread(() -> {
            try {
                // Sync yoga classes
                List<YogaClass> localClasses = dbHelper.getAllYogaClasses();
                for (YogaClass yogaClass : localClasses) {
                    syncYogaClass(yogaClass);
                }

                // Sync class instances
                for (YogaClass yogaClass : localClasses) {
                    List<YogaClassInstance> instances =
                            dbHelper.getClassInstancesForClass(yogaClass.getId());
                    for (YogaClassInstance instance : instances) {
                        syncClassInstance(instance);
                    }
                }

                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Sync failed: " + e.getMessage());
            }
        }).start();
    }

    private void syncYogaClass(YogaClass yogaClass) throws Exception {
        retrofit2.Response<YogaClass> response;
        if (yogaClass.getId() == 0) {
            // New class, create it
            response = apiService.createClass(yogaClass).execute();
        } else {
            // Existing class, update it
            response = apiService.updateClass(yogaClass.getId(), yogaClass).execute();
        }

        if (!response.isSuccessful()) {
            throw new Exception("Failed to sync yoga class");
        }
    }

    private void syncClassInstance(YogaClassInstance instance) throws Exception {
        retrofit2.Response<YogaClassInstance> response;
        if (instance.getId() == 0) {
            // New instance, create it
            response = apiService.createInstance(instance).execute();
        } else {
            // Existing instance, update it
            response = apiService.updateInstance(instance.getId(), instance).execute();
        }

        if (!response.isSuccessful()) {
            throw new Exception("Failed to sync class instance");
        }
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(String message);
    }
}
