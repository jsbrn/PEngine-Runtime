package world.objects.components.logic.handlers.level;

import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class RemoveObjectHandler extends BlockHandler {

    SceneObject o;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        o.getParent().removeObject(o);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
