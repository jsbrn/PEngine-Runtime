package world.objects.components.logic.handlers;

import misc.Types;
import world.World;

/**
 * TODO: Write Types.toLevel and use that in this class. Don't pass Text as a parameter.
 * @author Jeremy
 */
public class SwitchLevelHandler extends BlockHandler {
    
    String id;
    
    @Override
    public void init() {
        id = Types.toString((String)getParent().getInput(0)[2]);
    }
    
    @Override
    public void update() {
        World.getWorld().switchToLevel(id);
    }
    
}
