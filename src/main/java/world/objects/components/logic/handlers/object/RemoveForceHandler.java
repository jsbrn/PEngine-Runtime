package world.objects.components.logic.handlers.object;

import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class RemoveForceHandler extends BlockHandler {
    
    SceneObject o;
    String name;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
        name = (String)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        o.removeForce(name);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { o = null; name = null; }
    
}
