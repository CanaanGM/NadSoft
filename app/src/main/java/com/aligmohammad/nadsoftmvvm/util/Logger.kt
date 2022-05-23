package com.aligmohammad.nadsoftmvvm.util

import android.util.Log
import com.aligmohammad.nadsoftmvvm.util.Constants.DEBUG
import com.aligmohammad.nadsoftmvvm.util.Constants.TAG
import com.google.firebase.crashlytics.FirebaseCrashlytics

var isUnitTest = false

fun printLogD(className: String?, message: String ) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    }
    else if(DEBUG && isUnitTest){
        println("$className: $message")
    }
}

/*
    Priorities: Log.DEBUG, Log. etc....
 */
fun cLog(msg: String?){
    msg?.let {
        if(!DEBUG){
            FirebaseCrashlytics.getInstance().log(it)
        }
    }

}
