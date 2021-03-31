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

    override fun readFile(path: String): String {
        return fileMap[path]?.toString(charset)
            ?: throw IllegalStateException("invalid file $path")
    }

    override fun writeFile(path: String, content: String) {
        fileMap[path] = content.toByteArray(charset)
    }

    override fun deleteFile(path: String) {
        fileMap.remove(path)
    }
}
