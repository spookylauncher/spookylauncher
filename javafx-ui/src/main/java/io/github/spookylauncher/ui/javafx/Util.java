package io.github.spookylauncher.ui.javafx;

import javafx.application.Platform;
import javafx.scene.image.PixelReader;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util {

    public static interface NoReturnCallable {
        void call() throws Exception;
    }

    public static java.awt.Image jfxImageToAwt(javafx.scene.image.Image image) {
        BufferedImage awtImg = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        PixelReader reader = image.getPixelReader();

        for(int y = 0; y < awtImg.getHeight(); y++) {
            for(int x = 0; x < awtImg.getWidth(); x++) {
                awtImg.setRGB(x, y, reader.getArgb(x, y));
            }
        }

        return awtImg;
    }

    public static void call(NoReturnCallable action) {
        call(
            () -> {
                action.call();
                return null;
            }
        );
    }

    public static <T> T call(Callable<T> action) {
        if (Platform.isFxApplicationThread()) {
            try {
                return action.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            FutureTask<T> task = new FutureTask<>(() -> {
                try {
                    return action.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Platform.runLater(task);
            try {
                return task.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        }
    }
}
