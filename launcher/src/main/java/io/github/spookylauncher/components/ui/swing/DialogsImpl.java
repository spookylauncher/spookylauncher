package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.advio.InstallAdapter;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.ui.spi.*;
import io.github.spookylauncher.components.ui.swing.forms.ConfirmDialog;
import io.github.spookylauncher.components.ui.swing.forms.ProgressPanel;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.structures.tuple.Tuple3;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

class DialogsImpl extends LauncherComponent implements Dialogs {

    private final SwingUIProvider provider;

    private final TitlePanelImpl titlePanel;

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

    private Tuple3<JLabel, JProgressBar, JDialog> createSwingProgressDialog(String title) {
        ProgressPanel panel = new ProgressPanel();

        JDialog dialog = new JDialog();

        dialog.setTitle(title);
        dialog.setContentPane(panel.panel);

        dialog.setResizable(false);
        dialog.setLocationRelativeTo(provider.getFrame());

        dialog.setSize(486, 76);

        dialog.setIconImage(provider.window().getIcon());

        dialog.setVisible(true);

        return new Tuple3<>(panel.label, panel.progressBar, dialog);
    }

    @Override
    public InstallAdapter createInstallationDialog(String title, String subTitleFormat, InstallAdapter inputAdapter) {
        AtomicBoolean abortInstall = new AtomicBoolean();

        Locale locale = components.get(Translator.class).getLocale();

        Tuple3<JLabel, JProgressBar, JDialog> tuple = createSwingProgressDialog(locale.get(title));

        titlePanel.setEnabledButtons(false, TitlePanel.PLAY, TitlePanel.VERSIONS);

        tuple.z.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        tuple.z.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                if(abortInstall.get()) return;

                showConfirmDialog(
                        () -> abortInstall.set(true),

                        locale.get("abortingInstallation"),

                        locale.get("abortingInstallationQuestion")
                );
            }
        });

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

        adapter.closeDialog = () -> {
            tuple.z.setVisible(false);
            tuple.z.dispose();
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