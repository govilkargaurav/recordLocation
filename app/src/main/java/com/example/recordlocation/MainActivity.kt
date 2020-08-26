package com.example.recordlocation

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.recordlocation.LocationServiceBroadcaster.LocationService
import com.example.recordlocation.Model.LocationModel
import com.example.recordlocation.Model.LoginRequest
import com.example.recordlocation.Model.User
import com.example.recordlocation.RetrofitSingleton.APISingletonObject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.*

class MainActivity : AppCompatActivity() {
    private var locationManager : LocationManager? = null
    lateinit var locationRequest : LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    companion object{
        var instanse : MainActivity? = null
        fun getInstance(): MainActivity? {
            return instanse
        }

    }

    fun updateLocation(value: LocationModel){
        this@MainActivity.runOnUiThread{
            RLtextView.text = value.strlatitude.toString()
            RLtextView.isVisible = true

        }

        APISingletonObject.instance.recorduserLocation(value)
            .enqueue(object : Callback<LocationModel> {
                override fun onResponse(call: Call<LocationModel>, response: Response<LocationModel>) {
                    RLtextView.text = response.body()?.strlatitude ?: String()
                    RLtextView.isVisible = true
                }

                override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                    RLtextView.text = "Something went wrong, Please try again"
                    RLtextView.isVisible = true
                }
            })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instanse = this

        Dexter.withContext(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener, MultiplePermissionsListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(applicationContext,"Granted Permission", Toast.LENGTH_LONG)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(applicationContext,"You must accept this permission in order to record your location", Toast.LENGTH_LONG)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                }
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    Toast.makeText(applicationContext,"Granted Permission", Toast.LENGTH_LONG)
                    updateLocation()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(applicationContext,"Denied Permission", Toast.LENGTH_LONG)
                }

            }).check()


    }

    private fun updateLocation() {
        buildLocationRequest()
        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this@MainActivity,LocationService::class.java)
        intent.setAction(LocationService.ACTION_UPDATE_PROCESS)
        return PendingIntent.getBroadcast(this@MainActivity,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest(){

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval=5000
        locationRequest.smallestDisplacement = 10f
    }

    fun loginBtnClicked(view: View) {
        val requestParam = LoginRequest(
            editTextTextPersonName.text.toString(),
            editTextTextPassword2.text.toString()
        )

        APISingletonObject.instance.getLoggedinUser(requestParam)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    RLtextView.text = response.body()?.username ?: String()
                    RLtextView.isVisible = true
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    RLtextView.text = "Something went wrong, Please try again"
                    RLtextView.isVisible = true
                }
            })
    }
}



