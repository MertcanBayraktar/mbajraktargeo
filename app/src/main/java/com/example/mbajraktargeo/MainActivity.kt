package com.example.mbajraktargeo


import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.location.Location

import android.view.View
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager





class MainActivity : AppCompatActivity(), View.OnClickListener {
val textViewerConstant = "Total Distance Traveled : "
lateinit var startBtn:Button
lateinit var stopBtn:Button
lateinit var fusedLocationProviderClient:FusedLocationProviderClient
lateinit var distanceInMeters:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stopBtn=findViewById(R.id.btnStop)
        startBtn=findViewById(R.id.btnStart)
        distanceInMeters = findViewById(R.id.totalDistanceTraveled)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        findViewById<Button>(R.id.btnStart).setOnClickListener{
            fetchLocation()
        }
        stopBtn.setOnClickListener(this)
        startBtn.setOnClickListener(this)

    }

override fun onClick(v:View?){
    if(v===startBtn){
        startService(Intent(this,MyService::class.java))
    }
    else if(v===stopBtn){
        stopService(Intent(this,MyService::class.java))
        Toast.makeText(applicationContext, "Service Has Stopped", Toast.LENGTH_SHORT).show()
    }
}



    private fun fetchLocation()
    {
        val task = fusedLocationProviderClient.lastLocation
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
            return
        }
        task.addOnSuccessListener { 
            if(it !=null){
                Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
                currentLongitude = it.longitude
                currentLocactionLatitude = it.latitude
            }
        }
    }

    var currentLocactionLatitude = 0.00
    var currentLongitude = 0.00

    var previousLocationLatiude = 0.00
    var previousLocationLongitude = 0.00

    var totalDistanceWalkedInMeters = 0.0f

    private fun CalculateDistanceTraveled(){
        val locationCurrent = Location("")
        locationCurrent.latitude = currentLocactionLatitude
        locationCurrent.longitude = currentLongitude
        val locationPrevious = Location("")
        locationPrevious.latitude = previousLocationLatiude
        locationPrevious.longitude = previousLocationLongitude

        totalDistanceWalkedInMeters += locationPrevious.distanceTo(locationCurrent)
        distanceInMeters.text = textViewerConstant + totalDistanceWalkedInMeters.toString()
    }
    private fun CheckForLocationChange(){
        previousLocationLatiude = currentLocactionLatitude
        previousLocationLongitude = currentLongitude
        fetchLocation()
        CalculateDistanceTraveled()
    }
    override fun onResume() {
        super.onResume()

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("my-event")
        )
    }
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Extract data included in the Intent
            val message = intent.getStringExtra("message")
            Toast.makeText(applicationContext, "Service Has Started,Counting your steps..", Toast.LENGTH_SHORT).show()
            CheckForLocationChange()
        }
    }
    override fun onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onPause()
    }

}
