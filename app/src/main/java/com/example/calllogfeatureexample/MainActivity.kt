package com.example.calllogfeatureexample

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*


const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun checkAndRequestPermissions(): Boolean {
        val readPhoneState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        val read_call_log =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
        val listPermissionsNeeded: MutableList<String> = mutableListOf()
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (read_call_log != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG)
        }
        if (read_call_log != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
        }
        if (read_call_log != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                (listPermissionsNeeded.toTypedArray() as Array<String?>),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(broadcastReceiver)
    }


    private val broadcastReceiver: CallReceiver = object : CallReceiver() {

        override fun onIncomingCallAnswered(ctx: Context?, number: String?, start: Date?) {
            super.onIncomingCallAnswered(ctx, number, start)
            Log.e(TAG, "incomingcallans: ${number}")
        }

        override fun onIncomingCallReceived(ctx: Context?, number: String?, start: Date?) {
            super.onIncomingCallReceived(ctx, number, start)
            Log.e(TAG, "incomingreceived: $number")
        }

    }

    override fun onResume() {
        super.onResume()
        if (checkAndRequestPermissions()){
            this.registerReceiver(broadcastReceiver,  IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
        }
    }

}