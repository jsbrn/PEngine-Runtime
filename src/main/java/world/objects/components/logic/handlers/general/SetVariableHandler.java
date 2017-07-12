package world.objects.components.logic.handlers.general;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class SetVariableHandler extends BlockHandler {
    
    @Override
    public boolean update() {
        getFlow().setVar((String)getParent().getOutput(0)[2], getParent().resolveInput(0));
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
}
