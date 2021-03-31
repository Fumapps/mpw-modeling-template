package de.unistuttgart.iste.sqa.modeling.setup

import de.unistuttgart.iste.sqa.modeling.setup.filesystem.IFileSystem
import java.io.File

class SetupGenerator(
    private val fileSystem: IFileSystem
) {
    private val MPW_NAME = "\$MPW_NAME\$"

    private lateinit var configuration: SetupConfiguration

    fun generate(configuration: SetupConfiguration) {
        this.configuration = configuration

        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            val content = fileSystem.readFile(filePath)
            val replacedContent = content.replacePlaceholders()
            fileSystem.writeFile(filePath, replacedContent)
        }
        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            val content = fileSystem.readFile(filePath)
            if (File(filePath).name.contains(MPW_NAME)) {
                fileSystem.deleteFile(filePath)
            }
            val replacedFilePath = filePath.replacePlaceholders()
            fileSystem.writeFile(replacedFilePath, content)
        }
    }

    private fun String.replacePlaceholders() = this
        .replace(MPW_NAME, configuration.mpwName.toLowerCase())
}