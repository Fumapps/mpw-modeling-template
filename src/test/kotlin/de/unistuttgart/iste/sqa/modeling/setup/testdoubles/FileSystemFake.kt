package de.unistuttgart.iste.sqa.modeling.setup.testdoubles

import de.unistuttgart.iste.sqa.modeling.setup.filesystem.IFileSystem
import java.lang.IllegalStateException
import java.nio.charset.Charset

class FileSystemFake : IFileSystem {
    override var charset: Charset = Charsets.UTF_8
    val fileMap = mutableMapOf<String, ByteArray>()

    override fun walkFiles(path: String, function: (String) -> Unit) {
        fileMap
            .filter { it.key.startsWith(path) }
            .forEach { function(it.key) }
    }

    override fun walkDirectories(path: String, function: (String) -> Unit) {
        fileMap
            .filter { it.key.startsWith(path) }
            .map { it.key.removeAfterLast("/") }
            .map { it.splitFolderParts() }
            .flatten()
            .toSet()
            .forEach { function(it) }
    }

    /** Removes the last part of a string identified by @param pattern */
    private fun String.removeAfterLast(pattern: String): String {
        val i = this.lastIndexOf(pattern)
        return this.removeRange(i, this.length)
    }

    /** Splits a path by "/" into its folders. E.g. a/b/c will result in {a, a/b, a/b/c} */
    private fun String.splitFolderParts(): MutableList<String> {
        val parts = this.split("/")
        var path = ""
        val result = mutableListOf<String>()
        parts.forEach {
            path += it
            path += "/"
            result += path;
        }
        return result
    }

    override fun readFile(path: String): String {
        return fileMap[path]?.toString(charset)
            ?: throw IllegalStateException("invalid file $path")
    }

    override fun writeFile(path: String, content: String) {
        fileMap[path] = content.toByteArray(charset)
    }

    /* fake implementation simply adds the prefix "ENCODED"  */
    override fun writeFileForBase64Data(path: String, contentInBase64: String) {
        fileMap[path] = "ENCODED: $contentInBase64".toByteArray(charset)
    }

    override fun deleteFile(path: String) {
        fileMap.remove(path)
    }

    override fun renameDirectory(parentPath: String, directoryName: String, newDirectoryName: String) {
        val newMap = fileMap.mapKeys { entry ->
            var key = entry.key
            val prefix = "$parentPath/$directoryName"
            if (entry.key.startsWith(prefix)) {
                val suffix = entry.key.substring(prefix.length)
                key = "$parentPath/$newDirectoryName/$suffix".replace("//", "/")
            }
            key
        }
        fileMap.clear()
        fileMap.putAll(newMap)
    }

    override fun renameFile(parentPath: String, fileName: String, newFileName: String) {
        val value = fileMap.remove("$parentPath/$fileName")
        if (value != null) {
            fileMap["$parentPath/$newFileName"] = value
        }
    }
}
