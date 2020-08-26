package com.example.recordlocation.RetrofitSingleton

import com.example.recordlocation.Interface.InterfaceAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APISingletonObject {

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private  const val BASE_URL = "http://www.coolklik.com/api/"
    val instance : InterfaceAPI by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        retrofit.create(InterfaceAPI::class.java)
    }

}