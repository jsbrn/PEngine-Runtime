package world.objects.components.logic.handlers.level;

import world.Level;
import world.World;
import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SwitchLevelHandler extends BlockHandler {
    
    Level l;
    
    @Override
    public void init() {
        l = (Level)getParent().resolveInput(0);
    }
    
    @Override
    public void update() {
        World.getWorld().switchToLevel(l);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
    @Override
    public void clean() { l = null; }
    
}
