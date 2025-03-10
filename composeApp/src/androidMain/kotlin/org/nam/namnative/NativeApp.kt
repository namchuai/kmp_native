package org.nam.namnative

import KoinInitializer
import android.app.Application

class NativeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}