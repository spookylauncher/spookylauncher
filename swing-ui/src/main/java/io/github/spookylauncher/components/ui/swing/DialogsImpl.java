package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.components.ui.*;
import io.github.spookylauncher.components.ui.Dialog;
import io.github.spookylauncher.components.ui.Label;
import io.github.spookylauncher.components.ui.swing.beauty.UIBeautician;
import io.github.spookylauncher.components.ui.swing.forms.MultiProgressDialog;
import io.github.spookylauncher.io.InstallAdapter;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.ui.swing.forms.ConfirmDialog;
import io.github.spookylauncher.components.ui.swing.forms.ProgressPanel;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.structures.tuple.Tuple2;
import io.github.spookylauncher.util.structures.tuple.Tuple3;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

class DialogsImpl extends LauncherComponent implements Dialogs {

    private final SwingUIProvider provider;

    private final TitlePanelImpl titlePanel;

    private MultiProgressDialog multiProgressDialog;

    DialogsImpl(final ComponentsController components, final SwingUIProvider provider) {
        super("Swing API Dialogs", components);

        this.titlePanel = (TitlePanelImpl) provider.panel();
        this.provider = provider;
    }

    @Override
    public Dialog showConfirmDialog(Runnable ok, String title, String message) {
        return showConfirmDialog(ok, title, message, null);
    }

    @Override
    public Dialog showConfirmDialog(Runnable ok, String title, String message, Runnable cancel) {
        ConfirmDialog dialog = new ConfirmDialog(ok, cancel);

        dialog.setTitle(title);

        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.setSize(455, 175);

        final Translator translator = components.get(Translator.class);

        dialog.textArea.setText(message);
        dialog.scrollPane.setBorder(null);
        dialog.scrollPane.setOpaque(false);
        dialog.buttonOK.setText(translator.get("yes"));
        dialog.buttonCancel.setText(translator.get("no"));

        dialog.setIconImage(provider.window().getIcon());

        dialog.setLocationRelativeTo(provider.getFrame());

        dialog.setVisible(true);

        UIBeautician.comb(dialog);

        return dialog;
    }

    @Override
    public Tuple3<Label, ProgressBar, Dialog> createProgressDialog(String title) {
        Tuple3<JLabel, JProgressBar, JDialog> tuple = createSwingProgressDialog(title);

        Label label;

        return new Tuple3<>(
                label = SPIFactory.getLabel(tuple.x),
                SPIFactory.getProgressBar(tuple.y),
                SPIFactory.getDialog(tuple.z, label));
    }

    @Override
    public Tuple3<Label, ProgressBar, Runnable> createProgressView() {
        Tuple3<JLabel, JProgressBar, Runnable> tuple = createSwingProgressView();

        return new Tuple3<>(
                SPIFactory.getLabel(tuple.x),
                SPIFactory.getProgressBar(tuple.y),
                tuple.z
        );
    }

    private void setupSwingDialog(JDialog dialog) {
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(provider.getFrame());

        dialog.setSize(525, 150);

        dialog.setIconImage(provider.window().getIcon());

        dialog.setVisible(true);

        UIBeautician.comb(dialog);
    }

    private MultiProgressDialog getMultiProgressDialog() {
        if(multiProgressDialog == null) {
            multiProgressDialog = new MultiProgressDialog();
            setupSwingDialog(multiProgressDialog);
        }

        return multiProgressDialog;
    }

    private void closeMultiProgressDialog() {
        if(multiProgressDialog != null) {
            JDialog dialog = multiProgressDialog;
            dialog.setVisible(false);
            dialog.dispose();
            multiProgressDialog = null;
        }
    }

    private Tuple3<JLabel, JProgressBar, Runnable> createSwingProgressView() {
        ProgressPanel panel = new ProgressPanel();

        panel.panel.setPreferredSize(new Dimension(525, 50));
        panel.panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        getMultiProgressDialog().addPanel(panel.panel);

        UIBeautician.comb(panel.panel);

        return new Tuple3<>(panel.label, panel.progressBar, () -> getMultiProgressDialog().removePanel(panel.panel));
    }

    private Tuple3<JLabel, JProgressBar, JDialog> createSwingProgressDialog(String title) {
        ProgressPanel panel = new ProgressPanel();

        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setContentPane(panel.panel);
        setupSwingDialog(dialog);

        UIBeautician.comb(panel.panel);

        return new Tuple3<>(panel.label, panel.progressBar, dialog);
    }

    @Override
    public InstallAdapter createInstallationView(String title, String subTitleFormat, InstallAdapter inputAdapter) {
        AtomicBoolean abortInstall = new AtomicBoolean();

        Locale locale = components.get(Translator.class).getLocale();

        // createSwingProgressDialog(locale.get(title))

        Tuple3<JLabel, JProgressBar, Runnable> tuple = createSwingProgressView();
        JPanel panel = (JPanel) tuple.y.getParent();

        titlePanel.setEnabledButtons(false, TitlePanel.PLAY, TitlePanel.VERSIONS);

        tuple.y.setMaximum(100);
        tuple.y.setMinimum(0);

        InstallAdapter adapter = new InstallAdapter();

        final boolean validAdapter = inputAdapter != null;
        final boolean hasTitleConsumer = validAdapter && inputAdapter.titleConsumer != null;
        final boolean hasCancelSupplier = validAdapter && inputAdapter.cancelSupplier != null;
        final boolean hasProgressConsumer = validAdapter && inputAdapter.progressConsumer != null;

        adapter.titleConsumer = name -> {
            tuple.x.setText(String.format(subTitleFormat, name));

            if(hasTitleConsumer) inputAdapter.titleConsumer.accept(name);
        };

        adapter.cancelSupplier = () -> (hasCancelSupplier && inputAdapter.cancelSupplier.get()) || abortInstall.get();

        adapter.onEnd = () -> {
            if(getMultiProgressDialog().removePanel(panel)) {
                closeMultiProgressDialog();
            }
        };

        adapter.onCancel = () -> {
            if(validAdapter && inputAdapter.onCancel != null) inputAdapter.onCancel.run();
        };

        adapter.progressConsumer = progress -> {
            tuple.y.setValue(progress);

            if(hasProgressConsumer) inputAdapter.progressConsumer.accept(progress);
        };

        adapter.progressBarMaxConsumer = tuple.y::setMaximum;

        return adapter;
    }
}