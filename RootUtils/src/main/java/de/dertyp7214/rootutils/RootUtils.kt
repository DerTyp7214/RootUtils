package de.dertyp7214.rootutils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.jaredrummler.android.shell.CommandResult
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
    clazz.memberProperties.forEach { it checkRoot clazz }
    clazz.functions.forEach { it checkRoot clazz }
    clazz checkRoot clazz

    clazz.nestedClasses.forEach { inject(it) }
}

private infix fun KAnnotatedElement.checkRoot(clazz: KClass<*>) {
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

fun runCommand(command: String): CommandResult {
    return com.jaredrummler.android.shell.Shell.run(command)
}

fun String.asCommand(): CommandResult {
    return runCommand(this)
}