package de.unistuttgart.$MPW_NAME$.ui;

import de.unistuttgart.iste.sqa.mpw.framework.mpw.UserInputInterface;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;
import java.util.function.Function;

public class JavaFXInputInterface implements UserInputInterface {

    private volatile Optional<Dialog<?>> currentDialog = Optional.empty();

    private class TextDialogWrapper {

        Optional<String> result;

        public void showAndWait(final String message, final String defaultValue, final Function<String, Boolean> validator) {
            JavaFXUtil.blockingExecuteOnFXThread(() -> {
                final TextInputDialog textInputDialog = new TextInputDialog(defaultValue);
                currentDialog = Optional.of(textInputDialog);
                textInputDialog.setTitle("$MPW_NAME_FIRST_UPPER$ needs input!");
                textInputDialog.setHeaderText(message);

                final Button okButton = (Button) textInputDialog.getDialogPane().lookupButton(ButtonType.OK);
                textInputDialog.getDialogPane().getButtonTypes().remove(ButtonType.CANCEL);
                final TextField inputField = textInputDialog.getEditor();
                final BooleanBinding isInvalid = Bindings.createBooleanBinding(() -> !validator.apply(inputField.getText()), inputField.textProperty());
                okButton.disableProperty().bind(isInvalid);
                result = textInputDialog.showAndWait();
                currentDialog = Optional.empty();
            });
        }

    }

    @Override
    public int readInteger(final String message) {
        final TextDialogWrapper wrapper = new TextDialogWrapper();
        wrapper.showAndWait(message, "0", this::validateInt);
        return wrapper.result.map(Integer::parseInt).orElse(-1);
    }

    @Override
    public String readString(final String message) {
        final TextDialogWrapper wrapper = new TextDialogWrapper();
        wrapper.showAndWait(message, "", this::validateString);
        return wrapper.result.orElse(null);
    }

    private boolean validateString(final String s) {
        return s != null && !s.equals("");
    }

    private boolean validateInt(final String s) {
        try {
            final int result = Integer.parseInt(s);
            return result >= 0;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void confirmAlert(final Throwable t) {
        JavaFXUtil.blockingExecuteOnFXThread(() -> {
            final Dialog<ButtonType> alertDialog = new Alert(AlertType.ERROR);
            this.currentDialog = Optional.of(alertDialog);
            alertDialog.setTitle("An exception occurred, program execution stopped.");
            alertDialog.setHeaderText("An exception of type " + t.getClass().getSimpleName() +
                    " occurred.\n" + t.getMessage() + ".\nProgram execution will be aborted. Please "+
                    "fix your program and try again.");
            alertDialog.showAndWait();
            this.currentDialog = Optional.empty();
        });
    }

    @Override
    public void abort() {
        this.currentDialog.ifPresent(dialog -> Platform.runLater(dialog::close));
        this.currentDialog = Optional.empty();
    }

}