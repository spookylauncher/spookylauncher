package io.github.spookylauncher.ui.javafx.impl;

import io.github.spookylauncher.ui.MessageType;
import io.github.spookylauncher.ui.Messages;
import io.github.spookylauncher.ui.javafx.Util;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class JFXMessagesImpl implements Messages {

    @Override
    public void message(String title, String message, MessageType type) {
        Util.call(() -> {
            AlertType alertType;

            switch (type) {
                case INFO:
                    alertType = AlertType.INFORMATION;
                    break;
                case WARNING:
                    alertType = AlertType.WARNING;
                    break;
                case ERROR:
                    alertType = AlertType.ERROR;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            Alert alert = new Alert(alertType);

            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            alert.showAndWait();
        });
    }
}
