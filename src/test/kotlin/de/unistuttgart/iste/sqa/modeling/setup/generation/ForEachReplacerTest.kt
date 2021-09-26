package de.unistuttgart.iste.sqa.modeling.setup.generation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ForEachReplacerTest {
    private lateinit var sut: ForEachReplacer
    private lateinit var actual: String

    @Test
    fun `GIVEN text without placeholder WHEN replace foreach placeholders THEN text is unchanged`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = emptyArray())
        replaceForeachPlaceholders("my text")
        assertReplacedText("my text")
    }

    @Test
    fun `GIVEN text with FOREACH_GAME_COMMAND placeholder AND one COMMAND_NAME usage WHEN replace foreach placeholders AND we have no commands THEN placeholders are removed`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = emptyArray())
        replaceForeachPlaceholders("begin \$FOREACH_GAME_COMMAND\$call \$COMMAND_NAME\$\$END_FOREACH\$")
        assertReplacedText("begin ")
    }

    @Test
    fun `GIVEN text with FOREACH_GAME_COMMAND placeholder AND one COMMAND_NAME usage WHEN replace foreach placeholders AND we have 1 command THEN command name is inserted`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = arrayOf("move"))
        replaceForeachPlaceholders("begin \$FOREACH_GAME_COMMAND\$call \$COMMAND_NAME\$\$END_FOREACH\$")
        assertReplacedText("begin call move")
    }

    @Test
    fun `GIVEN text with FOREACH_GAME_COMMAND placeholder AND one COMMAND_NAME_FIRST_UPPER usage WHEN replace foreach placeholders AND we have 1 command THEN command name is inserted`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = arrayOf("move"))
        replaceForeachPlaceholders("begin \$FOREACH_GAME_COMMAND\$call \$COMMAND_NAME_FIRST_UPPER\$\$END_FOREACH\$")
        assertReplacedText("begin call Move")
    }

    @Test
    fun `GIVEN text with FOREACH_GAME_COMMAND placeholder AND one COMMAND_NAME_ALL_UPPER usage WHEN replace foreach placeholders AND we have 1 command THEN command name is inserted`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = arrayOf("move"))
        replaceForeachPlaceholders("begin \$FOREACH_GAME_COMMAND\$call \$COMMAND_NAME_ALL_UPPER\$\$END_FOREACH\$")
        assertReplacedText("begin call MOVE")
    }

    @Test
    fun `GIVEN text with FOREACH_GAME_COMMAND placeholder AND one COMMAND_NAME usage WHEN replace foreach placeholders AND we have 2 commands THEN command names are inserted`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = arrayOf("move", "turnLeft"))
        replaceForeachPlaceholders("\$FOREACH_GAME_COMMAND\$\n" +
                "call \$COMMAND_NAME\$\n" +
                "\$END_FOREACH\$")
        assertReplacedText(
            "call move\n" +
            "call turnLeft")
    }

    @Test
    fun `GIVEN text with TWO FOREACH_GAME_COMMAND placeholders AND one COMMAND_NAME usage WHEN replace foreach placeholders AND we have 1 command THEN command names are inserted for both foreachs`() {
        with(variableName = "COMMAND", collectionName = "GAME_COMMAND", elements = arrayOf("move"))
        replaceForeachPlaceholders(
                "\$FOREACH_GAME_COMMAND\$\n" +
                "1st call \$COMMAND_NAME\$\n" +
                "\$END_FOREACH\$\n" +
                "\$FOREACH_GAME_COMMAND\$\n" +
                "2nd call \$COMMAND_NAME\$\n" +
                "\$END_FOREACH\$")
        assertReplacedText(
            "1st call move\n" +
            "2nd call move")
    }

    private fun with(
        variableName: String,
        collectionName: String,
        vararg elements: String) {
        sut = ForEachReplacer(collectionName, variableName, elements.toList())
    }

    private fun replaceForeachPlaceholders(input: String) {
        actual = sut.replace(input)
    }

    private fun assertReplacedText(expected: String) {
        assertEquals(expected, actual)
    }
}