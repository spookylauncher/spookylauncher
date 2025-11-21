package io.github.spookylauncher.components.ui.swing.forms;

import io.github.spookylauncher.components.JREController;
import io.github.spookylauncher.components.ui.spi.Messages;
import io.github.spookylauncher.tree.launcher.Options;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.OptionsController;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.PlaceholderTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.regex.Pattern;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    public JButton buttonCancel;
    public JLabel ramLabel;
    public JLabel javaPathLabel;
    public JSlider ramSlider;
    private JTextField javaPathField;
    public JTextField ramField;
    public JLabel ramVolumeLabel;
    public JLabel nicknameLabel;
    private PlaceholderTextField nicknameField;
    private JCheckBox discordRpcCheckbox;
    private JLabel discordPresenceLabel;
    public JButton jreSettingsButton;

    private boolean ramFocused;

    public SettingsDialog(int minMemory, int maxMemory, JREController jreController, Messages messages, OptionsController optionsController, Translator localeController) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        ((AbstractDocument) ramField.getDocument()).setDocumentFilter(
                new DocumentFilter() {
                    private final Pattern regexCheck = Pattern.compile("[0-9]+");

                    private void updateSlider() {
                        int memory = Integer.parseInt(ramField.getText());

                        if (memory < minMemory) memory = minMemory;
                        else if (memory > maxMemory) memory = maxMemory;

                        ramSlider.setValue(memory);
                    }

                    @Override
                    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
                        if (str == null) return;

                        if (regexCheck.matcher(str).matches()) {
                            super.insertString(fb, offs, str, a);

                            updateSlider();
                        }
                    }

                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String str, AttributeSet attrs)
                            throws BadLocationException {
                        if (str == null) {
                            return;
                        }

                        if (regexCheck.matcher(str).matches()) {
                            fb.replace(offset, length, str, attrs);

                            updateSlider();
                        }
                    }
                }
        );

        ramField.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                ramFocused = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                ramFocused = false;

                ramField.setText(String.valueOf(ramSlider.getValue()));
            }
        });

        Options options = optionsController.getOptions();

        ramSlider.setMaximum(maxMemory);
        ramSlider.setMinimum(minMemory);
        ramSlider.setValue(options.memory);

        discordRpcCheckbox.setSelected(options.discordPresence);

        nicknameField.setText(options.getNickname());
        nicknameField.setPlaceholder("Player####");

        ramField.setText(String.valueOf(options.memory));

        ramSlider.addChangeListener((ChangeEvent e) -> {
            if (!ramFocused) ramField.setText(String.valueOf(ramSlider.getValue()));
        });

        buttonOK.addActionListener(e -> {
            options.memory = ramSlider.getValue();

            options.discordPresence = discordRpcCheckbox.isSelected();

            options.setNickname(nicknameField.getText());

            try {
                optionsController.store();
            } catch (IOException ex) {
                // TODO: normal catch
                ex.printStackTrace();
            }

            dispose();
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        jreSettingsButton.addActionListener(
                e -> {
                    JreSettingsDialog dialog = new JreSettingsDialog(
                            messages,
                            jreController,
                            optionsController,
                            localeController
                    );

                    Locale locale = localeController.getLocale();

                    dialog.buttonCancel.setText(locale.get("cancel"));
                    dialog.useExternalJreButton.setText(locale.get("useExternalJre"));
                    dialog.useInstalledJres.setText(locale.get("useInstalledJres"));

                    dialog.setLocationRelativeTo(SettingsDialog.this);

                    dialog.setTitle(getTitle());

                    dialog.setIconImage(getIconImages().get(0));

                    dialog.setVisible(true);
                }
        );
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 7, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        ramLabel = new JLabel();
        ramLabel.setText("RAM");
        panel3.add(ramLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ramSlider = new JSlider();
        ramSlider.setEnabled(true);
        ramSlider.setExtent(0);
        panel3.add(ramSlider, new GridConstraints(2, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ramField = new JTextField();
        ramField.setText("1024");
        panel3.add(ramField, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(10, -1), null, 0, false));
        ramVolumeLabel = new JLabel();
        ramVolumeLabel.setText("MB");
        panel3.add(ramVolumeLabel, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nicknameLabel = new JLabel();
        nicknameLabel.setText("Nickname");
        panel3.add(nicknameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nicknameField = new PlaceholderTextField();
        nicknameField.setText("");
        nicknameField.setToolTipText("");
        panel3.add(nicknameField, new GridConstraints(1, 1, 1, 6, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        discordRpcCheckbox = new JCheckBox();
        discordRpcCheckbox.setHorizontalAlignment(10);
        discordRpcCheckbox.setHorizontalTextPosition(2);
        discordRpcCheckbox.setText("");
        panel3.add(discordRpcCheckbox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discordPresenceLabel = new JLabel();
        discordPresenceLabel.setText("Discord RP");
        panel3.add(discordPresenceLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jreSettingsButton = new JButton();
        jreSettingsButton.setText("Open JRE Settings");
        contentPane.add(jreSettingsButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}