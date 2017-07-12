package world.objects.components.logic.handlers.general;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class StartHandler extends BlockHandler {
    
    public boolean update() {
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
