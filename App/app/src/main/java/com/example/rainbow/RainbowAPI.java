package com.example.rainbow;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RainbowAPI {

    @GET("goal")
    Call<PostItem> getGoalTime(@Query("target") String target);

    @GET("time")
    Call<PostItem> getStudyTime(@Query("target") String target);

    @GET("interrupt")
    Call<PostItem> getInterruptTime(@Query("target") String target);

    @GET("achievement")
    Call<PostItemStringList> getAchievementList(@Query("achieved") int target0, @Query("start") String target1, @Query("end") String target2);

    //@FormUrlEncoded
    @POST("goal")
    //Call<PostItemGoal> postGoal(@Field("date") String date, @Field("time") int time);
    Call<PostItemGoal> postGoal(@Body PostGoal newgoal);

    @GET("estimation")
    Call<PostItemEstimation> getInterruptTime(@Query("start") String target1, @Query("end") String target2);
}
