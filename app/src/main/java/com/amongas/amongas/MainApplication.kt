package com.amongas.amongas

import android.app.Application
import android.net.Network

class MainApplication : Application() {
    var originalNetwork: Network? = null
}
