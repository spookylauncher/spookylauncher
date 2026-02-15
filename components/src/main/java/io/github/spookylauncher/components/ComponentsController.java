package io.github.spookylauncher.components;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ComponentsController {
    private static final Logger LOG = Logger.getLogger("components controller");
    private final HashMap<Class<?>, Integer> componentsByClass = new HashMap<>();
    private final HashMap<String, Integer> componentsByName = new HashMap<>();
    private final List<LauncherComponent> components = new ArrayList<>();
    private final List<Integer> initialized = new ArrayList<>();

    private final HashMap<Integer, Set<Function<ComponentsController, Boolean>> > onInitializedEvents = new HashMap<>();

    private int nextComponentId;

    public void addOnInitializedEvent(int componentIndex, Function<ComponentsController, Boolean> consumer) {
        Set<Function<ComponentsController, Boolean>> set;

        if(!onInitializedEvents.containsKey(componentIndex)) onInitializedEvents.put(componentIndex, set = new HashSet<>());
        else set = onInitializedEvents.get(componentIndex);

        set.add(consumer);
    }

    public boolean isInitialized(String name) {
        return isInitialized(components.get(componentsByName.get(name)));
    }

    public <T> boolean isInitialized(Class<T> clazz) {
        return isInitialized(components.get(componentsByClass.get(clazz)));
    }

    public boolean isInitialized(LauncherComponent component) {
        return initialized.contains(getIndex(component));
    }

    public boolean isInitialized(int componentIndex) {
        return initialized.contains(componentIndex);
    }

    public int getIndex(LauncherComponent component) {
        return components.indexOf(component);
    }

    public <T extends LauncherComponent> T get(int index) {
        return (T) components.get(index);
    }

    public <T> T get(Class<T> clazz) {
        return (T) components.get(componentsByClass.get(clazz));
    }

    public <T extends LauncherComponent> T get(String name) { return (T) components.get(componentsByName.get(name)); }

    public List<LauncherComponent> getComponents() {
        return components;
    }

    public int put(LauncherComponent component) {
        return put(component.getClass(), component);
    }

    public int put(Class<?> clazz, LauncherComponent component) {
        componentsByClass.put(clazz, nextComponentId);
        components.add(component);
        return nextComponentId++;
    }

    public int put(String name, LauncherComponent component) {
        componentsByName.put(name, nextComponentId);
        components.add(component);
        return nextComponentId++;
    }

    public void initializeComponents(List<Integer> priority, List<Integer> async) {
        List<Integer> indices = new ArrayList<>(priority);

        for(int i = 0;i < components.size();i++) if(!priority.contains(i)) indices.add(i);

        for(int i : indices) initializeComponent(i, async.contains(i));

        onInitializedEvents.clear();
    }

    private boolean canceled;

    private void initializeComponent(int index, boolean async) {
        if(canceled) return;

        LauncherComponent component = get(index);

        Runnable task = () -> {
            try {
                LOG.info("initializing \"" + component.getName() + "\" launcher component");

                component.initialize();

                initialized.add(index);

                if(onInitializedEvents.containsKey(index)) {
                    Set<Function<ComponentsController, Boolean>> set = onInitializedEvents.get(index);

                    for(Function<ComponentsController, Boolean> consumer : set) {
                        if(consumer.apply(this)) canceled = true;
                        break;
                    }

                    set.clear();
                }
            } catch(Exception e) {
                LOG.severe("failed to initialize \"" + component.getName() + "\" launcher component:");
                LOG.logp(Level.SEVERE, "io.github.spookylauncher.components.ComponentsController", "initializeComponent", "Throw!", e);
            }
        };

        if(async) new Thread(task).start();
        else task.run();
    }
}