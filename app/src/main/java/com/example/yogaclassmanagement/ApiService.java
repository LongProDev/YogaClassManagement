package com.example.yogaclassmanagement;

import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {
    @GET("yoga-classes")
    Call<List<YogaClass>> getAllClasses();

    @POST("yoga-classes")
    Call<YogaClass> createClass(@Body YogaClass yogaClass);

    @PUT("yoga-classes/{id}")
    Call<YogaClass> updateClass(@Path("id") long id, @Body YogaClass yogaClass);

    @DELETE("yoga-classes/{id}")
    Call<Void> deleteClass(@Path("id") long id);

    @GET("class-instances")
    Call<List<YogaClassInstance>> getAllInstances();

    @POST("class-instances")
    Call<YogaClassInstance> createInstance(@Body YogaClassInstance instance);

    @PUT("class-instances/{id}")
    Call<YogaClassInstance> updateInstance(@Path("id") long id, @Body YogaClassInstance instance);

    @DELETE("class-instances/{id}")
    Call<Void> deleteInstance(@Path("id") long id);
}