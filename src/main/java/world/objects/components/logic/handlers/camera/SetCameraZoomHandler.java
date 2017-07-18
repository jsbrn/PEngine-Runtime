package world.objects.components.logic.handlers.camera;

import world.Camera;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetCameraZoomHandler extends BlockHandler {

    double x;
    
    @Override
    public void init() {
        x = (Double)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        Camera.setZoom((int)x);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
