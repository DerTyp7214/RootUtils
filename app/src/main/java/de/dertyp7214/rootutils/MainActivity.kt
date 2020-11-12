package de.dertyp7214.rootutils

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

@RequireRoot(false)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @RequireRoot(false)
    val c = true

    @RequireRoot(false)
    fun a() {
        Log.d("YEET", "RUN")
    }

    @RequireRoot(false)
    fun String.d() {}
}