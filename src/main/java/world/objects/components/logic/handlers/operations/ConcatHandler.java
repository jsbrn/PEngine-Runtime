package world.objects.components.logic.handlers.operations;

import world.objects.components.logic.Block;
import world.objects.components.logic.handlers.BlockHandler;

public class ConcatHandler extends BlockHandler {

    String a, b;
    
    @Override
    public void init() {
        a = (String)getParent().resolveInput(0);
        b = (String)getParent().resolveInput(1);
    }
    
    @Override
    public boolean update() {
        getFlow().setVar((String)getParent().getOutput(0)[2], a+b);
        getFlow().goTo(getParent().getConn(Block.NODE_OUT));
        return true;
    }
    
    @Override
    public void clean() { a = ""; b = ""; }
    
}
