package io.github.spookylauncher.components.ui.swing.forms;

import javax.swing.*;
import java.awt.*;

public class MultiProgressDialog extends JDialog {
    private final JPanel mainContentPanel;
    public JScrollPane scrollPane;

    private void revalidateScrollPane() {
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void addPanel(final JPanel panel) {
        mainContentPanel.add(panel);
        revalidateScrollPane();
    }

    public boolean removePanel(final JPanel panel) {
        mainContentPanel.remove(panel);
        revalidateScrollPane();
        return mainContentPanel.getComponentCount() == 0;
    }

    public MultiProgressDialog() {
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        mainContentPanel = new JPanel();

        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(mainContentPanel);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }
}
