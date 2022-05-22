package com.example.calllogfeatureexample

import android.annotation.SuppressLint
import android.content.Context
import com.example.calllogfeatureexample.Constants.FIREBASE_INSTANCE_URL
import com.example.calllogfeatureexample.Constants.INCOMING_CALL_ANSWERED
import com.example.calllogfeatureexample.Constants.INCOMING_CALL_ENDED
import com.example.calllogfeatureexample.Constants.INCOMING_CALL_RECEIVED
import com.example.calllogfeatureexample.Constants.MISSED_CALL
import com.example.calllogfeatureexample.Constants.OUTGOING_CALL_ENDED
import com.example.calllogfeatureexample.Constants.OUTGOING_CALL_STARTED
import com.example.calllogfeatureexample.Constants.USER_CALL_DATA
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

const val TAG = "calllogstate"

open class CallReceiver : PhonecallReceiver() {

    private var mDatabaseReference =
        FirebaseDatabase.getInstance(FIREBASE_INSTANCE_URL).getReference(USER_CALL_DATA)


    override fun onIncomingCallReceived(ctx: Context?, number: String?, start: Date?) {
        val callUserData = CallUserData(
            convertLongToTime(start?.time),
            number.toString(),
            INCOMING_CALL_RECEIVED
        )
        val key = mDatabaseReference.push().key ?: ""
        val callData = createCallData(key, callUserData)
        mDatabaseReference.child(key).setValue(callData)
    }

    override fun onIncomingCallAnswered(ctx: Context?, number: String?, start: Date?) {
        val callUserData = CallUserData(
            convertLongToTime(start?.time),
            number.toString(),
            INCOMING_CALL_ANSWERED
        )
        val key = mDatabaseReference.push().key ?: ""
        val callData = createCallData(key, callUserData)
        mDatabaseReference.child(key).setValue(callData)

    }

    override fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
        val callUserData = CallUserData(
            convertLongToTime(start?.time),
            number.toString(),
            INCOMING_CALL_ENDED
        )
        val key = mDatabaseReference.push().key ?: ""
        val callData = createCallData(key, callUserData)
        mDatabaseReference.child(key).setValue(callData)
    }

    override fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?) {
        val callUserData = CallUserData(
            convertLongToTime(start?.time),
            number.toString(),
            OUTGOING_CALL_STARTED
        )
        val key = mDatabaseReference.push().key ?: ""
        val callData = createCallData(key, callUserData)
        mDatabaseReference.child(key).setValue(callData)
    }

    override fun onOutgoingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {

        val callUserData = CallUserData(
            convertLongToTime(start?.time),
            number.toString(),
            OUTGOING_CALL_ENDED
        )
        val key = mDatabaseReference.push().key ?: ""
        val callData = createCallData(key, callUserData)
        mDatabaseReference.child(key).setValue(callData)
    }

    override fun onMissedCall(ctx: Context?, number: String?, start: Date?) {

        //val newRef = mDatabaseReference.child("UserCallData")
        //newRef.setValue(callUserData)

        val callUserData = CallUserData(
            convertLongToTime(start?.time),
            number.toString(),
            MISSED_CALL
        )
        val key = mDatabaseReference.push().key ?: ""
        val callData = createCallData(key, callUserData)
        mDatabaseReference.child(key).setValue(callData)
    }


    private fun createCallData(key: String, content: CallUserData): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap[key] = content.toString()
        return hashMap
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long?): String {
        val date = Date(time!!)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm a")
        return format.format(date)
    }
}