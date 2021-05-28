package de.dertyp7214.rootutils.core

import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.dertyp7214.rootutils.Magisk

fun SuFile.parseModuleMeta(): Magisk.Module.Meta {
    val text = SuFileInputStream.open(this).readBytes().toString(Charsets.UTF_8)
    val map = HashMap<String, Any>()
    text.split("\n").forEach {
        val line = it.split("=")
        if (line.size > 1) map[line[0]] = line[1]
    }
    return Magisk.Module.Meta(
        (map["id"] ?: "") as String,
        (map["name"] ?: "") as String,
        (map["version"] ?: "") as String,
        (map["versionCode"] ?: "") as String,
        (map["author"] ?: "") as String,
        (map["description"] ?: "") as String,
        map
    )
}