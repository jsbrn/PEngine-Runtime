package world.objects.components.logic.handlers.camera;

import world.Camera;
import world.objects.SceneObject;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetCameraTargetHandler extends BlockHandler {

    double x, y;
    SceneObject o;
    
    @Override
    public void init() {
        if (getParent().inputCount() == 1) {
            o = (SceneObject)getParent().resolveInput(0);
        }
        if (getParent().inputCount() == 2) {
            x = (Double)getParent().resolveInput(0);
            y = (Double)getParent().resolveInput(1);
        }
    }
    
    @Override
    public boolean update() {
        if (getParent().inputCount() == 1) Camera.setTarget(o);
        if (getParent().inputCount() == 2) Camera.setTarget((int)x, (int)y);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
