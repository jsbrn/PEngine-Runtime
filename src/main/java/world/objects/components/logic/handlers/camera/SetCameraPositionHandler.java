package world.objects.components.logic.handlers.camera;

import world.Camera;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetCameraPositionHandler extends BlockHandler {

    double x, y;
    
    @Override
    public void init() {
        x = (Double)getParent().resolveInput(0);
        y = (Double)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        Camera.setX(x);
        Camera.setY(y);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { x = 0; y = 0; }
    
}
