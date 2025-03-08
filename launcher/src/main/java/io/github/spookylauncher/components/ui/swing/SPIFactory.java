package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.components.ui.spi.Dialog;
import io.github.spookylauncher.components.ui.spi.Label;
import io.github.spookylauncher.components.ui.spi.ProgressBar;

import javax.swing.*;
import javax.swing.text.JTextComponent;

class SPIFactory {
    public static ProgressBar getProgressBar(JProgressBar progress) {
        return new ProgressBar() {
            @Override
            public int getValue() {
                return progress.getValue();
            }

            @Override
            public int getMinValue() {
                return progress.getMinimum();
            }

            @Override
            public int getMaxValue() {
                return progress.getMaximum();
            }

            @Override
            public void setValue(int value) {
                progress.setValue(value);
            }

            @Override
            public void setMinValue(int minValue) {
                progress.setMinimum(minValue);
            }

            @Override
            public void setMaxValue(int maxValue) {
                progress.setMaximum(maxValue);
            }
        };
    }

    public static Label getLabel(JTextComponent text) {
        return new Label() {
            @Override
            public String getText() {
                return text.getText();
            }

            @Override
            public void setText(String newText) {
                text.setText(newText);
            }
        };
    }

    public static Label getLabel(JLabel label) {
        return new Label() {
            @Override
            public String getText() {
                return label.getText();
            }

            @Override
            public void setText(String text) {
                label.setText(text);
            }
        };
    }

    public static Dialog getDialog(JDialog dialog, Label label) {
        return new Dialog() {
            @Override
            public String getTitle() {
                return dialog.getTitle();
            }

            @Override
            public void setTitle(String title) {
                dialog.setTitle(title);
            }

            @Override
            public void setVisible(boolean visible) {
                dialog.setVisible(visible);
            }

            @Override
            public String getText() {
                return label.getText();
            }

            @Override
            public void setText(String text) {
                label.setText(text);
            }
        };
    }
}