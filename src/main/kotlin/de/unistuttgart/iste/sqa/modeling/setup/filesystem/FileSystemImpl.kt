package de.unistuttgart.iste.sqa.modeling.setup.filesystem

import java.io.File
import java.nio.charset.Charset

class FileSystemImpl(override var charset: Charset = Charsets.UTF_8) : IFileSystem {
    override fun listFiles(path: String): List<String> {
        return File(path).list()?.toList() ?: emptyList()
    }

    override fun readFile(path: String): String {
        return File(path).readText(charset)
    }

    override fun writeFile(path: String, content: String) {
        File(path).writeText(content, charset)
    }
}