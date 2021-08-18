package de.unistuttgart.iste.sqa.modeling.setup.filesystem

import java.io.File
import java.nio.charset.Charset
import java.util.*

class FileSystemImpl(override var charset: Charset = Charsets.UTF_8) : IFileSystem {
    override fun walkFiles(path: String, function: (String) -> Unit) {
        File(path).walk().forEach {
            if (it.isFile) {
                function(it.absolutePath)
            }
        }
    }

    override fun walkDirectories(path: String, function: (String) -> Unit) {
        File(path).walk().forEach {
            if (it.isDirectory) {
                function(it.absolutePath)
            }
        }
    }

    override fun readFile(path: String): String {
        return File(path).readText(charset)
    }

    override fun writeFile(path: String, content: String) {
        File(path).writeText(content, charset)
    }

    override fun writeFileForBase64Data(path: String, contentInBase64: String) {
        val data = Base64.getDecoder().decode(contentInBase64)
        File(path).writeBytes(data)
    }

    override fun deleteFile(path: String) {
        File(path).delete()
    }

    override fun renameDirectory(parentPath: String, directoryName: String, newDirectoryName: String) {
        val oldDirectoryFile = File(parentPath, directoryName)
        val newDirectoryFile = File(parentPath, newDirectoryName)
        oldDirectoryFile.renameTo(newDirectoryFile)
    }

    override fun renameFile(parentPath: String, fileName: String, newFileName: String) {
        val oldFile = File(parentPath, fileName)
        val newFile = File(parentPath, newFileName)
        oldFile.renameTo(newFile)
    }
}