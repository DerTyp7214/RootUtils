package de.dertyp7214.rootutils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileOutputStream
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

fun init(application: Application) {
    application.registerActivityLifecycleCallbacks(object :
        Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            inject(activity::class)
        }
    })
}

private fun inject(clazz: KClass<*>) {
    clazz.memberProperties.forEach { it.checkRoot() }
    clazz.functions.forEach { it.checkRoot() }
    clazz.checkRoot()

    clazz.nestedClasses.forEach { inject(it) }
}

private fun KAnnotatedElement.checkRoot() {
    findAnnotation<RequireRoot>()?.apply {
        if (!Shell.rootAccess()) {
            Log.d("ROOT", "Root is not accessible for ${this@checkRoot}")
            if (throwError) throw RootException(message)
        }
    }
}

private class RootException(message: String) : Exception(message)

fun writeSuFile(file: SuFile, content: String) {
    SuFileOutputStream(file).use {
        it.write(content.toByteArray(Charsets.UTF_8))
    }
}

fun runCommand(command: String, callback: (result: Array<String>) -> Unit = {}): Boolean {
    return Shell.su(command).exec().apply {
        if (err.size > 0) err.toTypedArray().apply { callback(this) }.contentToString()
        if (out.size > 0) out.toTypedArray().apply { callback(this) }.contentToString()
    }.isSuccess.apply {
        Log.d("RUN COMMAND", "$command -> $this")
    }
}

fun String.asCommand(callback: (result: Array<String>) -> Unit = {}): Boolean {
    return runCommand(this, callback)
}

fun rootAccess(): Boolean {
    return Shell.rootAccess()
}

fun getShell(): Shell {
    return Shell.getShell()
}

fun su(command: String): Shell.Job {
    return Shell.su(command)
}