package io.github.spookylauncher.components.ui.swing;

import io.github.spookylauncher.advio.InstallAdapter;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.components.ui.spi.*;
import io.github.spookylauncher.components.ui.spi.Dialog;
import io.github.spookylauncher.components.ui.spi.Label;
import io.github.spookylauncher.components.ui.swing.forms.ConfirmDialog;
import io.github.spookylauncher.components.ui.swing.forms.MultiProgressDialog;
import io.github.spookylauncher.components.ui.swing.forms.ProgressPanel;
import io.github.spookylauncher.util.Locale;
import io.github.spookylauncher.util.structures.tuple.Tuple2;
import io.github.spookylauncher.util.structures.tuple.Tuple3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

class DialogsImpl extends LauncherComponent implements Dialogs {

    private final SwingUIProvider provider;

    private final TitlePanelImpl titlePanel;

    private final Map<String, JDialog> multiProgressDialogsMap = new HashMap<>();

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
        Tuple2<JLabel, JProgressBar> tuple = createSwingProgressDialog(title);

        Label label;

        return new Tuple3<>(
                label = SPIFactory.getLabel(tuple.x),
                SPIFactory.getProgressBar(tuple.y),
                SPIFactory.getDialog(getMultiProgressDialog("1"), label)); // TODO
    }

    private JDialog getMultiProgressDialog(String name) {
        JDialog dialog = multiProgressDialogsMap.get(name);

        if(dialog == null)
            multiProgressDialogsMap.put(name, dialog = new MultiProgressDialog());

        return dialog;
    }

    private void closeMultiProgressDialog(String name) {
        if(multiProgressDialogsMap.containsKey(name)) {
            JDialog dialog = multiProgressDialogsMap.get(name);
            dialog.setVisible(false);
            dialog.dispose();
            multiProgressDialogsMap.remove(name);
        }
    }

    private Tuple2<JLabel, JProgressBar> createSwingProgressDialog(String title) {
        ProgressPanel panel = new ProgressPanel();

        JDialog dialog = getMultiProgressDialog(null); // TODO;

        dialog.setTitle("");

        ((MultiProgressDialog)dialog).scrollPane.add(panel.panel);

        dialog.setResizable(false);
        dialog.setLocationRelativeTo(provider.getFrame());

        dialog.setSize(486, 500);

        dialog.setIconImage(provider.window().getIcon());

        dialog.setVisible(true);

        return new Tuple2<>(panel.label, panel.progressBar);
    }

    @Override
    public InstallAdapter createInstallationDialog(String title, String subTitleFormat, InstallAdapter inputAdapter) {
        AtomicBoolean abortInstall = new AtomicBoolean();

        Locale locale = components.get(Translator.class).getLocale();

        Tuple2<JLabel, JProgressBar> tuple = createSwingProgressDialog(locale.get(title));

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
            tuple.y.setVisible(false);

            if(tuple.y.getParent() != null) {
                Container parent = tuple.y.getParent();

                parent.remove(tuple.y);

                Component[] comps = parent.getComponents();

                if(comps == null || comps.length == 0)
                    closeMultiProgressDialog(null); // TODO
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