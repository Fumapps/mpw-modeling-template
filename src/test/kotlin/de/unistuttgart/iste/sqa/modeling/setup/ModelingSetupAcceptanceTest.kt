package de.unistuttgart.iste.sqa.modeling.setup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.Files
import java.util.concurrent.TimeUnit

internal class ModelingSetupAcceptanceTest {
    @Test
    fun `GIVEN a simple configuration WHEN execute setup AND call maven package afterwards THEN build succeeds`() {
        val configuration = SetupConfiguration(
            createTempFile(),
            "hamster",
            "PauleHamster",
            "Territory",
            gameCommands("move", "turnLeft"),
            editorCommands("addWallToTile", "addGrainToTile"),
            queries("frontIsClear", "grainAvailable")
        )

        cleanup(configuration)

        val sut = ModelingTemplateProcessor()
        sut.createProjectStructureFromTemplate(configuration)

        assertThatNoPlaceholderIsExistingAnyMore(configuration.targetPath)
        callMvnPackageToGenerateMpw(configuration)
        cleanup(configuration)
    }

    private fun callMvnPackageToGenerateMpw(configuration: SetupConfiguration) {
        val process = ProcessBuilder(
            "mvn", "package", "--file", "pom.xml",
            "-Dmaven.repo.local=${configuration.targetPath}/maven-local-repository"
        )
            .directory(File(configuration.targetPath))
            .inheritIO()
            .start()

        process.waitFor(10, TimeUnit.MINUTES)
        assertEquals(0, process.exitValue(), process.inputStream.bufferedReader().readText())
    }

    private fun assertThatNoPlaceholderIsExistingAnyMore(targetPath: String) {
        File(targetPath).walkTopDown().filter { it.name.contains("$") }.forEach {
            throw IllegalStateException("file name still contains placeholders: $it")
        }
        File(targetPath).walkTopDown().filter { it.isFile && it.readText().containsAnyPlaceholder() }.forEach {
            throw IllegalStateException("file content still contains placeholders: $it")
        }
    }
    private fun String.containsAnyPlaceholder(): Boolean {
        return "\\$(MPW|ACTOR|STAGE|IMAGE|GAME|EDITOR|COMMAND|FOREACH|END|QUERY)".toRegex().find(this) != null
    }

    private fun gameCommands(vararg names: String) = names.toMutableList()
    private fun editorCommands(vararg names: String) = names.toMutableList()
    private fun queries(vararg names: String) = names.toMutableList()

    private fun createTempFile(): String {
        val prefix = "ModelingSetupAcceptanceTest-${System.currentTimeMillis()}"
        val directory = Files.createTempDirectory(prefix)
        return directory.toAbsolutePath().toString()
    }

    private fun cleanup(configuration: SetupConfiguration) {
        File(configuration.targetPath).deleteRecursively()
    }
}
