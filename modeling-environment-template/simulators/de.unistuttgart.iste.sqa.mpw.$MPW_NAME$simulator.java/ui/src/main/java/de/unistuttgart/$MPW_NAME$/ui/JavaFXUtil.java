package de.unistuttgart.$MPW_NAME$.ui;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.CountDownLatch;

public final class JavaFXUtil {

    public static final void blockingExecuteOnFXThread(final Runnable runnable) {
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch doneLatch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    runnable.run();
                } finally {
                    doneLatch.countDown();
                }
            });

            try {
                doneLatch.await();
            } catch (final InterruptedException ex) { }
        } else {
            runnable.run();
        }
    }

    public static Image changeColor(final Image image, final Color color) {
        final int width = (int)image.getWidth();
        final int height = (int)image.getHeight();
        //Creating a writable image
        final WritableImage wImage = new WritableImage(width, height);

        //Reading color from the loaded image
        final PixelReader pixelReader = image.getPixelReader();

        //getting the pixel writer
        final PixelWriter writer = wImage.getPixelWriter();

        //Reading the color of the image
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                //Retrieving the color of the pixel of the loaded image
                final Color originalColor = pixelReader.getColor(x, y);
                Color newColor;
                if (originalColor.getBlue() == 1.0) {
                    newColor = Color.color(color.getRed(), color.getGreen(), color.getBlue(), originalColor.getOpacity());
                } else {
                    newColor = originalColor;
                }
                //Setting the color to the writable image
                writer.setColor(x, y, newColor);
            }
        }
        return wImage;
    }
}
