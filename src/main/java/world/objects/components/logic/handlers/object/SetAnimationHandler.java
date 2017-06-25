package world.objects.components.logic.handlers.object;

import world.objects.SceneObject;
import world.objects.components.Animation;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetAnimationHandler extends BlockHandler {
    
    Animation anim;
    SceneObject o;
    
    @Override
    public void init() {
        o = (SceneObject)getParent().resolveInput(0);
        anim = (Animation)getParent().resolveInput(1);
    }
    
    @Override
    public void update() {
        o.setAnimation(anim);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
    @Override
    public void clean() { anim = null; o = null; }
    
}
