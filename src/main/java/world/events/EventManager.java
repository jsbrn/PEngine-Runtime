package world.events;

import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {

    private static HashMap<String, ArrayList<Event>> events = new HashMap<String, ArrayList<Event>>();
    
    public static void add(String name, Event value) {
        ArrayList<Event> list = get(name);
        if (list == null)  { 
            ArrayList<Event> new_ = new ArrayList<Event>();
            new_.add(value);
            events.put(name, new_);
        } else {
            list.add(value);
        }
    }
    
    public static void remove(String name, Object[] params) {
        ArrayList<Event> l = get(name);
        Event e = get(name, params);
        if (l == null || e == null) return;
        l.remove(e);
    }
    
    public static ArrayList<Event> get(String name) {
        return events.get(name);
    }
    
    public static boolean exists(String key, Object[] params) {
        return get(key, params) != null;
    }
    
    public static Event get(String key, Object[] params) {
        ArrayList<Event> list = get(key);
        if (list == null) return null;
        for (Event e: list) {
            boolean match = true;
            for (Object o: params) {
                if (!e.contains(o)) match = false;
            }
            if (match) return e;
        }
        return null;
    }
    
    public static void clear() { 
        for (ArrayList<Event> list: events.values()) {
            for (int i = list.size()-1; i > -1; i--) {
                Event e = list.get(i);
                if (!e.isPersistent()) list.remove(e);
            }
        }
    }
    
}
