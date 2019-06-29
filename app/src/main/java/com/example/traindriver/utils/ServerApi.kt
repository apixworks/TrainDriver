package com.example.traindriver.utils

import com.example.traindriver.models.DriverResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ServerApi {

    @FormUrlEncoded
    @POST("login/driver")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<DriverResponse>

    @FormUrlEncoded
    @POST("arrival/arrival.php")
    fun notifyUser(
        @Field("station") email: String,
        @Field("time") password: String
    ): Call<String>
}