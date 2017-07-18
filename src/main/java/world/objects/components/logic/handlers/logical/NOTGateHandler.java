package world.objects.components.logic.handlers.logical;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class NOTGateHandler extends BlockHandler {

    boolean a;
    
    @Override
    public void init() {
        a = (Boolean)getParent().resolveInput(0);
    }
    
    @Override
    public boolean update() {
        boolean result = !a;
        getFlow().setVar(getParent().resolveOutput(0), result);
        getFlow().goTo(getParent().getConn(result ? Block.NODE_YES : Block.NODE_NO));
        return true;
    }
    
}
