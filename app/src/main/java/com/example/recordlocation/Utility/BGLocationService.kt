package com.example.recordlocation.Utility

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.text.style.UpdateAppearance
import android.util.Log
import androidx.core.app.NotificationCompat
//import com.example.recordlocation.LocationServiceBroadcaster.LocationService
import com.example.recordlocation.R
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus

class BGLocationService : Service() {


    companion object{
        private val CHANNEL_ID = "channel_01"
        private val PACKAGE_NAME = "com.example.recordlocation"
        private val EXTRA_STARTED_FROM_NOTIFICATION  = "$PACKAGE_NAME.started_from_notification"
        private val UPDATE_INTERVAL_IN_MIL : Long = 10000
        private val FASTESD_UPDATE_INTERVAL_IN_MIL : Long = (UPDATE_INTERVAL_IN_MIL/2)
        private val NOTIFCATION_ID = 1234

    }

    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder(){
        internal val service : BGLocationService
            get() = this@BGLocationService
    }

    private var mChangingConfiguration = false

    private var mNotificationManager : NotificationManager? = null

    private var locationRequest : LocationRequest? = null

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var locationCallback : LocationCallback? = null

    private var mServiceHandler: Handler? = null

    private var mLocation : Location? = null

    private val notification : Notification

    get() {
        val intent = Intent(this, BGLocationService::class.java)
        val text = Common.getLocationText(mLocation)
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true)
        val servicePendingIntent = PendingIntent.getService(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val activityPendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this)
            .addAction(R.drawable.ic_baseline_location_on_24, "Launch", activityPendingIntent)
            .addAction(R.drawable.ic_baseline_cancel_24, "Cancel", servicePendingIntent)
            .setContentText(text.toString())
            .setContentTitle(Common.getLocationTitle(this))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setVibrate(null)
            .setTicker(text.toString())
            .setWhen(System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId(CHANNEL_ID)
        }

        return builder.build()

    }



    fun requestLocationUpdates(){
        Common.setRequestLocationUpdates(this,true)
        startService(Intent(applicationContext,BGLocationService::class.java))

        try {
            fusedLocationProviderClient!!.requestLocationUpdates(locationRequest!!,locationCallback!!,
                Looper.myLooper())


        }catch (ex:SecurityException){
            Common.setRequestLocationUpdates(this,false)

        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object:LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
                onNewLocation(p0!!.lastLocation)
            }
        }

        createLocationRequest()
        getLastLocation()

        val handlerThread = HandlerThread("RECLOC")
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = packageName
            val mChannel = NotificationChannel(CHANNEL_ID,name,NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        //stopForeground(true)
        startForeground(NOTIFCATION_ID,notification)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (!mChangingConfiguration && Common.requestingLocationUpdates(this))
            startForeground(NOTIFCATION_ID,notification)

        return true
    }

    override fun onDestroy() {
        mServiceHandler!!.removeCallbacksAndMessages(null)
        super.onDestroy()
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val startFromNotification = intent!!.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,false)
        if (startFromNotification){
            removeLocationUpdates()
            stopSelf()
        }

        return Service.START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

     fun removeLocationUpdates() {
        try {
            fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
            Common.setRequestLocationUpdates(this,false)
            stopSelf()
        }catch (ex: SecurityException){
            Common.setRequestLocationUpdates(this,true)
            Log.e("AHAHA", ""+ex.message.toString())
        }
    }

    private fun getLastLocation() {
        try {
            fusedLocationProviderClient!!.lastLocation
                .addOnCompleteListener{task ->
                        if (task.isSuccessful && task.result != null)
                            mLocation = task.result
                        else
                            Log.e("AHAHA", "FAILED TO GET LOCATION")

                }
        }catch (ex:SecurityException){
            Log.e("AHAHA", ""+ex.message.toString())
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = UPDATE_INTERVAL_IN_MIL
        locationRequest!!.fastestInterval= FASTESD_UPDATE_INTERVAL_IN_MIL
        locationRequest!!.smallestDisplacement = 1f
    }

    private fun onNewLocation(lastLocation: Location?) {
        mLocation = lastLocation!!
        EventBus.getDefault().postSticky(BackgroundLocation(mLocation!!))
        if (serviceIsRunningInForeground(this)){
            mNotificationManager!!.notify(NOTIFCATION_ID,notification)
        }
    }

    private fun serviceIsRunningInForeground(context:Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)){
            if (javaClass.name.equals(service.service.className))
                if (service.foreground)
                    return true
        }
        return false
    }




}
