package world.objects.components.logic.handlers.object;

import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AnchorToHandler extends BlockHandler {

    SceneObject o, o2;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
        o2 = (SceneObject)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        o.anchorTo(o2);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
