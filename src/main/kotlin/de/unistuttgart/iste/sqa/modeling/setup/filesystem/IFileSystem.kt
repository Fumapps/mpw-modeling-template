package de.unistuttgart.iste.sqa.modeling.setup.filesystem

import java.nio.charset.Charset

interface IFileSystem {
    var charset: Charset

    fun walkFiles(path: String, function: (String) -> Unit)
    fun readFile(path: String): String
    fun writeFile(path: String, content: String)
    fun deleteFile(path: String)
}