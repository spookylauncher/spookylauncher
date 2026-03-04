package io.github.spookylauncher.ui.stub;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.ui.Dialog;
import io.github.spookylauncher.ui.Dialogs;
import io.github.spookylauncher.ui.Label;
import io.github.spookylauncher.ui.ProgressBar;
import io.github.spookylauncher.io.InstallAdapter;
import io.github.spookylauncher.util.structures.tuple.Tuple3;

class StubDialogsImpl extends LauncherComponent implements Dialogs {

    StubDialogsImpl(final ComponentsController components) {
        super("Stub Dialogs", components);
    }

    @Override
    public Dialog showConfirmDialog(Runnable ok, String title, String message) {
        return null;
    }

    @Override
    public Dialog showConfirmDialog(
        Runnable ok,
        String title,
        String message,
        Runnable cancel
    ) {
        return null;
    }

    @Override
    public Tuple3<Label, ProgressBar, Dialog> createProgressDialog(
        String title
    ) {
        return null;
    }

    @Override
    public Tuple3<Label, ProgressBar, Runnable> createProgressView() {
        return null;
    }

    @Override
    public InstallAdapter createInstallationView(
        String title,
        String subTitleFormat,
        InstallAdapter inputAdapter
    ) {
        return null;
    }
}
