package world.objects.components.logic.handlers.object;

import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetPositionHandler extends BlockHandler {

    SceneObject o;
    double x, y;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
        x = (Double)getParent().resolveInput(1);
        y = (Double)getParent().resolveInput(2);
    }
    
    @Override
    public boolean update() {
        o.setWorldX(x);
        o.setWorldY(y);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { x = 0; y = 0; }
    
}
