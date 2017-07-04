package world.objects.components.logic.handlers.events;

import world.events.EventManager;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AwaitKeyReleasedHandler extends BlockHandler {
    
    @Override
    public void init() {}
    
    @Override
    public void update() {
        if (EventManager.exists("collision", new Object[]{this})) {
            getFlow().goTo(getParent().getConn(Block.NODE_YES));
        }
    }
    
}
