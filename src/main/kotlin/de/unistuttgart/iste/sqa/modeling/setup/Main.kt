package de.unistuttgart.iste.sqa.modeling.setup

import java.io.File

fun main() {
    val configuration = SetupConfiguration(
        File("generate-mpw-test").absolutePath,
        "racoon",
        "Racoon",
        "Territory",
        gameCommands("move", "turnLeft", "eat"),
        editorCommands("addWallToTile", "addNutToTile"),
        queries("frontIsClear", "nutAvailable")
    )

    val sut = ModelingTemplateProcessor()
    sut.createProjectStructureFromTemplate(configuration)
}

private fun gameCommands(vararg names: String) = names.toMutableList()
private fun editorCommands(vararg names: String) = names.toMutableList()
private fun queries(vararg names: String) = names.toMutableList()
