package io.github.spookylauncher.components.ui.spi;

public interface ProgressBar {
    int getValue();

    int getMinValue();

    int getMaxValue();

    void setValue(int value);

    void setMinValue(int minValue);

    void setMaxValue(int maxValue);
}