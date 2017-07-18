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
        getFlow().setVar(getParent().resolveOutput(0), !a);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
