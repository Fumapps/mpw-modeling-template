package de.unistuttgart.iste.sqa.modeling.setup

import de.unistuttgart.iste.sqa.modeling.setup.testdoubles.FileSystemFake
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SetupGeneratorTest {
    private val dummyFilePath = "/root"

    private lateinit var configuration: SetupConfiguration
    private lateinit var sut: SetupGenerator
    private lateinit var fileSystemFake: FileSystemFake

    //region Feature: simple file content replacement

    @Test // Scenario: replace mpw name
    fun `GIVEN mpw name WHEN generate file content with placeholder THEN mpw name is inserted`() {
        withMpwName("hamster")
        andWithFakeFile("/root/myfile.txt", "hello \$MPW_NAME\$")
        generate()
        assertFile("/root/myfile.txt", "hello hamster")
    }

    @Test // Scenario: replace mpw name automaticall is lowercase
    fun `GIVEN mpw name in mixed case WHEN generate file content THEN mpw name is inserted as lower case`() {
        withMpwName("HaMsTeR")
        andWithFakeFile("/root/myfile.txt", "hello \$MPW_NAME\$")
        generate()
        assertFile("/root/myfile.txt", "hello hamster")
    }

    //endregion

    //region Feature: simple file name replacement

    @Test // Scenario: replace mpw name in file name
    fun `GIVEN mpw name WHEN generate file name THEN mpw name is inserted in file name`() {
        withMpwName("hamster")
        andWithFakeFile("/root/my_\$MPW_NAME\$.txt", "")
        generate()
        assertFileIsReplacedTo("/root/my_\$MPW_NAME\$.txt", "/root/my_hamster.txt")
    }

    //endregion

    @BeforeEach
    private fun setup() {
        configuration = SetupConfiguration(dummyFilePath, "", "")
        fileSystemFake = FileSystemFake()
        sut = SetupGenerator(fileSystemFake)
    }

    private fun withMpwName(mpwName: String) {
        configuration.mpwName = mpwName
    }

    private fun andWithFakeFile(path: String, content: String) {
        fileSystemFake.writeFile(path, content)
    }

    private fun generate() {
        sut.generate(configuration)
    }

    private fun assertFile(path: String, expectedContent: String) {
        assertTrue(fileSystemFake.fileMap.containsKey(path))
        val actualContent = fileSystemFake.readFile(path)
        assertEquals(expectedContent, actualContent)
    }

    private fun assertFileIsReplacedTo(originalPath: String, newPath: String) {
        assertFileNotExists(originalPath)
        assertFileExists(newPath)
    }

    private fun assertFileExists(path: String) {
        assertTrue(fileSystemFake.fileMap.containsKey(path))
    }

    private fun assertFileNotExists(path: String) {
        assertFalse(fileSystemFake.fileMap.containsKey(path))
    }
}