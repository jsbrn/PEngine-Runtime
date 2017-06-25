package world.objects.components.logic.handlers.object;

import misc.MiscMath;
import world.Force;
import world.objects.SceneObject;
import world.objects.components.Animation;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class AddForceHandler extends BlockHandler {
    
    SceneObject o;
    Force f;
    String name;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
        name = (String)getParent().resolveInput(1);
        double angle = (Double)getParent().resolveInput(2);
        double acc = (Double)getParent().resolveInput(3);
        double mag = (Double)getParent().resolveInput(4);
        int[] offset = MiscMath.getRotatedOffset(0, -100, angle);
        double vel[] = MiscMath.calculateVelocity(offset[0], offset[1]);
        f = new Force(vel[0], vel[1], acc, mag);
    }
    
    @Override
    public void update() {
        o.addForce(name, f);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
    @Override
    public void clean() { o = null; f = null; name = null; }
    
}
