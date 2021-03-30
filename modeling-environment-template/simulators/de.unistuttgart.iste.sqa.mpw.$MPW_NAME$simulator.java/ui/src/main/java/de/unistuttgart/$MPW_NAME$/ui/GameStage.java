package de.unistuttgart.$MPW_NAME$.ui;

import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.GameViewInput;
import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.GameViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

class GameStage extends Stage {

    private GameSceneController sceneController;

    public GameStage(final GameViewInput gameViewInput, final GameViewModel gameViewModel) throws IOException {
        super();
        prepareStage();
        sceneController.connectToGame(gameViewInput, gameViewModel);
        this.setOnCloseRequest(event -> {
            new Thread(gameViewInput::close).start();
        });
    }

    public void prepareStage() throws IOException {
        this.setTitle("$MPW_NAME_FIRST_UPPER$ Simulator - Game Window");
        final BorderPane root = (BorderPane) loadFromFXML();
        final Scene scene = new Scene(root, 700, 400);
        scene.getStylesheets().add("css/game.css");
        this.minHeightProperty().bind(root.minHeightProperty());
        this.minWidthProperty().bind(root.minWidthProperty());
        this.setScene(scene);
    }

    private Parent loadFromFXML() throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader()
                .getResource("fxml/GameScene.fxml"));
        final Parent root = fxmlLoader.load();
        this.sceneController = (GameSceneController) fxmlLoader.getController();
        return root;
    }
}
