package world.events;

import java.util.ArrayList;

public class Event {
    
    ArrayList<Object> parameters;
    boolean persistent;

    public Event(Object[] params, boolean persistent) {
        this.parameters = new ArrayList<Object>();
        this.persistent = persistent;
        for (Object o: params) add(o);
    }

    public boolean isPersistent() {
        return persistent;
    }
    
    public void add(Object o) { parameters.add(o); }
    
    public boolean contains(Object o) { return parameters.contains(o); }
    
}