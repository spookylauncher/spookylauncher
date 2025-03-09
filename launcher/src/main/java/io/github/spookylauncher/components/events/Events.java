package io.github.spookylauncher.components.events;

import io.github.spookylauncher.components.ComponentsController;
import io.github.spookylauncher.components.ErrorHandler;
import io.github.spookylauncher.components.LauncherComponent;
import io.github.spookylauncher.log.Level;

import java.util.*;

public final class Events extends LauncherComponent {
    public static final String SHUTDOWN = "shutdown";

    private final Map<String, Set<Handler>> events = new HashMap<>();
    private final Stack<String> emitted = new Stack<>();

    public Stack<String> emissionHistory() {
        return emitted;
    }

    public Events(ComponentsController components) {
        super("Events", components);
    }

    public void subscribe(String event, Handler handler) {
        Set<Handler> set;

        if(events.containsKey(event)) set = events.get(event);
        else events.put(event, set = new HashSet<>());

        set.add(handler);
    }

    public void unsubscribe(String event, Handler handler) {
        if(events.containsKey(event)) events.get(event).remove(handler);
    }

    public void unsubscribeAll(String event) {
        if(!events.containsKey(event)) return;

        events.get(event).clear();
    }

    public void emitAndUnsubscribeAll(String event, Object...args) {
        emit(event, args);
        unsubscribeAll(event);
    }

    public void emit(String event, Object...args) {
        if(!events.containsKey(event)) return;

        emitted.push(event);

        for(Handler handler : events.get(event)) {
            try {
                handler.invoke(args);
            } catch(Exception e) {
                log(Level.ERROR, "exception occurred in event handler \"" + handler + "\"");
                components.get(ErrorHandler.class).handleException("error", e);
            }
        }
    }
}