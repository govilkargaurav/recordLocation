package com.example.recordlocation.Utility

import android.content.Context
import android.location.Location
import android.preference.Preference
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.example.recordlocation.MainActivity
import com.example.recordlocation.Model.LocationModel
import com.example.recordlocation.Model.LocationResponseModel
import com.example.recordlocation.Model.User
import com.example.recordlocation.RetrofitSingleton.APISingletonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessControlContext
import java.text.DateFormat
import java.util.*

object Common {

     val KEY_REQUEST_LOCATION_UPDATE  = "requesting_location_update"


    fun getLocationText(location: Location?): String {
        val userObj : User? = MainActivity.getUserObj()
        if (userObj != null){

            val locParam = LocationModel(
                userObj.userName,
                userObj.userId,
                location?.latitude.toString(),
                location?.longitude.toString()
            )

            APISingletonObject.instance.recorduserLocation(locParam)
                .enqueue(object : Callback<LocationResponseModel> {
                    override fun onResponse(call: Call<LocationResponseModel>, response: Response<LocationResponseModel>) {
                        Log.e("QWER","GGGG")
                    }
                    override fun onFailure(call: Call<LocationResponseModel>, t: Throwable) {
                        Log.e("QWER","QWERTY")
                    }
                })
        }



        return if (location == null){
            "Unknown Location"
        } else {
            "" + location.latitude + "/" + location.longitude
        }

    }

    fun getLocationTitle(context: Context): String {

        return String.format("Location Updated ${DateFormat.getDateInstance().format(Date())}")
    }

    fun setRequestLocationUpdates(context: Context, value: Boolean) {

        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUEST_LOCATION_UPDATE,value)
            .apply()
    }

    fun requestingLocationUpdates(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUEST_LOCATION_UPDATE,false)
    }
}