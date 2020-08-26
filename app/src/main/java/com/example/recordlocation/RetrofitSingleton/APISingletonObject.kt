package com.example.recordlocation.RetrofitSingleton

import com.example.recordlocation.Interface.InterfaceAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APISingletonObject {

    private  const val BASE_URL = "http://192.168.225.153:3001/voteit/user/"
    val instance : InterfaceAPI by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(InterfaceAPI::class.java)
    }

}