package io.github.spookylauncher.ui;

public interface ProgressBar {
    int getValue();

    int getMinValue();

    int getMaxValue();

    void setValue(int value);

    void setMinValue(int minValue);

    void setMaxValue(int maxValue);
}
