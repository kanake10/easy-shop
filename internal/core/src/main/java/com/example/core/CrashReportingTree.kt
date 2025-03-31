package com.example.core

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) return

        FirebaseCrashlytics.getInstance().apply {
            log("$tag: $message")
            t?.let { recordException(it) }
        }
    }
}
