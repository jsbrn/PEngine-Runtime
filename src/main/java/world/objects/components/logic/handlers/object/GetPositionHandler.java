package world.objects.components.logic.handlers.object;

import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class GetPositionHandler extends BlockHandler {

    SceneObject o;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        getFlow().setVar(getParent().resolveOutput(0), o.getWorldCoords()[0]);
        getFlow().setVar(getParent().resolveOutput(1), o.getWorldCoords()[1]);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
