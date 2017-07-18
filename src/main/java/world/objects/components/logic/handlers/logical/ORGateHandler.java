package world.objects.components.logic.handlers.logical;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class ORGateHandler extends BlockHandler {

    boolean a, b;
    
    @Override
    public void init() {
        a = (Boolean)getParent().resolveInput(0);
        b = (Boolean)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        getFlow().setVar(getParent().resolveOutput(0), a || b);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
