package world.objects.components.logic.handlers.logical;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class ANDGateHandler extends BlockHandler {

    boolean a, b;
    
    @Override
    public void init() {
        a = (Boolean)getParent().resolveInput(0);
        b = (Boolean)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        boolean result = a && b;
        getFlow().setVar(getParent().resolveOutput(0), result);
        getFlow().goTo(getParent().getConn(result ? Block.NODE_YES : Block.NODE_NO));
        return true;
    }
    
}
