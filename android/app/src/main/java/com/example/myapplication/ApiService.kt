package com.example.myapplication

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService{
    //user methods-----------------------------------------------------
    @POST("/auth/register")
    suspend fun register(
        @Body request : RegisterRequest
    )
}