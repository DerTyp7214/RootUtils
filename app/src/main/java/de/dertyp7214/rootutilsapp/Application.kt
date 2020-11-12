package de.dertyp7214.rootutilsapp

import android.app.Application
import de.dertyp7214.rootutils.init

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        init(this)
    }
}