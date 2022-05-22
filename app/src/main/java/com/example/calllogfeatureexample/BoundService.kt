package com.example.calllogfeatureexample

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder


class BoundService : Service() {

    private val myBinder = MyLocalBinder()

    override fun onBind(intent: Intent): IBinder? {
        return myBinder
    }

    fun getCallData(): String {
        return "Test"
    }

    inner class MyLocalBinder : Binder() {
        fun getService() : BoundService {
            return this@BoundService
        }

    }
}