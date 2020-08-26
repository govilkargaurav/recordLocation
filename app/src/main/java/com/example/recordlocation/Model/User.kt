package com.example.recordlocation.Model
import com.google.gson.annotations.SerializedName

class User(
    val login : Boolean,
    val userId : Int,
    val userName : String
)

class LoginRequest(
    val userName : String,
    val userPassword : String
)

class LocationModel(
    var userName : String,
    var userId : Int,
    val lat : String,
    val lng : String
)

class LocationResponseModel (
    val status : String
)