package com.example.recordlocation.Interface

import com.example.recordlocation.Model.LocationModel
import com.example.recordlocation.Model.LocationResponseModel
import com.example.recordlocation.Model.LoginRequest
import com.example.recordlocation.Model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface InterfaceAPI {


@POST("setGeoLocation.php")
fun recorduserLocation (@Body requestParam: LocationModel) : Call<LocationResponseModel>

@POST("auth.php")
fun getLoggedinUser (@Body requestParam: LoginRequest) : Call<User>
}