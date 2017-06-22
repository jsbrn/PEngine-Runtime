package world.objects.components.logic.handlers;

import world.objects.components.logic.Block;

public class StartHandler extends BlockHandler {
    
    public void update() {
        getParent().getParent().goTo(getParent().getConn(Block.NODE_OUT));
    }
    
}
