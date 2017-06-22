package world.objects.components.logic.handlers;

import misc.Types;
import world.World;
import world.objects.SceneObject;
import world.objects.components.Animation;
import world.objects.components.logic.Block;

/**
 * TODO: Write Types.toLevel and use that in this class. Don't pass Text as a parameter.
 * @author Jeremy
 */
public class SetAnimationHandler extends BlockHandler {
    
    Animation anim;
    SceneObject o;
    
    @Override
    public void init() {
        o = getParent().getParent().getParent(); //god damn
        anim = Types.toAnimation(World.getWorld().getCurrentLevel(), o, (String)getParent().getInput(0)[2]);
    }
    
    @Override
    public void update() {
        o.setAnimation(anim);
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
}
