package io.github.spookylauncher.components.ui.swing.forms;

import javax.swing.*;
import java.awt.event.*;

public class MultiProgressDialog extends JDialog {
    private JPanel contentPane = new JPanel();
    public JScrollPane scrollPane;

    public MultiProgressDialog() {
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
}
