package de.unistuttgart.iste.sqa.modeling.setup

import de.unistuttgart.iste.sqa.modeling.setup.filesystem.FileSystemImpl
import de.unistuttgart.iste.sqa.modeling.setup.filesystem.IFileSystem
import de.unistuttgart.iste.sqa.modeling.setup.generation.SetupGenerator
import java.lang.IllegalStateException
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.plaf.FileChooserUI

/**
 * This is the facade class to create a template project by given
 * configuration parameters.
 */
class ModelingTemplateProcessor {
    private val templateDirectoryPath = Path.of("modeling-environment-template")
    private val fileSystem : IFileSystem = FileSystemImpl()

    fun createProjectStructureFromTemplate(configuration: SetupConfiguration) {
        if (templateDirectoryNotExists()) {
            throw IllegalStateException("the template project directory does not exists - is seems that the binary program is not correctly configured")
        }
        val targetPath = Path.of(configuration.targetPath)
        if (targetPath.alreadyExists()) {
            throw IllegalStateException("the target path '${configuration.targetPath}' must not exist")
        }
        templateDirectoryPath.toFile().copyRecursively(targetPath.toFile())

        if (targetPath.pomFileNotExists()) {
            throw IllegalStateException("there is no POM file in the copied template directory '${configuration.targetPath}/pom.xml'")
        }

        val generator = SetupGenerator(fileSystem)
        generator.generate(configuration)
    }

    private fun templateDirectoryNotExists() = !Files.exists(templateDirectoryPath)
    private fun Path.alreadyExists() = toFile().exists()
    private fun Path.pomFileNotExists() = !toFile().resolve("pom.xml").exists()
}