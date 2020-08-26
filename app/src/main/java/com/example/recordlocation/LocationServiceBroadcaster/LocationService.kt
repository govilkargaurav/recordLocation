package com.example.recordlocation.LocationServiceBroadcaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.recordlocation.MainActivity
import com.example.recordlocation.Model.LocationModel
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.lang.StringBuilder

class LocationService : BroadcastReceiver() {

    companion object{
        val ACTION_UPDATE_PROCESS = "com.example.recordlocation.LocationServiceBroadcaster.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null){
                val action = intent!!.action
                if (action.equals(ACTION_UPDATE_PROCESS)){
                    val result = LocationResult.extractResult(intent!!)

                    if (result != null){
                        val location = result.lastLocation
                        val locationString = StringBuilder(location.latitude.toString()).append("/").append(location.latitude.toString())

                        try {
                            MainActivity.getInstance()?.updateLocation(location.latitude.toString(),location.longitude.toString())
                        }catch (e : Exception){
                            Toast.makeText(context,locationString,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }
}
