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
    private val ACTOR_QUERY_LITERAL = "ACTOR_QUERY"

    private val FOREACH_X = "\$FOREACH_XXX\$"
    private val FOREACH_GAME_COMMAND = FOREACH_X.replace("XXX", GAME_COMMAND_LITERAL)
    private val FOREACH_EDITOR_COMMAND = FOREACH_X.replace("XXX", EDITOR_COMMAND_LITERAL)
    private val FOREACH_ACTOR_QUERY = FOREACH_X.replace("XXX", ACTOR_QUERY_LITERAL)
    private val FOREACH_END = "\$END_FOREACH\$"
    private val FOREACH_VAR_COMMAND_NAME = "\$COMMAND_NAME\$"
    private val FOREACH_VAR_QUERY_NAME = "\$QUERY_NAME\$"

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
            if (filePath.toFileName().contains(FOREACH_ACTOR_QUERY)) {
                replaceActorQueryLoopFileNamePlaceholder(filePath, configuration)
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

    private fun replaceActorQueryLoopFileNamePlaceholder(
        filePath: String, configuration: SetupConfiguration
    ) = internalReplaceCommandLoopFileNamePlaceholder(filePath, configuration.actorQueries, ACTOR_QUERY_LITERAL)

    private fun internalReplaceCommandLoopFileNamePlaceholder(
        filePath: String,
        commands: MutableList<String>,
        literalPlaceholder: String
    ) {
        val foreachPatternString = FOREACH_X.replace("XXX", literalPlaceholder)

        val original = filePath.toFileName()
        val content = fileSystem.readFile(filePath)
        val parentFilePath = filePath.toParentFilePath()

        val begin = original.indexOf(foreachPatternString)
        val end = original.indexOf(FOREACH_END)
        assert(end > begin)

        val prefix = original.substring(0, begin)
        val loopPart = original.substring(begin + foreachPatternString.length, end)
        val suffix = original.substring(end + FOREACH_END.length)
        commands.forEach { variableName ->
            val variableNamePlaceholder = literalPlaceholder.toVariableNamePlaceholder()
            val replacedLoopPart = loopPart.replacePlaceholdersWithVariableName(variableNamePlaceholder, variableName)
            val newFileName = prefix + replacedLoopPart + suffix
            val replacedContent = content.replacePlaceholdersWithVariableName(variableNamePlaceholder, variableName)
            fileSystem.writeFile(parentFilePath.toSubFilePath(newFileName), replacedContent)
        }
        fileSystem.deleteFile(filePath)
    }

    /**
     * @param variableNamePlaceHolder is set to either COMMAND_NAME or QUERY_NAME
     */
    private fun String.replacePlaceholdersWithVariableName(variableNamePlaceHolder:String, variableName: String) = this
        .replacePlaceholders()
        .replace(variableNamePlaceHolder, variableName)

    /**
     * @param this has to contain exactly one "_", used for literals "GAME_COMMAND", "EDITOR_COMMAND", "ACTOR_QUERY"
     */
    private fun String.toVariableNamePlaceholder() = "$" + substringAfter("_") + "_NAME$"

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
