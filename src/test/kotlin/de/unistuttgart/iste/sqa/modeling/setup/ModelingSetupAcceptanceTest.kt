package de.unistuttgart.iste.sqa.modeling.setup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.util.concurrent.TimeUnit

internal class ModelingSetupAcceptanceTest {
    @Test
    fun `GIVEN a simple configuration WHEN execute setup AND call maven package afterwards THEN build succeeds`() {
        val configuration = SetupConfiguration(
            createTempFile(),
            "hamstersimulator",
            "Hamster",
            "Territory",
            gameCommands("move", "turnLeft"),
            editorCommands("addWallToTile", "addGrainToTile"),
            queries("frontIsClear", "grainAvailable")
        )

        cleanup(configuration)

        val sut = ModelingTemplateProcessor()
        sut.createProjectStructureFromTemplate(configuration)

        val process = ProcessBuilder("mvn", "package", "--file", configuration.targetPath,
            "-Dmaven.repo.local=${configuration.targetPath}/maven-local-repository"
        )
            .directory(File(configuration.targetPath))
            .inheritIO()
            .start()

        process.waitFor(10, TimeUnit.MINUTES)
        assertEquals(0, process.exitValue(), process.inputStream.bufferedReader().readText())

        cleanup(configuration)
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
