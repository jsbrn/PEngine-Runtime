package world.events;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {

    private static HashMap<String, ArrayList<Event>> events = new HashMap<String, ArrayList<Event>>();
    
    public static void add(String name, Event value) {
        ArrayList<Event> list = get(name);
        if (list == null)  { 
            events.put(name, new ArrayList<Event>());
        } else {
            list.add(value);
        }
    }
    
    public static ArrayList<Event> get(String name) {
        return events.get(name);
    }
    
    public static boolean exists(String key, Object[] params) {
        ArrayList<Event> list = get(key);
        for (Event e: list) {
            boolean match = true;
            for (Object o: params) {
                if (!e.contains(o)) match = false;
            }
            if (match) return true;
        }
        return false;
    }
    
    public static void clear() { events.clear(); }
    
}

class Event {
    
    ArrayList<Object> parameters;

    public Event(Object[] params) {
        this.parameters = new ArrayList<Object>();
        for (Object o: params) add(o);
    }
    
    public void add(Object o) { parameters.add(o); }
    
    public boolean contains(Object o) { return parameters.contains(o); }
    
}
