package com.example.mbajraktargeo
import android.app.Service
import android.os.IBinder
import android.content.Intent
import android.os.Handler
import androidx.localbroadcastmanager.content.LocalBroadcastManager



class MyService:Service() {
    private val iBinder:IBinder?=null
    override fun onBind(p0: Intent?): IBinder? {
        return iBinder
    }

    override fun onCreate(){
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendMessage()
        RepeatHelper.repeatDelayed(delay){
            sendMessage()
        }
        return START_STICKY

    }
    //timer
    val delay = 10000L
    object RepeatHelper {
        fun repeatDelayed(delay: Long, todo: () -> Unit) {
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    todo()

                    handler.postDelayed(this, delay)

                }
            }, delay)
        }
    }
        private fun sendMessage() {
        val intent = Intent("my-event")
        // add data
        intent.putExtra("message", "data")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}