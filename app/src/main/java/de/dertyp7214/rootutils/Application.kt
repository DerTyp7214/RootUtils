package de.dertyp7214.rootutils

import android.app.Application

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        init(this)
    }
}