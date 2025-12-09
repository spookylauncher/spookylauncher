package io.github.spookylauncher.components.ui;

import io.github.spookylauncher.io.InstallAdapter;
import io.github.spookylauncher.util.structures.tuple.Tuple3;

public interface Dialogs {

    Dialog showConfirmDialog(Runnable ok, String title, String message);

    Dialog showConfirmDialog(Runnable ok, String title, String message, Runnable cancel);

    Tuple3<Label, ProgressBar, Dialog> createProgressDialog(String title);

    // third argument is function closing the dialog
    Tuple3<Label, ProgressBar, Runnable> createProgressView();

    InstallAdapter createInstallationView(String title, String subTitleFormat, InstallAdapter inputAdapter);
}