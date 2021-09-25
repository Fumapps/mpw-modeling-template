package de.unistuttgart.$MPW_NAME$.examples;

import de.unistuttgart.$MPW_NAME$.main.Simple$MPW_NAME_FIRST_UPPER$Game;

public class Example01 extends Simple$MPW_NAME_FIRST_UPPER$Game {
    public static void main(final String[] args) {
        createInstance(Example01.class);
    }

    @Override
    protected void run() {
        displayInNewGameWindow();
        $MPW_NAME$.move();
        // TODO: call further interesting commands
    }

}
