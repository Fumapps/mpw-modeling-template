package de.unistuttgart.iste.sqa.modeling.setup.filesystem

import java.nio.charset.Charset

interface IFileSystem {
    var charset: Charset

    fun listFiles(path: String): List<String>
    fun readFile(path: String): String
    fun writeFile(path: String, content: String)
}