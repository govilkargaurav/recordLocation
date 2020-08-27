package com.example.recordlocation

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
//import com.example.recordlocation.LocationServiceBroadcaster.LocationService
import com.example.recordlocation.Model.LocationModel
import com.example.recordlocation.Model.LocationResponseModel
import com.example.recordlocation.Model.LoginRequest
import com.example.recordlocation.Model.User
import com.example.recordlocation.RetrofitSingleton.APISingletonObject
import com.example.recordlocation.Utility.BGLocationService
import com.example.recordlocation.Utility.BackgroundLocation
import com.example.recordlocation.Utility.Common
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {


    private var mService: BGLocationService? = null
    private var mBound = false



    companion object{
        var instanse : MainActivity? = null
        var userObj : User? = null
        fun getInstance(): MainActivity? {
            return instanse
        }

        @JvmName("getUserObj1")
        fun getUserObj(): User? {
            return userObj
        }

    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as BGLocationService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mService = null
            mBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this)
            .withPermissions(
                Arrays.asList(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    
                    updateLocation.setOnClickListener {
                        mService!!.requestLocationUpdates()
                    }

                    removeLocationUpdate.setOnClickListener {
                        mService!!.removeLocationUpdates()
                    }

                    setButtonState(Common.requestingLocationUpdates(this@MainActivity))
                    bindService(
                        Intent(this@MainActivity, BGLocationService::class.java),
                        mServiceConnection,
                        Context.BIND_AUTO_CREATE
                    )

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                }

            }).check()

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
            if (p1.equals(Common.KEY_REQUEST_LOCATION_UPDATE))
                setButtonState(p0!!.getBoolean(Common.KEY_REQUEST_LOCATION_UPDATE,false))
    }

    private fun setButtonState(boolean: Boolean) {
        if (boolean){
                removeLocationUpdate.isEnabled = true
                updateLocation.isEnabled = false
        }else{
            removeLocationUpdate.isEnabled = false
            updateLocation.isEnabled = true
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    fun onBackgroundLocationRetrieve(event: BackgroundLocation) {
        if (event.location != null) {
            Toast.makeText(this, Common.getLocationText((event.location)), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
        EventBus.getDefault().register(this)
    }


    override fun onStop() {

        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    fun loginBtnClicked(view: View) {
        val requestParam = LoginRequest(
            editTextTextPersonName.text.toString(),
            editTextTextPassword2.text.toString()
        )
        APISingletonObject.instance.getLoggedinUser(requestParam)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.body()!=null) {
                        userObj = response.body()
                    }
                    RLtextView.text = response.body()?.userName ?: String()
                    RLtextView.isVisible = true

                    editTextTextPersonName.isVisible = false
                    editTextTextPassword2.isVisible = false
                    btnLogin.isVisible = false
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    RLtextView.text = "Something went wrong in Login, Please try again"
                    RLtextView.isVisible = true
                }
            })
    }


}



