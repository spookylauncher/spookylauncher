package io.github.spookylauncher.advio;

import java.util.*;

public final class Executor {

    public static int execute(String[] cmd) {
        try {
            return new ProcessBuilder(cmd)
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int execute(List<String> cmd) {
        return execute(cmd.toArray(new String[0]));
    }

    public static int execute(String cmd) {
        try {
            StringTokenizer st = new StringTokenizer(cmd);
            String[] array = new String[st.countTokens()];

            for (int i = 0; st.hasMoreTokens(); i++)
                array[i] = st.nextToken();

            return execute(array);
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}