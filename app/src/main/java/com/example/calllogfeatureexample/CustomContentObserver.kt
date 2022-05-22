package com.example.calllogfeatureexample

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper

class CustomContentObserver : ContentObserver(Handler(Looper.getMainLooper())) {


}