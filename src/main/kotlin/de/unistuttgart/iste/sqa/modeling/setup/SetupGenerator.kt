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

    private val GAME_COMMAND_LITERAL = "GAME_COMMAND"
    private val EDITOR_COMMAND_LITERAL = "EDITOR_COMMAND"

    private val FOREACH_X_COMMAND = "\$FOREACH_XXX\$"
    private val FOREACH_GAME_COMMAND = FOREACH_X_COMMAND.replace("XXX", GAME_COMMAND_LITERAL)
    private val FOREACH_EDITOR_COMMAND = FOREACH_X_COMMAND.replace("XXX", EDITOR_COMMAND_LITERAL)
    private val FOREACH_END = "\$END_FOREACH\$"
    private val FOREACH_VAR_COMMAND_NAME = "\$COMMAND_NAME\$"

    private val IMAGES_FILE = "\$IMAGES\$"

    private lateinit var configuration: SetupConfiguration

    fun generate(configuration: SetupConfiguration) {
        this.configuration = configuration

        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            replaceGeneralPlaceholders(filePath)
        }
        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            if (filePath.toFileName() == IMAGES_FILE) {
                replaceImageFileNamePlaceholder(filePath, configuration)
            }
            if (filePath.toFileName().contains(FOREACH_GAME_COMMAND)) {
                replaceGameCommandLoopFileNamePlaceholder(filePath, configuration)
            }
            if (filePath.toFileName().contains(FOREACH_EDITOR_COMMAND)) {
                replaceEditorCommandLoopFileNamePlaceholder(filePath, configuration)
            }
        }
        fileSystem.walkFiles(configuration.targetPath) { filePath ->
            replaceGeneralFileNamePlaceholders(filePath)
        }
    }

    private fun replaceGeneralPlaceholders(filePath: String) {
        val content = fileSystem.readFile(filePath)
        val replacedContent = content.replacePlaceholders()
        fileSystem.writeFile(filePath, replacedContent)
    }

    private fun replaceImageFileNamePlaceholder(
        filePath: String,
        configuration: SetupConfiguration
    ) {
        fileSystem.deleteFile(filePath)
        val parentFilePath = filePath.toParentFilePath()
        for (image in configuration.images) {
            fileSystem.writeFileForBase64Data(
                parentFilePath.toSubFilePath("${image.name}.png"),
                image.dataEncodedInBase64
            )
        }
    }

    private fun replaceGeneralFileNamePlaceholders(filePath: String) {
        val content = fileSystem.readFile(filePath)
        val fileName = filePath.toFileName()
        val replacedFileName = fileName.replacePlaceholders()
        val containedAnyPlaceholder = fileName != replacedFileName
        if (containedAnyPlaceholder) {
            fileSystem.deleteFile(filePath)
            fileSystem.writeFile(filePath.toParentFilePath().toSubFilePath(replacedFileName), content)
        }
    }

    private fun replaceGameCommandLoopFileNamePlaceholder(
        filePath: String, configuration: SetupConfiguration
    ) = internalReplaceCommandLoopFileNamePlaceholder(filePath, configuration.gameCommands, GAME_COMMAND_LITERAL)

    private fun replaceEditorCommandLoopFileNamePlaceholder(
        filePath: String, configuration: SetupConfiguration
    ) = internalReplaceCommandLoopFileNamePlaceholder(filePath, configuration.editorCommands, EDITOR_COMMAND_LITERAL)

    private fun internalReplaceCommandLoopFileNamePlaceholder(
        filePath: String,
        commands: MutableList<String>,
        commandPlaceholder: String
    ) {
        val foreachPatternString = FOREACH_X_COMMAND.replace("XXX", commandPlaceholder)

        val original = filePath.toFileName()
        val content = fileSystem.readFile(filePath)
        val parentFilePath = filePath.toParentFilePath()

        val begin = original.indexOf(foreachPatternString)
        val end = original.indexOf(FOREACH_END)
        assert(end > begin)

        val prefix = original.substring(0, begin)
        val loopPart = original.substring(begin + foreachPatternString.length, end)
        val suffix = original.substring(end + FOREACH_END.length)
        commands.forEach { commandName ->
            val replacedLoopPart = loopPart.replacePlaceholdersWithCommandName(commandName)
            val newFileName = prefix + replacedLoopPart + suffix
            val replacedContent = content.replacePlaceholdersWithCommandName(commandName)
            fileSystem.writeFile(parentFilePath.toSubFilePath(newFileName), replacedContent)
        }
        fileSystem.deleteFile(filePath)
    }

    private fun String.replacePlaceholdersWithCommandName(commandName: String) = this
        .replacePlaceholders()
        .replace(FOREACH_VAR_COMMAND_NAME, commandName)

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