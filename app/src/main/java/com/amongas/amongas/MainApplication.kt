package com.amongas.amongas
import com.jakewharton.threetenabp.AndroidThreeTen
import android.app.Application
import android.net.Network

class MainApplication : Application() {
    var originalNetwork: Network? = null
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
