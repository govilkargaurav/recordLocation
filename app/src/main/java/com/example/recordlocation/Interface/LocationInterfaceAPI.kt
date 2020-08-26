package com.example.recordlocation.Interface

import com.example.recordlocation.Model.LocationModel
import com.example.recordlocation.Model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationInterfaceAPI {
    @POST("recordLocation")
    fun postLocation (@Body requestParam: LocationModel) : Call<User>

}