package de.dertyp7214.rootutils

import com.jaredrummler.android.shell.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.dertyp7214.rootutils.core.parseModuleMeta
import java.io.File
import kotlin.text.Charsets.UTF_8

class Magisk private constructor() {
    companion object {
        const val MODULES_PATH = "/data/adb/modules"
        fun getMagisk(): Magisk? {
            return if (Shell.run("magisk").getStderr().startsWith("magisk", true))
                Magisk()
            else null
        }
    }

    val versionString: String
        get() {
            return Shell.run("magisk -v").getStdout()
        }

    val versionNumber: String
        get() {
            return Shell.run("magisk -V").getStdout()
        }

    val versionFullString: String
        get() {
            return Shell.run("magisk -c").getStdout()
        }

    val modules: List<Module>
        get() {
            return SuFile(MODULES_PATH).listFiles()?.filter { SuFile(it, "module.prop").exists() }
                ?.map {
                    val meta = SuFile(it, "module.prop").parseModuleMeta()
                    Module(meta.id, it, meta)
                } ?: ArrayList()
        }

    fun isModuleInstalled(id: String): Boolean {
        return modules.any { it.id == id }
    }

    fun installOrUpdateModule(meta: Module.Meta, files: Map<String, String?>) {
        val modulesDir = SuFile(MODULES_PATH, meta.id)
        modulesDir.mkdirs()
        writeSuFile(SuFile(modulesDir, "module.prop"), meta.toString())
        files.forEach { file ->
            SuFile(modulesDir, file.key).apply {
                if (exists()) {
                    var text = SuFileInputStream(this).readBytes().toString(UTF_8)

                    if (text.contains(file.value?.split("=")?.get(0).toString())) {
                        text = text.replace(
                            text.lines()
                                .first { it.contains(file.value?.split("=")?.get(0).toString()) },
                            file.value ?: ""
                        )
                    } else text += "\n${file.value ?: ""}"

                    if (text.lines().isEmpty()) text = file.value ?: ""

                    writeSuFile(SuFile(modulesDir, file.key), text)
                } else {
                    if (file.value != null) writeSuFile(this, file.value ?: "")
                    else mkdirs()
                }
            }
        }
    }

    data class Module(val id: String, val path: File, val meta: Meta) {
        data class Meta(
            val id: String,
            val name: String,
            val version: String,
            val versionCode: String,
            val author: String,
            val description: String,
            val raw: Map<String, Any> = mapOf()
        ) {
            override fun toString(): String {
                return "id=$id\nname=$name\nversion=$version\nversionCode=$versionCode\nauthor=$author\ndescription=$description"
            }
        }
    }
}