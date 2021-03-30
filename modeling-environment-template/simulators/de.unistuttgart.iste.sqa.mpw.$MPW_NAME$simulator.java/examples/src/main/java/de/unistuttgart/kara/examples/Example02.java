package de.unistuttgart.$MPW_NAME$.examples;

import de.unistuttgart.$MPW_NAME$.main.Simple$MPW_NAME_FIRST_UPPER$Game;

public class Example02 extends Simple$MPW_NAME_FIRST_UPPER$Game {
    public static void main(final String[] args) {
        createInstance(Example02.class);
    }

    @Override
    protected String get$STAGE_NAME_FIRST_UPPER$File() {
        return "/$STAGE_NAME$s/example02.ter";
    }

    /**
     * Another $MPW_NAME$ program.
     */
    @Override
    protected void run() {
        displayInNewGameWindow();

        $MPW_NAME$.turnRight();

        for (int i = 0; i < 3; i++) {
            $MPW_NAME$.move();
            $MPW_NAME$.putLeaf();
        }

        rotate180Degrees();

        for (int i = 0; i < 3; i++) {
            $MPW_NAME$.removeLeaf();
            $MPW_NAME$.move();
        }
    }

    private void rotate180Degrees() {
        $MPW_NAME$.turnLeft();
        $MPW_NAME$.turnLeft();
    }
}
