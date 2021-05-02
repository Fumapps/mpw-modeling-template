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

    @Test // Scenario: replace mpw name automatically is lowercase
    fun `GIVEN mpw name in mixed case WHEN generate file content THEN mpw name is inserted as lower case`() {
        withMpwName("HaMsTeR")
        andWithFakeFile("/root/myfile.txt", "hello \$MPW_NAME\$")
        generate()
        assertFile("/root/myfile.txt", "hello hamster")
    }

    @Test // Scenario: replace actor name
    fun `GIVEN actor name in mixed case WHEN generate file content THEN actor name is inserted as lower case`() {
        withActorName("HamsterActor")
        andWithFakeFile("/root/myfile.txt", "hello \$ACTOR_NAME\$")
        generate()
        assertFile("/root/myfile.txt", "hello hamsteractor")
    }

    @Test // Scenario: replace stage name
    fun `GIVEN stage name in mixed case WHEN generate file content THEN stage name is inserted as lower case`() {
        withStageName("Territory")
        andWithFakeFile("/root/myfile.txt", "hello \$STAGE_NAME\$")
        generate()
        assertFile("/root/myfile.txt", "hello territory")
    }

    //endregion

    //region Feature: special case suffix replacement

    @Test // Scenario: replace mpw name first upper
    fun `GIVEN mpw name WHEN generate file content with first letter uppercase placeholder THEN mpw name is inserted with first upper`() {
        withMpwName("myHamster")
        andWithFakeFile("/root/myfile.txt", "hello \$MPW_NAME_FIRST_UPPER\$")
        generate()
        assertFile("/root/myfile.txt", "hello MyHamster")
    }

    @Test // Scenario: replace actor name first upper
    fun `GIVEN actor name WHEN generate file content with first letter uppercase placeholder THEN actor name is inserted with first upper`() {
        withActorName("myHamster")
        andWithFakeFile("/root/myfile.txt", "hello \$ACTOR_NAME_FIRST_UPPER\$")
        generate()
        assertFile("/root/myfile.txt", "hello MyHamster")
    }

    @Test // Scenario: replace stage name first upper
    fun `GIVEN stage name WHEN generate file content with first letter uppercase placeholder THEN stage name is inserted with first upper`() {
        withStageName("myTerritory")
        andWithFakeFile("/root/myfile.txt", "hello \$STAGE_NAME_FIRST_UPPER\$")
        generate()
        assertFile("/root/myfile.txt", "hello MyTerritory")
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

    @Test // Scenario: replace actor name in file name
    fun `GIVEN actor name WHEN generate file name THEN actor name is inserted in file name`() {
        withActorName("Hamster")
        andWithFakeFile("/root/my_\$ACTOR_NAME\$.txt", "")
        generate()
        assertFileIsReplacedTo("/root/my_\$ACTOR_NAME\$.txt", "/root/my_hamster.txt")
    }

    @Test // Scenario: replace stage name in file name
    fun `GIVEN stage name WHEN generate file name THEN stage name is inserted in file name`() {
        withStageName("Territory")
        andWithFakeFile("/root/my_\$STAGE_NAME\$.txt", "")
        generate()
        assertFileIsReplacedTo("/root/my_\$STAGE_NAME\$.txt", "/root/my_territory.txt")
    }

    //endregion

    //region Feature: directory name replacement
    // TODO: test replacement of leaf/intermediary directories
    //endregion

    //region Feature: dynamic replacements - Images

    @Test // Scenario: generate image files for placeholders
    fun `GIVEN two images WHEN generate file name THEN file image placeholders are replaced`() {
        withImages("apple: =ENCODED_APPLE_IMAGE=", "tree: =ENCODED_TREE_IMAGE=")
        andWithFakeFile("/root/\$IMAGES\$", "")
        generate()
        assertFileNotExists("/root/\$IMAGES\$")
        assertFile("/root/apple.png", "ENCODED: =ENCODED_APPLE_IMAGE=")
        assertFile("/root/tree.png", "ENCODED: =ENCODED_TREE_IMAGE=")
    }

    //endregion

    //region Feature: looped file replacements

    @Test // Scenario: generate game command files for placeholders
    fun `GIVEN two game commands WHEN generate for file name placeholder THEN two game command files are replaced`() {
        withGameCommands("foo", "bar")
        andWithFakeFile("/root/\$FOREACH_GAME_COMMAND\$\$COMMAND_NAME\$\$END_FOREACH\$.henshin", "<content>")
        generate()
        assertFileNotExists("/root/\$FOREACH_GAME_COMMAND\$\$COMMAND_NAME\$\$END_FOREACH\$.henshin")
        assertFile("/root/foo.henshin", "<content>")
        assertFile("/root/bar.henshin", "<content>")
    }

    @Test // Scenario: generate game command files for placeholders also in file content
    fun `GIVEN two game commands WHEN generate for content command name placeholder THEN game command name in files is replaced`() {
        withGameCommands("foo", "bar")
        andWithFakeFile("/root/\$FOREACH_GAME_COMMAND\$\$COMMAND_NAME\$\$END_FOREACH\$.henshin", "\$COMMAND_NAME\$")
        generate()
        assertFile("/root/foo.henshin", "foo")
        assertFile("/root/bar.henshin", "bar")
    }

    @Test // Scenario: generate game command files also with other placeholders
    fun `GIVEN game command WHEN generate for command name placeholder AND for mpw name THEN both placeholders are replaced`() {
        withGameCommands("foo")
        withMpwName("hamster")
        andWithFakeFile("/root/\$FOREACH_GAME_COMMAND\$\$COMMAND_NAME\$_\$MPW_NAME\$\$END_FOREACH\$.henshin", "\$COMMAND_NAME\$: \$MPW_NAME\$")
        generate()
        assertFile("/root/foo_hamster.henshin", "foo: hamster")
    }

    @Test // Scenario: generate editor command files for placeholders
    fun `GIVEN two editor commands WHEN generate for file name placeholder THEN two editor command files are replaced`() {
        withEditorCommands("foo", "bar")
        andWithFakeFile("/root/\$FOREACH_EDITOR_COMMAND\$\$COMMAND_NAME\$\$END_FOREACH\$.henshin", "<content>")
        generate()
        assertFileNotExists("/root/\$FOREACH_EDITOR_COMMAND\$\$COMMAND_NAME\$\$END_FOREACH\$.henshin")
        assertFile("/root/foo.henshin", "<content>")
        assertFile("/root/bar.henshin", "<content>")
    }

    @Test // Scenario: generate query files for placeholders
    fun `GIVEN two queries WHEN generate for file name placeholder THEN two query files are replaced`() {
        withActorQueries("foo", "bar")
        andWithFakeFile("/root/\$FOREACH_ACTOR_QUERY\$\$QUERY_NAME\$\$END_FOREACH\$.query", "<content>")
        generate()
        assertFileNotExists("/root/\$FOREACH_QUERY\$\$QUERY_NAME\$\$END_FOREACH\$.query")
        assertFile("/root/foo.query", "<content>")
        assertFile("/root/bar.query", "<content>")
    }

    @Test // Scenario: generate query files for placeholders also in file content
    fun `GIVEN two queries WHEN generate for content command name placeholder THEN query name in files is replaced`() {
        withActorQueries("foo", "bar")
        andWithFakeFile("/root/\$FOREACH_ACTOR_QUERY\$\$QUERY_NAME\$\$END_FOREACH\$.query", "\$QUERY_NAME\$")
        generate()
        assertFile("/root/foo.query", "foo")
        assertFile("/root/bar.query", "bar")
    }

    //endregion

    @BeforeEach
    private fun setup() {
        configuration = SetupConfiguration(dummyFilePath, "", "", "")
        fileSystemFake = FileSystemFake()
        sut = SetupGenerator(fileSystemFake)
    }

    private fun withMpwName(name: String) {
        configuration.mpwName = name
    }

    private fun withActorName(name: String) {
        configuration.actorName = name
    }

    private fun withStageName(name: String) {
        configuration.stageName = name
    }

    private fun withImages(vararg images: String) {
        configuration.images += images
            .map { it.split(": ") }
            .map { Image(it[0], it[1]) }
            .toMutableList()
    }

    private fun withGameCommands(vararg commandNames: String) {
        configuration.gameCommands += commandNames
    }

    private fun withEditorCommands(vararg commandNames: String) {
        configuration.editorCommands += commandNames
    }

    private fun withActorQueries(vararg queryNames: String) {
        configuration.actorQueries += queryNames
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