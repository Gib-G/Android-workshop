package com.gib.filrouge

import android.app.Application

class App: Application() {

    // Assigning the app's context to Api::appContext.
    override fun onCreate() {
        super.onCreate()
        Api.setUpContext(this)
    }

}