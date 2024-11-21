package com.example.yogaclassmanagement;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;
import android.util.Log;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class CloudSyncManager {
    private static final String BASE_URL = "https://yogaclassmanagement.onrender.com/api/";
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_RETRIES = 3;
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final ApiService apiService;

    public CloudSyncManager(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
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

    private <T> T executeWithRetry(RetryableOperation<T> operation) throws Exception {
        Exception lastException = null;
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                if (i < MAX_RETRIES - 1) {
                    // Exponential backoff
                    Thread.sleep((long) (1000 * Math.pow(2, i)));
                }
            }
        }
        throw new Exception("Operation failed after " + MAX_RETRIES + " attempts: " + 
            lastException.getMessage());
    }

    private boolean isValidObjectId(String id) {
        return id != null && id.matches("^[0-9a-fA-F]{24}$");
    }

    private void syncYogaClass(YogaClass yogaClass) throws Exception {
        if (yogaClass == null) throw new Exception("Invalid yoga class data");
        if (!isValidObjectId(yogaClass.getId())) {
            Log.d("CloudSync", "Invalid ID format, creating new class");
            executeWithRetry(() -> {
                retrofit2.Response<YogaClass> response = apiService.createClass(yogaClass).execute();
                if (!response.isSuccessful()) {
                    String errorBody = response.errorBody() != null ? 
                        response.errorBody().string() : "Unknown error";
                    throw new Exception("Server error: " + response.code() + " - " + errorBody);
                }
                if (response.body() != null && response.body().getId() != null) {
                    yogaClass.setId(response.body().getId());
                }
                return null;
            });
            return;
        }

        executeWithRetry(() -> {
            retrofit2.Response<YogaClass> response;
            try {
                Log.d("CloudSync", "Attempting to update class: " + yogaClass.getId());
                response = apiService.updateClass(yogaClass.getId(), yogaClass).execute();
                
                if (response.code() == 404) {
                    Log.d("CloudSync", "Class not found, creating new: " + yogaClass.getType());
                    response = apiService.createClass(yogaClass).execute();
                }

                if (!response.isSuccessful()) {
                    String errorBody = response.errorBody() != null ? 
                        response.errorBody().string() : "Unknown error";
                    Log.e("CloudSync", "Server error: " + response.code() + " - " + errorBody);
                    throw new Exception("Server error: " + response.code() + " - " + errorBody);
                }

                if (response.body() != null && response.body().getId() != null) {
                    yogaClass.setId(response.body().getId());
                    Log.d("CloudSync", "Successfully synced class with ID: " + yogaClass.getId());
                }
            } catch (Exception e) {
                Log.e("CloudSync", "Error syncing class: " + e.getMessage());
                throw e;
            }
            return null;
        });
    }

    private void syncClassInstance(YogaClassInstance instance) throws Exception {
        if (instance == null) throw new Exception("Invalid class instance data");
        
        // Validate both instance ID and yogaClassId
        if (!isValidObjectId(instance.getId()) || !isValidObjectId(instance.getYogaClassId())) {
            Log.d("CloudSync", "Invalid ID format, creating new instance");
            executeWithRetry(() -> {
                retrofit2.Response<YogaClassInstance> response = apiService.createInstance(instance).execute();
                if (!response.isSuccessful()) {
                    String errorBody = response.errorBody() != null ? 
                        response.errorBody().string() : "Unknown error";
                    Log.e("CloudSync", "Server error: " + response.code() + " - " + errorBody);
                    throw new Exception("Server error: " + response.code() + " - " + errorBody);
                }
                if (response.body() != null && response.body().getId() != null) {
                    instance.setId(response.body().getId());
                    Log.d("CloudSync", "Successfully created instance with ID: " + instance.getId());
                }
                return null;
            });
            return;
        }

        executeWithRetry(() -> {
            retrofit2.Response<YogaClassInstance> response;
            try {
                Log.d("CloudSync", "Attempting to update instance: " + instance.getId());
                response = apiService.updateInstance(instance.getId(), instance).execute();
                
                if (response.code() == 404) {
                    Log.d("CloudSync", "Instance not found, creating new");
                    response = apiService.createInstance(instance).execute();
                }

                if (!response.isSuccessful()) {
                    String errorBody = response.errorBody() != null ? 
                        response.errorBody().string() : "Unknown error";
                    Log.e("CloudSync", "Server error: " + response.code() + " - " + errorBody);
                    throw new Exception("Server error: " + response.code() + " - " + errorBody);
                }

                if (response.body() != null && response.body().getId() != null) {
                    instance.setId(response.body().getId());
                    Log.d("CloudSync", "Successfully synced instance with ID: " + instance.getId());
                }
            } catch (Exception e) {
                Log.e("CloudSync", "Error syncing instance: " + e.getMessage());
                throw e;
            }
            return null;
        });
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(String message);
    }

    @FunctionalInterface
    private interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
