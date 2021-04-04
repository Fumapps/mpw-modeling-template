package de.unistuttgart.iste.sqa.modeling.setup

import de.unistuttgart.iste.sqa.modeling.setup.filesystem.IFileSystem
import java.io.File

class SetupGenerator(
    private val fileSystem: IFileSystem
) {
    private val MPW_NAME = "\$MPW_NAME\$"
    private val ACTOR_NAME = "\$ACTOR_NAME\$"
    private val STAGE_NAME = "\$STAGE_NAME\$"
    private val MPW_NAME_FIRST_UPPER = "\$MPW_NAME_FIRST_UPPER\$"
    private val ACTOR_NAME_FIRST_UPPER = "\$ACTOR_NAME_FIRST_UPPER\$"
    private val STAGE_NAME_FIRST_UPPER = "\$STAGE_NAME_FIRST_UPPER\$"

    private val IMAGES_FILE = "\$IMAGES\$"

    private lateinit var configuration: SetupConfiguration

    fun generate(configuration: SetupConfiguration) {
        this.configuration = configuration

        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            val content = fileSystem.readFile(filePath)
            val replacedContent = content.replacePlaceholders()
            fileSystem.writeFile(filePath, replacedContent)
        }
        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            if (filePath.toFileName() == IMAGES_FILE) {
                fileSystem.deleteFile(filePath)
                val parentFilePath = filePath.toParentFilePath()
                for (image in configuration.images) {
                    fileSystem.writeFileForBase64Data(parentFilePath.toSubFilePath("${image.name}.png"), image.dataEncodedInBase64)
                }
            }
        }
        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            val content = fileSystem.readFile(filePath)
            val fileName = filePath.toFileName()
            val replacedFileName = fileName.replacePlaceholders()
            val containedAnyPlaceholder = fileName != replacedFileName
            if (containedAnyPlaceholder) {
                fileSystem.deleteFile(filePath)
                fileSystem.writeFile(filePath.toParentFilePath().toSubFilePath(replacedFileName), content)
            }
        }
    }

    private fun String.replacePlaceholders() = this
        .replace(MPW_NAME, configuration.mpwName.toLowerCase())
        .replace(ACTOR_NAME, configuration.actorName.toLowerCase())
        .replace(STAGE_NAME, configuration.stageName.toLowerCase())
        .replace(MPW_NAME_FIRST_UPPER, configuration.mpwName.toFirstUpper())
        .replace(ACTOR_NAME_FIRST_UPPER, configuration.actorName.toFirstUpper())
        .replace(STAGE_NAME_FIRST_UPPER, configuration.stageName.toFirstUpper())

    private fun String.toFirstUpper() =
        if (isNotEmpty()) this[0].toUpperCase() + substring(1) else this

    private fun String.toFileName() = File(this).name
    private fun String.toParentFilePath() = File(this).parent
    private fun String.toSubFilePath(subPath: String) = "$this/$subPath"
}