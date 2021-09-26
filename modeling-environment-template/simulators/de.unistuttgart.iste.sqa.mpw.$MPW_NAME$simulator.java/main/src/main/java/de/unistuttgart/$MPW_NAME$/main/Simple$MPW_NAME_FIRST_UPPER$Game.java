package de.unistuttgart.$MPW_NAME$.main;

import de.unistuttgart.$MPW_NAME$.facade.$STAGE_NAME_FIRST_UPPER$Loader;
import de.unistuttgart.$MPW_NAME$.facade.$ACTOR_NAME_FIRST_UPPER$;
import de.unistuttgart.$MPW_NAME$.facade.$MPW_NAME_FIRST_UPPER$Game;
import de.unistuttgart.$MPW_NAME$.ui.JavaFXUI;
import de.unistuttgart.iste.sqa.mpw.framework.exceptions.GameAbortedException;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static de.unistuttgart.iste.sqa.mpw.framework.utils.Preconditions.*;

public abstract class Simple$MPW_NAME_FIRST_UPPER$Game {
    private static final String DEFAULT_$MPW_NAME_ALL_UPPER$_$STAGE_NAME_ALL_UPPER$ = "/de.unistuttgart.$MPW_NAME$.$STAGE_NAME_PLURAL$/example01.ter";

    protected static void createInstance(Class<? extends Simple$MPW_NAME_FIRST_UPPER$Game> $MPW_NAME$ProgramClass) {
        try {
            var program = $MPW_NAME$ProgramClass.getDeclaredConstructor().newInstance();
            program.doRun();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Name of the environment variable used to determine the output interface
     */
    private static final String OUTPUT_INTERFACE_ENVIRONMENT_VARIABLE_NAME = "OUTPUT_INTERFACE";

    /**
     * Variable inherited to child classes containing the $ACTOR_NAME$
     * which is named $ACTOR_NAME$ here. Intentionally, no getter or setter is used
     * as they are introduced only after lecture 2.
     */
    protected final $ACTOR_NAME_FIRST_UPPER$ $ACTOR_NAME$;

    /**
     * The game object of this simple game. Can be used to start, stop, reset,
     * or display the game.
     */
    protected final $MPW_NAME_FIRST_UPPER$Game game = new $MPW_NAME_FIRST_UPPER$Game();

    /**
     * The current Simple$MPW_NAME_FIRST_UPPER$Game. Can be used to load a $STAGE_NAME$ or to
     * display the game in a new game window.
     */
    protected final Simple$MPW_NAME_FIRST_UPPER$Game currentGame = this;

    /**
     * A console object to demonstrate IO besides using the read or write methods
     * of $ACTOR_NAME$s.
     */
    protected final Console console = System.console();

    /**
     * Initialized a simple $MPW_NAME$ game by loading a default $STAGE_NAME$
     * and setting protected references to contain $ACTOR_NAME$ and
     * the game.
     */
    public Simple$MPW_NAME_FIRST_UPPER$Game() {
        initializeGame(get$STAGE_NAME_FIRST_UPPER$File());
        game.startGamePaused();

        this.$ACTOR_NAME$ = this.game.get$STAGE_NAME_FIRST_UPPER$().get$ACTOR_NAME_FIRST_UPPER_REST_LOWERCASE$();
    }

    protected String get$STAGE_NAME_FIRST_UPPER$File() {
        return DEFAULT_$MPW_NAME_ALL_UPPER$_$STAGE_NAME_ALL_UPPER$;
    }

    protected void initializeGame(String fileName) {
        try {
            $STAGE_NAME_FIRST_UPPER$Loader.initializeFor(game).loadFromResourceFile(fileName);
            game.hardReset();
        } catch (IOException e) {
            throw new RuntimeException("failed to load the default $STAGE_NAME$", e);
        }
    }

    /**
     * Predefined $MPW_NAME$ method designed to be overridden in subclass.
     * Put the $MPW_NAME$ code into this method. This parent class version
     * is empty, so that the $MPW_NAME$ does not do anything by default.
     */
    protected abstract void run();

    /**
     * Method to start the execution of a $MPW_NAME$ game and handle any exceptions happening
     * while running.
     */
    public final void doRun() {
        try {
            this.run();
        } catch (final GameAbortedException e) {
            // End this game
        } catch (final RuntimeException e) {
            this.game.confirmAlert(e);
            throw e;
        }
        this.game.stopGame();
    }

    /**
     * Displays the $MPW_NAME$ game in a new game window
     * The UI type can be specified in the config file or in the environment variable
     * OUTPUT_INTERFACE. Possible values are JAVA_FX, HTTP and NONE
     * The default is JAVA_FX.
     */
    protected void displayInNewGameWindow() {
        final String mode = UIMode.JAVA_FX;
        switch (mode) {
            case UIMode.JAVA_FX:
                JavaFXUI.displayInNewGameWindow(this.game);
                break;
            case UIMode.NONE:
                // ignore
                break;
            default:
                throw new IllegalStateException("Unknown output interface type, possible values are: " +
                        UIMode.JAVA_FX + " or " + UIMode.NONE);
        }
    }

    /*@
     @ requires true;
     @ ensures game.getCurrentGameMode() == Mode.INITIALIZING;
     @*/
    /**
     * Loads the $STAGE_NAME_FIRST_UPPER$ from a resources file.
     * Only absolute resource paths are allowed. E.g. the fileName "/$STAGE_NAME$.ter" represents the file
     * $STAGE_NAME$.ter in the resources directory
     * This resets the game if it was already started. After the $STAGE_NAME$ was loaded, the game is
     * in mode INITIALIZING. To start the game, game.startGame() should be called
     *
     * @param fileName An absolute path to the resource file. Must start with a "/"
     * @throws IllegalArgumentException if fileName is no absolute resource path (does not start with "/")
     *                                  or if the file was not found
     */
    protected final void load$STAGE_NAME_FIRST_UPPER$FromResourceFile(final String fileName) {
        checkNotNull(fileName);
        checkArgument(fileName.startsWith("/"), "fileName does not start with \"/\"");
        final InputStream $STAGE_NAME$FileStream = getClass().getResourceAsStream(fileName);
        checkArgument($STAGE_NAME$FileStream != null, "$STAGE_NAME$ file not found");
        try {
            $STAGE_NAME_FIRST_UPPER$Loader.initializeFor(game).loadFromResourceFile(fileName);
        } catch (IOException e) {
            game.confirmAlert(e);
        }
    }

    /**
     * Loads the UI Mode from the environment variable if possible
     *
     * @return The UI mode if the environment variable was set, otherwise an empty optional
     * @throws IllegalStateException if an illegal value is set
     */
    private static Optional<String> getUIModeFromEnvironmentVariable() {
        final String value = System.getenv(Simple$MPW_NAME_FIRST_UPPER$Game.OUTPUT_INTERFACE_ENVIRONMENT_VARIABLE_NAME);
        if (value != null) {
            try {
                return Optional.of(value);
            } catch (final IllegalArgumentException e) {
                throw new IllegalStateException("Illegal environmental variable", e);
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Different UI types
     * No enum is used because enums use reflection for valueOf which can cause issues if reflection is forbidden
     */
    private static final class UIMode {
        public static final String JAVA_FX = "JAVA_FX";
        public static final String NONE = "NONE";
    }
}
