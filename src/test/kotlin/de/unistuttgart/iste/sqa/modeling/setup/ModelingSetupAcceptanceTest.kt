package de.unistuttgart.iste.sqa.modeling.setup

import de.unistuttgart.iste.sqa.modeling.setup.filesystem.FileSystemImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.TimeUnit

internal class ModelingSetupAcceptanceTest {
    @Test
    @Disabled
    fun `GIVEN a simple configuration WHEN execute setup AND call maven package afterwards THEN build succeeds`() {
        val fileSystem = FileSystemImpl()
        val configuration = SetupConfiguration(
            createTempFile(),
            "hamster",
            "Paule",
            gameCommands("move", "turnLeft"),
            editorCommands("addWallToTile", "addGrainToTile"),
            queries("frontIsClear", "grainAvailable")
        )
        val sut = SetupGenerator(fileSystem)
        sut.generate(configuration)

        val process = ProcessBuilder("mvn", "package", "--file", configuration.targetPath, "-Dmaven.repo.local",
            "${configuration.targetPath}/maven-local-repository"
        ).start()

        process.waitFor(5, TimeUnit.MINUTES)
        assertEquals(0, process.exitValue())
    }

    private fun gameCommands(vararg names: String) = names.toMutableList()
    private fun editorCommands(vararg names: String) = names.toMutableList()
    private fun queries(vararg names: String) = names.toMutableList()

    private fun createTempFile(): String {
        val file = File.createTempFile("ModelingSetupAcceptanceTest-", System.currentTimeMillis().toString())
        file.deleteOnExit()
        return file.absolutePath
    }
}