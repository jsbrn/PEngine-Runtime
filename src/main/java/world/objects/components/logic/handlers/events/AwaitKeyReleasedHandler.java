package world.objects.components.logic.handlers.events;

import world.events.EventManager;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AwaitKeyReleasedHandler extends BlockHandler {
    
    String c;
    
    @Override
    public void init() {
        c = (String)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        boolean e = EventManager.exists("keyrelease", new Object[]{c.charAt(0)});
        if (e) getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return !e;
    }
    
}
