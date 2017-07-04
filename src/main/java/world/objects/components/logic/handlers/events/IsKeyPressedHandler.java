package world.objects.components.logic.handlers.events;

import world.events.EventManager;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class IsKeyPressedHandler extends BlockHandler {
    
    String c;
    
    @Override
    public void init() {
        c = (String)getParent().resolveInput(0);
    }
    
    @Override
    public void update() {
        if (EventManager.exists("keypress", new Object[]{c.charAt(0)})) {
            getFlow().goTo(getParent().getConn(Block.NODE_YES));
        } else {
            getFlow().goTo(getParent().getConn(Block.NODE_NO));
        }
    }
    
}
