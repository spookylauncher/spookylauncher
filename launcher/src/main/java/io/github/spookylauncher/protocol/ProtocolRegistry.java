package io.github.spookylauncher.protocol;

import io.github.spookylauncher.advio.OSType;
import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.components.Translator;
import io.github.spookylauncher.util.Locale;
import com.sun.jna.platform.win32.*;

import javax.swing.*;
import java.io.IOException;

@Deprecated
public final class ProtocolRegistry extends LauncherComponent {

    public static final String PROTOCOL_NAME = "spookylaunch";

    public ProtocolRegistry(ComponentsController components) {
        super("Protocol Registry", components);
    }

    @Override
    public void initialize() throws IOException {
        super.initialize();

        RegisterResult result = register();

        Locale locale = components.get(Translator.class).getLocale();

        if(!result.isSuccess() || !result.isAlreadyRegistered()) {
            if(result.isAccessDenied()) JOptionPane.showMessageDialog(null, locale.get("regProtocolAccessDenied"), locale.get("regProtocolFailed"), JOptionPane.ERROR_MESSAGE);
            else if(result.isNotSupported()) JOptionPane.showMessageDialog(null, locale.get("regProtocolIsNotSupported"), locale.get("regProtocolFailed"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRegistered() {
        if (OSType.CURRENT == OSType.WINDOWS) {
            return Advapi32Util.registryKeyExists(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME + "\\shell\\open\\command");
        }

        return false;
    }

    public RegisterResult register() {
        if(isRegistered()) return new RegisterResult(RegisterResult.ALREADY_REGISTERED);

        String exePath;

        if(OSType.CURRENT == OSType.WINDOWS) {
            exePath = System.getenv("APPDATA") + "\\.spookylauncher\\spookylauncher.exe";

            try {
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME);
                Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME, "", "URL:Spooky Launcher Link");
                Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME, "URL Protocol", "");
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME, "shell");
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME, "shell\\open");
                Advapi32Util.registryCreateKey(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME, "shell\\open\\command");
                Advapi32Util.registrySetStringValue(WinReg.HKEY_CLASSES_ROOT, PROTOCOL_NAME + "\\shell\\open\\command", "",
                        "\"" + exePath + "\" \"--viaprotocol\" \"%1\""
                );
            } catch(Win32Exception e) {
                return new RegisterResult(RegisterResult.ACCESS_DENIED);
            }
        } else {
            System.err.println("spookylaunch protocol can't be registered on this platform (" + OSType.CURRENT.toString() + ")");
            return new RegisterResult(RegisterResult.NOT_SUPPORTED);
        }

        return new RegisterResult(RegisterResult.SUCCESS);
    }

    public static class RegisterResult {
        private final int flags;

        public static final int SUCCESS = 0b1, ACCESS_DENIED = 0b10, ALREADY_REGISTERED = 0b100, NOT_SUPPORTED = 0b1000;

        public RegisterResult(final int flags) {
            this.flags = flags;
        }

        public boolean isSuccess() {
            return get(SUCCESS);
        }

        public boolean isAccessDenied() {
            return get(ACCESS_DENIED);
        }

        public boolean isAlreadyRegistered() {
            return get(ALREADY_REGISTERED);
        }

        public boolean isNotSupported() {
            return get(NOT_SUPPORTED);
        }

        private boolean get(final int flag) {
            return (flags & flag) != 0;
        }
    }
}