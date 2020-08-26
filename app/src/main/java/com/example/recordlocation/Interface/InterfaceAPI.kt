package com.example.recordlocation.Interface

import com.example.recordlocation.Model.LocationModel
import com.example.recordlocation.Model.LoginRequest
import com.example.recordlocation.Model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.*


interface InterfaceAPI {


@POST("recordlocation")
fun recorduserLocation (@Body requestParam: LocationModel) : Call<LocationModel>

@POST("login")
fun getLoggedinUser (@Body requestParam: LoginRequest) : Call<User>
}