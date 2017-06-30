package world.events;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {

    private static HashMap<String, ArrayList<Object>> events = new HashMap<String, ArrayList<Object>>();
    
    public static void add(String name, Object value) {
        ArrayList<Object> list = get(name);
        if (list == null)  { 
            events.put(name, new ArrayList<Object>());
        } else {
            list.add(value);
        }
    }
    
    public static ArrayList<Object> get(String name) {
        return events.get(name);
    }
    
    public static boolean exists(String key, Object value) {
        ArrayList<Object> list = get(key);
        if (list == null) return false;
        return list.contains(value);
    }
    
    public static void clear() { events.clear(); }
    
}

class Event {
    
    ArrayList<Object> parameters;

    public Event() {
        this.parameters = new ArrayList<Object>();
    }
    
    public boolean contains(Object o) { return parameters.contains(o); }
    
}
