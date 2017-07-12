package world.objects.components.logic.handlers.events;

import world.events.EventManager;
import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AwaitCollisionHandler extends BlockHandler {
    
    SceneObject a, b;
    
    @Override
    public void init() {
        a = (SceneObject)getParent().resolveInput(0);
        b = (SceneObject)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        boolean a_b = EventManager.exists("collision", new Object[]{a, b});
        if (a_b) getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return !a_b;
    }
    
}
