package de.unistuttgart.iste.sqa.modeling.setup.generation

import de.unistuttgart.iste.sqa.modeling.setup.util.toFirstUpper
import java.lang.StringBuilder

/**
 * @param foreachCollectionName: short name for the "foreach"-collection name, e.g. "GAME_COMMAND", "EDITOR_COMMAND"
 * @param elementVariableName: short name for the variable-placeholder base name, e.g. "COMMAND", "QUERY", "IMAGE"
 * @param elements: list of the elements, e.g. ["move", "turnLeft"]
 */
class ForEachReplacer(foreachCollectionName: String,
                      elementVariableName: String,
                      private val elements: List<String>) {
    private val foreachPlaceholder = "\$FOREACH_$foreachCollectionName\$"
    private val foreachEndPlaceholder = "\$END_FOREACH\$"

    private val lowerCaseNamePlaceholder = "\$${elementVariableName}_NAME\$"
    private val upperCaseNamePlaceholder = "\$${elementVariableName}_NAME_ALL_UPPER\$"
    private val firstUpperNamePlaceholder = "\$${elementVariableName}_NAME_FIRST_UPPER\$"

    private val result = StringBuilder()
    private var lastIndex = 0

    fun replace(text: String): String {
        result.clear()

        while (lastIndexWasNotTheEndOfText(text)) {
            replaceForeachPlaceholdersAndIncreaseLastIndex(text)
        }

        return result.toString()
    }

    private fun lastIndexWasNotTheEndOfText(text: String) = lastIndex < text.length

    private fun replaceForeachPlaceholdersAndIncreaseLastIndex(text: String) {
        val foreachStartIndex = text.indexOf(foreachPlaceholder, lastIndex)
        if (foreachStartIndex >= 0) {
            replaceForeachAtIndex(text, foreachStartIndex)
        } else {
            addRemainingTextToOutput(text)
        }
    }

    private fun replaceForeachAtIndex(text: String, foreachStartIndex: Int) {
        addPartBeforeForeachToOutput(text, foreachStartIndex)
        val foreachBody = extractForeachBody(text, foreachStartIndex)
        elements.forEachIndexed { index, it ->
            if (index < elements.lastIndex) {
                addForeachBodyForElement(foreachBody.smartRemoveNewLines(), it)
            } else {
                addForeachBodyForElement(foreachBody.smartRemoveNewLinesForLastElement(), it)
            }
        }
    }

    private fun extractForeachBody(text: String, foreachStartIndex: Int): String {
        val foreachEndIndex = findForeachEndAndIncreaseLastIndex(text, foreachStartIndex)
        val foreachBody = text.substring(foreachStartIndex + foreachPlaceholder.length, foreachEndIndex)
        return foreachBody
    }

    private fun findForeachEndAndIncreaseLastIndex(text: String, foreachStartIndex: Int): Int {
        val foreachEndIndex = text.indexOf(foreachEndPlaceholder, foreachStartIndex)
        require(foreachEndIndex > foreachStartIndex)
        lastIndex = foreachEndIndex + foreachEndPlaceholder.length
        return foreachEndIndex
    }

    private fun addForeachBodyForElement(foreachBody: String, element: String) {
        result.append(foreachBody.replaceVariablePlaceholders(element))
    }

    private fun String.replaceVariablePlaceholders(element: String) =
        this.replace(lowerCaseNamePlaceholder, element)
            .replace(upperCaseNamePlaceholder, element.toUpperCase())
            .replace(firstUpperNamePlaceholder, element.toFirstUpper())

    private fun addRemainingTextToOutput(text: String) {
        addPartBeforeForeachToOutput(text, text.length)
        lastIndex = text.length
    }

    private fun addPartBeforeForeachToOutput(text: String, foreachStartIndex: Int) {
        result.append(text.substring(lastIndex, foreachStartIndex))
    }

    /** Removes direct newlines on the foreach placeholders (only start), which makes generation of code more pretty */
    private fun String.smartRemoveNewLines(): String {
        if (startsWith("\r\n")) {
            return substring(2)
        }
        if (startsWith("\n")) {
            return substring(1)
        }
        return this
    }
    /** Removes direct newlines on the foreach placeholders (start+end), which makes generation of code more pretty */
    private fun String.smartRemoveNewLinesForLastElement(): String {
        smartRemoveNewLines().apply {
            if (endsWith("\r\n")) {
                return substring(0, length-2)
            }
            if (endsWith("\n")) {
                return substring(0, length-1)
            }
            return this
        }
    }
}

