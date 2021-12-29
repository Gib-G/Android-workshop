package com.gib.filrouge

import android.app.Application
import com.gib.filrouge.network.Api

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Api.setUpContext(this)
    }

}