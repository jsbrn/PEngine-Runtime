package world.objects.components.logic.handlers.events;

import world.events.EventManager;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class IsKeyPressedHandler extends BlockHandler {
    
    @Override
    public void init() {}
    
    @Override
    public void update() {
        if (EventManager.exists("collision", this))
        getFlow().setVar((String)getParent().getOutput(0)[2], a+b);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
    @Override
    public void clean() { a = 0; b = 0; }
    
}
