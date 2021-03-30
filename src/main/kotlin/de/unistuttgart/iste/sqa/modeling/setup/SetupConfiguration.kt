package de.unistuttgart.iste.sqa.modeling.setup

data class Image(
    val name: String,
    val dataEncodedInBase64: String
)

data class SetupConfiguration(
    var targetPath: String,
    var mpwName: String,
    var actorName: String,
    val gameCommands: MutableList<String> = mutableListOf(),
    val editorCommands: MutableList<String> = mutableListOf(),
    val actorQueries: MutableList<String> = mutableListOf(),
    val images: MutableList<Image> = mutableListOf(),
)