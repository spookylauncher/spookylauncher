package io.github.spookylauncher.components.launch;

import io.github.spookylauncher.util.NumbersComparator;
import io.github.spookylauncher.util.StringUtils;

import java.util.Map;

public final class VMArgumentsSelector {

    public String getVMArgs(Map<String, String> specificVmArgsMap, int currentJavaMajorVersion) {
        String vmArgs = null;

        for(Map.Entry<String, String> entry : specificVmArgsMap.entrySet()) {
            String rawKey = entry.getKey();

            int compareTypeEndIndex = StringUtils.find(rawKey, Character::isDigit);

            String compareType = rawKey.substring(0, compareTypeEndIndex);

            int majorVersionInCondition = Integer.parseInt(rawKey.substring(compareTypeEndIndex + 1));

            if(NumbersComparator.compare(compareType, currentJavaMajorVersion, majorVersionInCondition))
                vmArgs = entry.getValue();
        }

        return vmArgs;
    }
}