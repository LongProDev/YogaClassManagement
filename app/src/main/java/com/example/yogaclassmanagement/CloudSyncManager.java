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
        if (connectivityManager == null) return false;
        
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
                if (localClasses == null) {
                    callback.onError("Failed to retrieve local classes");
                    return;
                }

                for (YogaClass yogaClass : localClasses) {
                    if (yogaClass != null) {
                        syncYogaClass(yogaClass);
                    }
                }

                // Sync class instances
                for (YogaClass yogaClass : localClasses) {
                    if (yogaClass != null) {
                        List<YogaClassInstance> instances =
                                dbHelper.getClassInstancesForClass(yogaClass.getId());
                        if (instances != null) {
                            for (YogaClassInstance instance : instances) {
                                if (instance != null) {
                                    syncClassInstance(instance);
                                }
                            }
                        }
                    }
                }

                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Sync failed: " + e.getMessage());
            }
        }).start();
    }

    private void syncYogaClass(YogaClass yogaClass) throws Exception {
        if (yogaClass == null) throw new Exception("Invalid yoga class data");

        retrofit2.Response<YogaClass> response;
        if (yogaClass.getId() == 0) {
            response = apiService.createClass(yogaClass).execute();
        } else {
            response = apiService.updateClass(yogaClass.getId(), yogaClass).execute();
        }

        if (response == null || !response.isSuccessful() || response.body() == null) {
            throw new Exception("Failed to sync yoga class: " + 
                (response != null ? response.code() : "null response"));
        }
    }

    private void syncClassInstance(YogaClassInstance instance) throws Exception {
        if (instance == null) throw new Exception("Invalid class instance data");

        retrofit2.Response<YogaClassInstance> response;
        if (instance.getId() == 0) {
            response = apiService.createInstance(instance).execute();
        } else {
            response = apiService.updateInstance(instance.getId(), instance).execute();
        }

        if (response == null || !response.isSuccessful() || response.body() == null) {
            throw new Exception("Failed to sync class instance: " + 
                (response != null ? response.code() : "null response"));
        }
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(String message);
    }
}
