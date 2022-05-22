package com.example.calllogfeatureexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import java.util.*


abstract class PhonecallReceiver : BroadcastReceiver() {
    var telephony : TelephonyManager ?= null

    override fun onReceive(context: Context?, intent: Intent) {

//        val phoneListener = MyPhoneStateListener()
//        telephony = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        telephony?.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)

        val telephony = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephony.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                super.onCallStateChanged(state, incomingNumber)
                //Log.e(TAG, "incomingNumber : $incomingNumber")
                onCallStateChanged(context, state, incomingNumber)
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)

    }

    //Derived classes should override these to respond to specific events of interest
    protected abstract fun onIncomingCallReceived(ctx: Context?, number: String?, start: Date?)
    protected abstract fun onIncomingCallAnswered(ctx: Context?, number: String?, start: Date?)
    protected abstract fun onIncomingCallEnded(
        ctx: Context?,
        number: String?,
        start: Date?,
        end: Date?
    )

    protected abstract fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?)
    protected abstract fun onOutgoingCallEnded(
        ctx: Context?,
        number: String?,
        start: Date?,
        end: Date?
    )

    protected abstract fun onMissedCall(ctx: Context?, number: String?, start: Date?)

    //Deals with actual events
    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    fun onCallStateChanged(context: Context?, state: Int, number: String?) {
        if (lastState == state) {
            //No change, debounce extras
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                savedNumber = number
                onIncomingCallReceived(context, number, callStartTime)
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->                 //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = Date()
                    onOutgoingCallStarted(context, savedNumber, callStartTime)
                } else {
                    isIncoming = true
                    callStartTime = Date()
                    onIncomingCallAnswered(context, savedNumber, callStartTime)
                }
            TelephonyManager.CALL_STATE_IDLE ->                 //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime)
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                }
        }
        lastState = state
    }

    companion object {
        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var callStartTime: Date? = null
        private var isIncoming = false
        //because the passed incoming is only valid in ringing
        private var savedNumber: String? = null
    }
}