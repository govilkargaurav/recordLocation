package com.example.recordlocation.Model
import com.google.gson.annotations.SerializedName

class User(

    val code : Int,
    val message : String,
    val token : String,
    val username : String,
    @SerializedName("user_id") val userId: String
)

class LoginRequest(
    val email : String,
    val password : String
)

class LocationModel(
    val strlatitude : String,
    val strlongitude : String


)

class LocationResponseModel (
    val strcode : Int,
    val strresponse : String
)