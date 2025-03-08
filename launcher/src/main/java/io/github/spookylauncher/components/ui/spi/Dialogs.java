package io.github.spookylauncher.components.ui.spi;

import io.github.spookylauncher.advio.InstallAdapter;
import io.github.spookylauncher.util.structures.tuple.Tuple3;

public interface Dialogs {

    Dialog showConfirmDialog(Runnable ok, String title, String message);

    Dialog showConfirmDialog(Runnable ok, String title, String message, Runnable cancel);

    Tuple3<Label, ProgressBar, Dialog> createProgressDialog(String title);

    InstallAdapter createInstallationDialog(String title, String subTitleFormat, InstallAdapter inputAdapter);
}