package com.example.calllogfeatureexample

import android.Manifest
import android.app.role.RoleManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*


const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
class MainActivity : AppCompatActivity() {

    var myService: BoundService? = null
    var isBound = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(broadCastReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
        if (checkAndRequestPermissions()){
            //requestRole()
            this.registerReceiver(broadcastReceiver,  IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
        }


        val intent = Intent(this, BoundService::class.java)
        //bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
        //bindService(intent, myCallingService, Context.BIND_AUTO_CREATE)


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
        if (!listPermissionsNeeded.isEmpty()) {
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

//    val broadCastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(contxt: Context?, intent: Intent?) {
////            when (intent?.action) {
////
////            }
//
//            Log.e(TAG, "onReceive: ${intent?.action}" )
//        }
//    }

    private val broadcastReceiver: CallReceiver = object : CallReceiver() {

        override fun onIncomingCallAnswered(ctx: Context?, number: String?, start: Date?) {
            super.onIncomingCallAnswered(ctx, number, start)
            Log.e(TAG, "incomingcallans: ${number}" )
        }

        override fun onIncomingCallReceived(ctx: Context?, number: String?, start: Date?) {
            super.onIncomingCallReceived(ctx, number, start)
            Log.e(TAG, "incomingreceived: $number" )
        }


//        override fun onReceive(context: Context?, intent: Intent) {
//            super.onReceive(context, intent)
//            Log.e(TAG, "onReceive: ${intent.action}" )
//        }
//
//        override fun onIncomingCallReceived(ctx: Context?, number: String?, start: Date?) {
//            Log.e(TAG, "incomingreceived: $number" )
//        }
//
//        override fun onIncomingCallAnswered(ctx: Context?, number: String?, start: Date?) {
//            Log.e(TAG, "incomingcallans: ${number}" )
//        }
//
//        override fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
//            Log.e(TAG, "incomingcallend: ${number}" )
//        }
//
//        override fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?) {
//            Log.e(TAG, "outcomingcall: ${number}" )
//        }
//
//        override fun onOutgoingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
//            Log.e(TAG, "outcomingcallend: ${number}" )
//        }
//
//        override fun onMissedCall(ctx: Context?, number: String?, start: Date?) {
//            Log.e(TAG, "missedcall: $number" )
//        }


    }

    private val REQUEST_ID = 1

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        startActivityForResult(intent, REQUEST_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                // Your app is now the call screening app
            } else {
                // Your app is not the call screening app
            }
        }
    }

    //var mCallServiceIntent: Intent = Intent(this, "android.telecom.CallScreeningService")
    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as BoundService.MyLocalBinder
            myService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        // Write a message to the database
//        val myRef = FirebaseDatabase.getInstance("https://calllogfeatureexample-1f06b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("message")
//
//        myRef.setValue("Hello, World!")
//
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val value = dataSnapshot.value
//                Log.e(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.e(TAG, "Failed to read value.", error.toException())
//            }
//        })
    }

    //https://calllogfeatureexample-default-rtdb.firebaseio.com

}